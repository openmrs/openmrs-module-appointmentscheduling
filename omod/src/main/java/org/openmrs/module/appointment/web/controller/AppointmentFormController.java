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

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.validator.AppointmentValidator;
import org.openmrs.module.appointment.web.AppointmentTypeEditor;
import org.openmrs.module.appointment.web.ProviderEditor;
import org.openmrs.module.appointment.web.TimeSlotEditor;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for creating appointments.
 */
@Controller
public class AppointmentFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(TimeSlot.class, new TimeSlotEditor());
		binder.registerCustomEditor(AppointmentType.class, new AppointmentTypeEditor());
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	@RequestMapping(value = "/module/appointment/appointmentForm", method = RequestMethod.GET)
	public void showForm(ModelMap model, HttpServletRequest request) {
		if (Context.isAuthenticated()) {
			model.put("selectedLocation", Context.getUserContext().getLocation());
			if (request.getParameter("patientId") != null) {
				model.put("appointment", getAppointment(null, Integer.parseInt(request.getParameter("patientId"))));
			}
		}
	}
	
	@ModelAttribute("selectedLocation")
	public Location getLocation(@RequestParam(value = "locationId", required = false) Location location) {
		if (location != null)
			return location;
		else
			return null;
	}
	
	@ModelAttribute("appointment")
	public Appointment getAppointment(@RequestParam(value = "appointmentId", required = false) Integer appointmentId,
	        @RequestParam(value = "patientId", required = false) Integer patientId) {
		Appointment appointment = null;
		
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentId != null)
				appointment = as.getAppointment(appointmentId);
		}
		
		if (appointment == null) {
			appointment = new Appointment();
			if (patientId != null)
				appointment.setPatient(Context.getPatientService().getPatient(patientId));
		}
		
		return appointment;
	}
	
	@ModelAttribute("availableTimes")
	public List<TimeSlot> getAvailableTimes(HttpServletRequest request, Appointment appointment,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "providerSelect", required = false) Provider provider,
	        @RequestParam(value = "locationId", required = false) Location location) {
		AppointmentType appointmentType = appointment.getAppointmentType();
		if (appointmentType == null || (fromDate != null && toDate != null && !fromDate.before(toDate)))
			return null;
		
		try {
			List<TimeSlot> availableTimeSlots = Context.getService(AppointmentService.class).getTimeSlotsByConstraints(
			    appointmentType, fromDate, toDate, provider, location);
			TimeSlot currentSelectedSlot = appointment.getTimeSlot();
			//The appointment time slot should be selected from the latest list
			if (currentSelectedSlot != null && !availableTimeSlots.contains(currentSelectedSlot))
				appointment.setTimeSlot(null);
			return availableTimeSlots;
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		List<Provider> providers = Context.getProviderService().getAllProviders(false);
		Collections.sort(providers, new Comparator<Provider>() {
			
			public int compare(Provider pr1, Provider pr2) {
				return pr1.getName().compareTo(pr2.getName());
			}
		});
		return providers;
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		List<AppointmentType> appointmentTypes = Context.getService(AppointmentService.class).getAllAppointmentTypes(false);
		Collections.sort(appointmentTypes, new Comparator<AppointmentType>() {
			
			public int compare(AppointmentType at1, AppointmentType at2) {
				return at1.getName().compareTo(at2.getName());
			}
		});
		return appointmentTypes;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, Appointment appointment, BindingResult result,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate) throws Exception {
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			
			if (request.getParameter("save") != null) {
				new AppointmentValidator().validate(appointment, result);
				
				if (result.hasErrors())
					return null;
				else {
					//TODO: change to enum
					appointment.setStatus("SCHEDULED");
					appointmentService.saveAppointment(appointment);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointment.Appointment.saved");
					return "redirect:appointmentList.list";
				}
			}
			if (request.getParameter("findAvailableTime") != null) {
				if (fromDate != null && toDate != null && !fromDate.before(toDate))
					result.rejectValue("timeSlot", "appointment.Appointment.error.InvalidDateInterval");
			}
		}
		return null;
	}
}
