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

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.openmrs.module.appointment.web.TimeSlotEditor;
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
	}
	
	@RequestMapping(value = "/module/appointment/appointmentForm", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
		
	}
	
	@ModelAttribute("availableTimes")
	public List<TimeSlot> getAvailableTimes(@RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "location", required = false) Location location) {
		if (fromDate != null)
			return Context.getService(AppointmentService.class).getAllTimeSlots();
		return null;
	}
	
	@ModelAttribute("appointment")
	public Appointment getAppointment(@RequestParam(value = "appointmentId", required = false) Integer appointmentId) {
		Appointment appointment = null;
		
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentId != null)
				appointment = as.getAppointment(appointmentId);
		}
		
		if (appointment == null)
			appointment = new Appointment();
		
		return appointment;
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getProviderService().getAllProviders();
	}
	
	@ModelAttribute("appointmentTypeList")
	public Set<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypes();
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, Appointment appointment, BindingResult result) throws Exception {
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			
			if (request.getParameter("save") != null) {
				new AppointmentValidator().validate(appointment, result);
			}
			if (result.hasErrors())
				return null;
		}
		return "";
	}
	
	@RequestMapping(params = "findAvailableTime", method = RequestMethod.POST)
	public void onFindTimesClick(ModelMap model, Appointment appointment,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "location", required = false) Location location) throws Exception {
		
		if (Context.isAuthenticated()) {
			if (fromDate == null)
				getAvailableTimes(new Date(), toDate, location);
			else
				getAvailableTimes(fromDate, toDate, location);
		}
	}
}
