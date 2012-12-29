/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.appointment.web.controller;

import java.beans.PropertyEditor;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.validator.AppointmentBlockValidator;
import org.openmrs.module.appointment.web.AppointmentTypeEditor;
import org.openmrs.module.appointment.web.ProviderEditor;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for editing appointment types.
 */
@Controller
public class AppointmentBlockFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(AppointmentType.class, new AppointmentTypeEditor());
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	@RequestMapping(value = "/module/appointment/appointmentBlockForm", method = RequestMethod.GET)
	public void showForm() {
		
	}
	
	@ModelAttribute("appointmentBlock")
	public AppointmentBlock getAppointmentBlock(
	        @RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId) {
		AppointmentBlock appointmentBlock = null;
		
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentBlockId != null)
				appointmentBlock = as.getAppointmentBlock(appointmentBlockId);
		}
		
		if (appointmentBlock == null)
			appointmentBlock = new AppointmentBlock();
		
		return appointmentBlock;
	}
	
	@ModelAttribute("timeSlotLength")
	public String getTimeSlotLength(@RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId) {
		if (appointmentBlockId == null)
			return "";
		else {
			if (Context.isAuthenticated()) {
				AppointmentService as = Context.getService(AppointmentService.class);
				AppointmentBlock appointmentBlock = as.getAppointmentBlock(appointmentBlockId);
				TimeSlot timeSlot = Context.getService(AppointmentService.class).getTimeSlotsInAppointmentBlock(
				    appointmentBlock).get(0);
				return (timeSlot.getEndDate().getTime() - timeSlot.getStartDate().getTime()) / 60000 + "";
			}
		}
		return "";
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getProviderService().getAllProviders();
	}
	
	@ModelAttribute("appointmentTypeList")
	public Set<AppointmentType> getAppointmentTypeList(
	        @RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId) {
		Set<AppointmentType> allTypes = Context.getService(AppointmentService.class).getAllAppointmentTypes();
		if (appointmentBlockId != null) {
			Set<AppointmentType> toShow = new HashSet<AppointmentType>();
			Set<AppointmentType> currentTypes = Context.getService(AppointmentService.class).getAppointmentBlock(
			    appointmentBlockId).getTypes();
			for (AppointmentType appointmentType : allTypes) {
				if (!currentTypes.contains(appointmentType)) {
					toShow.add(appointmentType);
				}
			}
			return toShow;
		} else
			return allTypes;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, AppointmentBlock appointmentBlock, BindingResult result,
	        @RequestParam(value = "timeSlotLength", required = false) String timeSlotLength) throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			if (request.getParameter("save") != null) {
				new AppointmentBlockValidator().validate(appointmentBlock, result);
				if (result.hasErrors()) {
					return null;
				} else {
					//Error checking
					if (!appointmentBlock.getStartDate().before(appointmentBlock.getEndDate())) {
						result.rejectValue("endDate", "appointment.AppointmentBlock.error.InvalidDateInterval");
						return null;
					}
					if (timeSlotLength.isEmpty() || Integer.parseInt(timeSlotLength) <= 0) {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						    "appointment.AppointmentBlock.error.selectTimeSlot");
						return null;
					}
					long appointmentBlocklengthInMinutes = (appointmentBlock.getEndDate().getTime() - appointmentBlock
					        .getStartDate().getTime()) / 60000;
					if (!timeSlotLength.isEmpty() && (Integer.parseInt(timeSlotLength) > appointmentBlocklengthInMinutes)) {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						    "appointment.AppointmentBlock.error.maximalTimeSlot");
						return null;
					}
					
					//First we need to save the appointment block (before creating the time slot
					appointmentService.saveAppointmentBlock(appointmentBlock);
					//Create the time slots.
					Integer slotLength = Integer.parseInt(timeSlotLength);
					int howManyTimeSlotsToCreate = (int) (appointmentBlocklengthInMinutes / slotLength);
					List<TimeSlot> currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
					if (currentTimeSlots.size() != howManyTimeSlotsToCreate) { //the time slot length changed therefore we need to update.
						//First we will purge the current time slots.
						for (TimeSlot timeSlot : currentTimeSlots) {
							appointmentService.purgeTimeSlot(timeSlot);
						}
						//Then we will add the new time slots corresponding to the new time slot length 
						Date startDate = appointmentBlock.getStartDate();
						Date endDate = null;
						Calendar cal;
						for (int i = 0; i < howManyTimeSlotsToCreate; i++) {
							cal = Context.getDateTimeFormat().getCalendar();
							cal.setTime(startDate);
							cal.add(Calendar.MINUTE, slotLength); // add slotLength minutes
							endDate = cal.getTime();
							TimeSlot timeSlot = new TimeSlot(appointmentBlock, startDate, endDate);
							startDate = endDate;
							appointmentService.saveTimeSlot(timeSlot);
						}
						//fill the lost time with one time slot if there is any.
						if (startDate.before(appointmentBlock.getEndDate())) {
							TimeSlot timeSlot = new TimeSlot(appointmentBlock, startDate, appointmentBlock.getEndDate());
							appointmentService.saveTimeSlot(timeSlot);
						}
					}
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointment.AppointmentBlock.saved");
				}
			}

			// if the user is unvoiding the AppointmentBlock
			else if (request.getParameter("unvoid") != null) {
				appointmentService.unvoidAppointmentBlock(appointmentBlock);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointment.AppointmentBlock.unvoidedSuccessfully");
			}
			
		}
		
		return "redirect:appointmentBlockList.list";
	}
}
