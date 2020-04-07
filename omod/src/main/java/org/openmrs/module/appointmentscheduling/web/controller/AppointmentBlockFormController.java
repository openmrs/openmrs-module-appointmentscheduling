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
package org.openmrs.module.appointmentscheduling.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.validator.AppointmentBlockValidator;
import org.openmrs.module.appointmentscheduling.web.AppointmentBlockEditor;
import org.openmrs.module.appointmentscheduling.web.AppointmentTypeEditor;
import org.openmrs.module.appointmentscheduling.web.ProviderEditor;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
		binder.registerCustomEditor(AppointmentBlock.class, new AppointmentBlockEditor());
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockForm", method = RequestMethod.GET)
	public void showForm(ModelMap model, HttpServletRequest request) throws ParseException {
		if (Context.isAuthenticated()) {
			if (request.getParameter("appointmentBlockId") != null) {
				AppointmentBlock appointmentBlock = getAppointmentBlock(Integer.parseInt(request
				        .getParameter("appointmentBlockId")));
				model.put("appointmentBlock", appointmentBlock);
				model.put("timeSlotLength", getTimeSlotLength(appointmentBlock, null));
			}
			if (request.getParameter("redirectedFrom") != null) {
				String redirectedFrom = (String) request.getParameter("redirectedFrom");
				if (redirectedFrom.equals("appointmentBlockCalendar.list")) {
					if (request.getParameter("fromDate") != null) {
						//Update model attribute appointment block
						AppointmentBlock appointmentBlock = new AppointmentBlock();
						Date startDate = Context.getDateTimeFormat().parse((String) request.getParameter("fromDate"));
						appointmentBlock.setStartDate(startDate);
						if (request.getParameter("toDate") != null) {
							Date endDate = Context.getDateTimeFormat().parse((String) request.getParameter("toDate"));
							appointmentBlock.setEndDate(endDate);
						}
						model.put("appointmentBlock", appointmentBlock);
					}
				}
				model.put("redirectedFrom", redirectedFrom);
			}
		}
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
	public String getTimeSlotLength(AppointmentBlock appointmentBlock,
	        @RequestParam(value = "timeSlotLength", required = false) String timeSlotLength) {
		if (timeSlotLength == null) {
			if (Context.isAuthenticated()) {
				AppointmentService as = Context.getService(AppointmentService.class);
				//If the AppointmentBlock is being Edited than load its slot duration.
				if (appointmentBlock.getCreator() != null) {
					TimeSlot timeSlot = Context.getService(AppointmentService.class)
					        .getTimeSlotsInAppointmentBlock(appointmentBlock).get(0);
					return (timeSlot.getEndDate().getTime() - timeSlot.getStartDate().getTime()) / 60000 + "";
				}
				//Else display the default slot length, defined by the global property 'defaultTimeSlotDuration'
				else {
					String defaultTimeSlotLength = (Context.getAdministrationService()
					        .getGlobalProperty(AppointmentUtils.GP_DEFAULT_TIME_SLOT_DURATION));
					
					return defaultTimeSlotLength;
				}
			}
		} else
			return timeSlotLength;
		
		return "";
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getProviderService().getAllProviders();
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		AppointmentService appointmentService = Context.getService(AppointmentService.class);
		return appointmentService.getAllAppointmentTypesSorted(false);
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockForm", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, ModelMap model, AppointmentBlock appointmentBlock,
	        BindingResult result, @RequestParam(value = "timeSlotLength", required = false) String timeSlotLength,
	        @RequestParam(value = "emptyTypes", required = false) String emptyTypes,
	        @RequestParam(value = "redirectedFrom", required = false) String redirectedFrom,
	        @RequestParam(value = "action", required = false) String action) throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			if (emptyTypes.equals("yes")) {
				//need to nullify the appointment types.
				appointmentBlock.setTypes(null);
			}
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			//if the user is voiding the selected appointment block
			if (action != null && action.equals("void")) {
				String voidReason = "Some Reason";//request.getParameter("voidReason");
				if (!(StringUtils.hasText(voidReason))) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
					    "appointmentscheduling.AppointmentBlock.error.voidReasonEmpty");
					return null;
				}
				List<TimeSlot> currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
				List<Appointment> appointments = new ArrayList<Appointment>();
				for (TimeSlot timeSlot : currentTimeSlots) {
					List<Appointment> appointmentsInSlot = appointmentService.getAppointmentsInTimeSlot(timeSlot);
					for (Appointment appointment : appointmentsInSlot) {
						appointments.add(appointment);
					}
				}
				//set appointments statuses from "Scheduled" to "Cancelled".
				for (Appointment appointment : appointments) {
					if (appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.SCHEDULED.toString())) {
						appointmentService.changeAppointmentStatus(appointment, AppointmentStatus.CANCELLED);
					}
				}
				//voiding appointment block
				appointmentService.voidAppointmentBlock(appointmentBlock, voidReason);
				//voiding time slots
				for (TimeSlot timeSlot : currentTimeSlots) {
					appointmentService.voidTimeSlot(timeSlot, voidReason);
					
				}
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentBlock.voidedSuccessfully");
			}
			//If the user is purging the AppointmentBlock
			else if (action != null && action.equals("purge")) {
				List<TimeSlot> currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
				//In case there are appointments within the appointment block we don't mind to purge it
				//purging the appointment block
				try {
					//purging the time slots
					for (TimeSlot timeSlot : currentTimeSlots) {
						appointmentService.purgeTimeSlot(timeSlot);
					}
					appointmentService.purgeAppointmentBlock(appointmentBlock);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					    "appointmentscheduling.AppointmentBlock.purgedSuccessfully");
				}
				catch (DataIntegrityViolationException e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.object.inuse.cannot.purge");
				}
				catch (APIException e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.general: " + e.getLocalizedMessage());
				}
				
			} else if (request.getParameter("save") != null) {
				new AppointmentBlockValidator().validate(appointmentBlock, result);
				if (result.hasErrors()) {
					return null;
				} else {
					//Error checking
					if (appointmentBlock.getStartDate().before(Calendar.getInstance().getTime())) {
						result.rejectValue("startDate", "appointmentscheduling.AppointmentBlock.error.dateCannotBeInThePast");
						return null;
					}
					if (!appointmentBlock.getStartDate().before(appointmentBlock.getEndDate())) {
						result.rejectValue("endDate", "appointmentscheduling.AppointmentBlock.error.InvalidDateInterval");
						return null;
					}
					if (timeSlotLength.isEmpty() || Integer.parseInt(timeSlotLength) <= 0) {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						    "appointmentscheduling.AppointmentBlock.error.selectTimeSlot");
						return null;
					}
					long appointmentBlocklengthInMinutes = (appointmentBlock.getEndDate().getTime() - appointmentBlock
					        .getStartDate().getTime()) / 60000;
					if (!timeSlotLength.isEmpty() && (Integer.parseInt(timeSlotLength) > appointmentBlocklengthInMinutes)) {
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						    "appointmentscheduling.AppointmentBlock.error.maximalTimeSlot");
						return null;
					}
					List<TimeSlot> currentTimeSlots = null;
					//if the appointment block is already created and now is being updated
					if (appointmentBlock.getCreator() != null) {
						boolean canBeUpdated = true;
						currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
						for (TimeSlot timeSlot : currentTimeSlots) {
							if (appointmentService.getAppointmentsInTimeSlot(timeSlot).size() > 0) {
								canBeUpdated = false;
								break;
							}
						}
						if (!canBeUpdated) {
							httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
							    "appointmentscheduling.AppointmentBlock.error.appointmentsExist");
							return null;
						}
					}
					//Check if overlapping appointment blocks exist in the system(We will consider Time And Provider only)
					if (appointmentService.getOverlappingAppointmentBlocks(appointmentBlock).size() > 0) { //Overlapping exists
						httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
						    "appointmentscheduling.AppointmentBlock.error.appointmentBlockOverlap");
						return null;
					}
					//First we need to save the appointment block (before creating the time slot)
					appointmentService.saveAppointmentBlock(appointmentBlock);
					//Create the time slots.
					Integer slotLength = Integer.parseInt(timeSlotLength);
					int howManyTimeSlotsToCreate = (int) (appointmentBlocklengthInMinutes / slotLength);
					if (currentTimeSlots == null) {
						currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
					}
					if (currentTimeSlots.size() != howManyTimeSlotsToCreate) { //the time slot length changed therefore we need to update.
						//First we will purge the current time slots.
						for (TimeSlot timeSlot : currentTimeSlots) {
							appointmentService.purgeTimeSlot(timeSlot);
						}
						//Then we will add the new time slots corresponding to the new time slot length 
						Date startDate = appointmentBlock.getStartDate();
						Date endDate = null;
						Calendar cal;
						//Create the time slots except the last one because it might be larger from the rest.
						for (int i = 0; i < howManyTimeSlotsToCreate - 1; i++) {
							cal = Context.getDateTimeFormat().getCalendar();
							cal.setTime(startDate);
							cal.add(Calendar.MINUTE, slotLength); // add slotLength minutes
							endDate = cal.getTime();
							TimeSlot timeSlot = new TimeSlot(appointmentBlock, startDate, endDate);
							startDate = endDate;
							appointmentService.saveTimeSlot(timeSlot);
						}
						//Create the last time slot that can be larger than the predefined time slot length.
						TimeSlot timeSlot = new TimeSlot(appointmentBlock, startDate, appointmentBlock.getEndDate());
						appointmentService.saveTimeSlot(timeSlot);
					}
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointmentscheduling.AppointmentBlock.saved");
				}
			}
			
			// if the user is unvoiding the AppointmentBlock
			else if (request.getParameter("unvoid") != null) {
				appointmentService.unvoidAppointmentBlock(appointmentBlock);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentBlock.unvoidedSuccessfully");
			}
			
		}
		return "redirect:" + redirectedFrom;
	}
}
