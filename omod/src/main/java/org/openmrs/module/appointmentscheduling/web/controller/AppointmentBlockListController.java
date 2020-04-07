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
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.util.OpenmrsUtil;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for listing appointment types.
 */
@Controller
public class AppointmentBlockListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockList", method = RequestMethod.GET)
	public void showForm(HttpServletRequest request, ModelMap model) throws ParseException {
		//Initializing the default properties of the appointment block list page.
		if (Context.isAuthenticated()) {
			//Set the location from the session
			if (request.getSession().getAttribute("chosenLocation") != null) {
				Location location = (Location) request.getSession().getAttribute("chosenLocation");
				model.addAttribute("chosenLocation", location);
			}
			//Set the provider from the session
			if (request.getSession().getAttribute("chosenProvider") != null) {
				Provider provider = Context.getProviderService().getProvider(
				    (Integer) request.getSession().getAttribute("chosenProvider"));
				model.addAttribute("chosenProvider", provider.getProviderId());
			}
			//Set the appointmentType from the session
			if (request.getSession().getAttribute("chosenType") != null) {
				AppointmentType appointmentType = Context.getService(AppointmentService.class).getAppointmentType(
				    (Integer) request.getSession().getAttribute("chosenType"));
				model.addAttribute("chosenType", appointmentType.getAppointmentTypeId());
			}
			//Set the date interval from the session
			String fromDate;
			String toDate;
			
			fromDate = (String) request.getSession().getAttribute("fromDate");
			toDate = (String) request.getSession().getAttribute("toDate");
			Calendar cal = Context.getDateTimeFormat().getCalendar();
			if (fromDate == null && toDate == null) {
				//In case the user loaded the page for the first time, we will set to default the time interval (1 week from today).
				fromDate = Context.getDateTimeFormat().format(OpenmrsUtil.firstSecondOfDay(new Date()));
				cal.setTime(OpenmrsUtil.getLastMomentOfDay(new Date()));
				cal.add(Calendar.DAY_OF_MONTH, 6);
				toDate = Context.getDateTimeFormat().format(cal.getTime());
			} else {
				//Session is not empty and we need to change the locale if we have to.
				Locale lastLocale = (Locale) request.getSession().getAttribute("lastLocale");
				Locale currentLocale = Context.getLocale();
				//check if the last locale equals to the current locale
				if (lastLocale != null && lastLocale.toString().compareTo(currentLocale.toString()) != 0) {
					//if the locals are different 
					fromDate = Context.getDateTimeFormat().format(OpenmrsUtil.getDateTimeFormat(lastLocale).parse(fromDate));
					toDate = Context.getDateTimeFormat().format(OpenmrsUtil.getDateTimeFormat(lastLocale).parse(toDate));
				}
			}
			//Update session variables - this will be updated in every locale change.
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute("fromDate", fromDate);
			httpSession.setAttribute("toDate", toDate);
			httpSession.setAttribute("lastLocale", Context.getLocale());
			
			//Update model variables - what the page shows.
			model.addAttribute("fromDate", fromDate);
			model.addAttribute("toDate", toDate);
		}
		
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getService(AppointmentService.class).getAllProvidersSorted(false);
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypesSorted(false);
	}
	
	@ModelAttribute("chosenLocation")
	public Location getLocation(@RequestParam(value = "locationId", required = false) Location location) {
		if (location != null)
			return location;
		else
			return null;
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockList", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, ModelMap model,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "locationId", required = false) Location location,
	        @RequestParam(value = "chosenType", required = false) Integer appointmentTypeId,
	        @RequestParam(value = "chosenProvider", required = false) Integer providerId,
	        @RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId,
	        @RequestParam(value = "action", required = false) String action) throws Exception {
		AppointmentBlock appointmentBlock = null;
		if (Context.isAuthenticated()) {
			HttpSession httpSession = request.getSession();
			if (!fromDate.before(toDate)) {
				httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
				    "appointmentscheduling.AppointmentBlock.error.InvalidDateInterval");
			}
			// save details from the appointment block list page using http session
			httpSession.setAttribute("chosenLocation", location);
			httpSession.setAttribute("fromDate", Context.getDateTimeFormat().format(fromDate).toString());
			httpSession.setAttribute("toDate", Context.getDateTimeFormat().format(toDate).toString());
			httpSession.setAttribute("lastLocale", Context.getLocale());
			httpSession.setAttribute("chosenProvider", providerId);
			httpSession.setAttribute("chosenType", appointmentTypeId);
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			//if the user is adding a new AppointmentBlock
			if (request.getParameter("add") != null) {
				return "redirect:appointmentBlockForm.form" + "?redirectedFrom=appointmentBlockList.list";
			}
			//if the user is editing an existing AppointmentBlock
			else if (request.getParameter("edit") != null) {
				if (appointmentBlockId != null) {
					return "redirect:appointmentBlockForm.form?appointmentBlockId=" + appointmentBlockId
					        + "&redirectedFrom=appointmentBlockList.list";
				} else {
					//In case appointment block was not selected
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					    "appointmentscheduling.AppointmentBlock.error.selectAppointmentBlock");
					return null;
				}
			}
			//if the user is changing to calendar view
			else if (action != null && action.equals("changeToCalendarView")) {
				return "redirect:appointmentBlockCalendar.list";
			}
			//in case appointment block was not selected
			else if (appointmentBlockId == null) {
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentBlock.error.selectAppointmentBlock");
				return null;
			} else { //retrieve the selected appointment block from the data base
				appointmentBlock = appointmentService.getAppointmentBlock(appointmentBlockId);
				
			}
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
				
				return "redirect:appointmentBlockList.list";
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
				return "redirect:appointmentBlockList.list";
				
			}
			
			// if the user is unvoiding the AppointmentBlock
			else if (request.getParameter("unvoid") != null) {
				List<TimeSlot> currentTimeSlots = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock);
				List<Appointment> appointmentsThatShouldBeUnvoided = new ArrayList<Appointment>();
				for (TimeSlot timeSlot : currentTimeSlots) {
					List<Appointment> currentAppointments = appointmentService.getAppointmentsInTimeSlot(timeSlot);
					for (Appointment appointment : currentAppointments) {
						if (!appointmentsThatShouldBeUnvoided.contains(appointment))
							appointmentsThatShouldBeUnvoided.add(appointment);
					}
				}
				//unvoiding the appointment block
				appointmentService.unvoidAppointmentBlock(appointmentBlock);
				//unvoiding the appointments
				for (Appointment appointment : appointmentsThatShouldBeUnvoided) {
					appointmentService.unvoidAppointment(appointment);
				}
				//unvoiding the time slots
				for (TimeSlot timeSlot : currentTimeSlots) {
					appointmentService.unvoidTimeSlot(timeSlot);
				}
				
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentBlock.unvoidedSuccessfully");
				
				return "redirect:appointmentBlockList.list";
			}
		} // Context authentication.
		return null;
	}
}
