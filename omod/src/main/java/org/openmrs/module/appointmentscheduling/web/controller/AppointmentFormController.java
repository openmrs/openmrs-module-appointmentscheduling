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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.validator.AppointmentValidator;
import org.openmrs.module.appointmentscheduling.web.AppointmentTypeEditor;
import org.openmrs.module.appointmentscheduling.web.ProviderEditor;
import org.openmrs.module.appointmentscheduling.web.TimeSlotEditor;
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
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentForm", method = RequestMethod.GET)
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
	public List<TimeSlot> getAvailableTimes(ModelMap model, HttpServletRequest request, Appointment appointment,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "providerSelect", required = false) Provider provider,
	        @RequestParam(value = "locationId", required = false) Location location,
	        @RequestParam(value = "includeFull", required = false) String includeFull,
	        @RequestParam(value = "flow", required = false) String flow) {
		AppointmentType appointmentType = appointment.getAppointmentType();
		if (appointmentType == null || (fromDate != null && toDate != null && !fromDate.before(toDate)))
			return null;
		//If its a walk-in flow change the start date to current time and end date to the end of today (23:59:59.999)
		if (flow != null) {
			fromDate = Calendar.getInstance().getTime();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			toDate = cal.getTime();
		}
		
		try {
			List<TimeSlot> availableTimeSlots = null;
			
			//No need to include full slots
			if (includeFull == null || !Context.hasPrivilege(AppointmentUtils.PRIV_SQUEEZE_APPOINTMENTS)) {
				availableTimeSlots = Context.getService(AppointmentService.class).getTimeSlotsByConstraints(appointmentType,
				    fromDate, toDate, provider, location);
				TimeSlot currentSelectedSlot = appointment.getTimeSlot();
				//The appointment time slot should be selected from the latest list
				if (currentSelectedSlot != null && !availableTimeSlots.contains(currentSelectedSlot))
					appointment.setTimeSlot(null);
			}
			
			//Include full and indicate which are full using the model attribute fullSlots
			else {
				availableTimeSlots = Context.getService(AppointmentService.class).getTimeSlotsByConstraintsIncludingFull(
				    appointmentType, fromDate, toDate, provider, location);
				List<TimeSlot> fullTimeSlots = new LinkedList<TimeSlot>();
				Map<Integer, Integer> overdueTimes = new HashMap<Integer, Integer>();
				
				Integer typeDuration = appointmentType.getDuration();
				
				Iterator<TimeSlot> iterator = availableTimeSlots.iterator();
				while (iterator.hasNext()) {
					TimeSlot slot = iterator.next();
					Integer timeLeft = Context.getService(AppointmentService.class).getTimeLeftInTimeSlot(slot);
					if (timeLeft < typeDuration) {
						fullTimeSlots.add(slot);
						overdueTimes.put(slot.getId(), timeLeft);
						iterator.remove();
					}
				}
				
				model.put("fullSlots", fullTimeSlots);
				model.put("overdueTimes", overdueTimes);
			}
			
			return availableTimeSlots;
		}
		catch (Exception ex) {
			return null;
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
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentForm", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, Appointment appointment, BindingResult result,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "flow", required = false) String flow,
	        @RequestParam(value = "origin", required = false) String origin) throws Exception {
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			
			if (request.getParameter("save") != null) {
				new AppointmentValidator().validate(appointment, result);
				
				if (result.hasErrors())
					return null;
				else {
					appointment.setDateCreated(new Date());
					if (flow != null) {
						appointment.setStatus(AppointmentStatus.WALKIN);
						//Start a new visit
						String visitTypeIdString = Context.getAdministrationService().getGlobalProperty(
						    AppointmentUtils.GP_DEFAULT_VISIT_TYPE);
						Integer visitTypeId = Integer.parseInt(visitTypeIdString);
						VisitType defaultVisitType = Context.getVisitService().getVisitType(visitTypeId);
						
						Visit visit = new Visit(appointment.getPatient(), defaultVisitType, new Date());
						visit.setLocation(appointment.getTimeSlot().getAppointmentBlock().getLocation());
						visit = Context.getVisitService().saveVisit(visit);
						appointment.setVisit(visit);
					} else
						appointment.setStatus(AppointmentStatus.SCHEDULED);
					appointmentService.saveAppointment(appointment);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointmentscheduling.Appointment.saved");
					//Check whether to redirect to appointments manage form (origin=null) or to patientDashboard (origin=dashboard)
					if (origin == null)
						return "redirect:appointmentList.list";
					else if (origin.equals("dashboard"))
						return "redirect:/patientDashboard.form?patientId=" + appointment.getPatient().getId().toString();
				}
			}
			if (request.getParameter("findAvailableTime") != null) {
				if (fromDate != null && toDate != null && !fromDate.before(toDate))
					result.rejectValue("timeSlot", "appointmentscheduling.Appointment.error.InvalidDateInterval");
			}
		}
		return null;
	}
}
