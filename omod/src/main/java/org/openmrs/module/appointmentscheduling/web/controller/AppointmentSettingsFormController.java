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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.GlobalProperty;
import org.openmrs.PersonAttributeType;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for manipulating module's settings.
 */
@Controller
public class AppointmentSettingsFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentSettingsForm", method = RequestMethod.GET)
	public void showForm(ModelMap model, HttpServletRequest request) {
		if (Context.isAuthenticated()) {
			
		}
	}
	
	//
	@ModelAttribute("visitTypeList")
	public List<VisitType> getVisitTypeList(@RequestParam(value = "visitTypeList", required = false) List<VisitType> types) {
		if (types != null)
			return types;
		
		types = new LinkedList<VisitType>();
		for (VisitType type : Context.getVisitService().getAllVisitTypes()) {
			if (!type.isRetired())
				types.add(type);
		}
		
		return types;
	}
	
	@ModelAttribute("selectedVisitType")
	public VisitType getSelectedVisitType(@RequestParam(value = "visitTypeSelect", required = false) VisitType type) {
		if (type != null)
			return type;
		
		type = Context.getVisitService().getVisitType(
		    Integer.parseInt(Context.getAdministrationService().getGlobalProperty(AppointmentUtils.GP_DEFAULT_VISIT_TYPE)));
		return type;
	}
	
	@ModelAttribute("personAttributeList")
	public List<PersonAttributeType> gePersonAtttributeList(
	        @RequestParam(value = "personAttributeList", required = false) List<PersonAttributeType> attributes) {
		if (attributes != null)
			return attributes;
		
		attributes = Context.getPersonService().getAllPersonAttributeTypes(false);
		
		return attributes;
	}
	
	@ModelAttribute("selectedPersonAttributeType")
	public PersonAttributeType getSelectedPersonAttributeType(
	        @RequestParam(value = "personAttributeTypeSelect", required = false) PersonAttributeType type) {
		if (type != null)
			return type;
		
		type = Context.getPersonService()
		        .getPersonAttributeType(
		            Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
		                AppointmentUtils.GP_PATIENT_PHONE_NUMBER)));
		return type;
	}
	
	@ModelAttribute("refreshInterval")
	public String getRefreshInterval(@RequestParam(value = "refreshDelayInput", required = false) String interval) {
		
		interval = (interval != null) ? interval : Context.getAdministrationService().getGlobalProperty(
		    AppointmentUtils.GP_DEFAULT_APPOINTMENTS_MANAGE_REFRESH);
		
		try {
			Integer.parseInt(interval);
		}
		catch (NumberFormatException ex) {
			interval = "";
		}
		
		return interval.toString();
	}
	
	@ModelAttribute("hideEndVisit")
	public String getHideEndVisit(@RequestParam(value = "hideEndVisit", required = false) String value) {
		
		value = (value != null) ? value : Context.getAdministrationService().getGlobalProperty(
		    AppointmentUtils.GP_HIDE_END_VISITS_BUTTONS);
		
		return value;
	}
	
	@ModelAttribute("slotDuration")
	public String getDefaultSlotDuration(@RequestParam(value = "slotDurationInput", required = false) String duration) {
		duration = (duration != null) ? duration : Context.getAdministrationService().getGlobalProperty(
		    AppointmentUtils.GP_DEFAULT_TIME_SLOT_DURATION);
		
		try {
			Integer.parseInt(duration);
		}
		catch (NumberFormatException ex) {
			duration = "";
		}
		
		return duration.toString();
	}

	@ModelAttribute("cleanOpenAppointmentScheduler")
	public String getDefaultCleanOpenAppointmentsValue(@RequestParam(value = "cleanOpenAppointmentScheduler", required = false) String cleanOpenAppointmentScheduler) {
		cleanOpenAppointmentScheduler = (cleanOpenAppointmentScheduler != null) ? cleanOpenAppointmentScheduler : Context.getAdministrationService().getGlobalProperty(
				AppointmentUtils.GP_CLEAN_OPEN_APPOINTMENTS);

		return cleanOpenAppointmentScheduler;
	}

	//
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentSettingsForm", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request,
	        @RequestParam(value = "visitTypeSelect", required = true) VisitType visitType,
	        @RequestParam(value = "personAttributeTypeSelect", required = true) PersonAttributeType attributeType,
	        @RequestParam(value = "slotDurationInput", required = true) String duration,
	        @RequestParam(value = "refreshDelayInput", required = false) String delay,
	        @RequestParam(value = "refreshRadio", required = true) String radio,
	        @RequestParam(value = "hideEndVisit", required = true) String hide,
		    @RequestParam(value = "cleanOpenAppointmentScheduler", required = true) String cleanOpenAppointmentScheduler) throws Exception {
		
		Integer slotDuration = null;
		Integer refreshDelay = null;
		try {
			slotDuration = Integer.parseInt(duration);
			if (Integer.parseInt(radio) > 0) {
				refreshDelay = Integer.parseInt(delay);
			} else {
				refreshDelay = -1;
			}
		}
		catch (NumberFormatException ex) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
			    "appointmentscheduling.Appointment.settings.error.time");
			return null;
		}
		if (Integer.parseInt(radio) > 0 && refreshDelay < 60) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
			    "appointmentscheduling.Appointment.settings.error.minRefreshInterval");
			return null;
		}
		
		//Update GPs
		if (request.getParameter("save") != null) {
			GlobalProperty visitTypeGP = Context.getAdministrationService().getGlobalPropertyObject(
			    AppointmentUtils.GP_DEFAULT_VISIT_TYPE);
			GlobalProperty phoneAttributeGP = Context.getAdministrationService().getGlobalPropertyObject(
			    AppointmentUtils.GP_PATIENT_PHONE_NUMBER);
			GlobalProperty refreshIntervalGP = Context.getAdministrationService().getGlobalPropertyObject(
			    AppointmentUtils.GP_DEFAULT_APPOINTMENTS_MANAGE_REFRESH);
			GlobalProperty slotDurationGP = Context.getAdministrationService().getGlobalPropertyObject(
			    AppointmentUtils.GP_DEFAULT_TIME_SLOT_DURATION);
			GlobalProperty hideEndVisitGP = Context.getAdministrationService().getGlobalPropertyObject(
			    AppointmentUtils.GP_HIDE_END_VISITS_BUTTONS);
			GlobalProperty cleanOpenAppointmentsValueGP = Context.getAdministrationService().getGlobalPropertyObject(
					AppointmentUtils.GP_CLEAN_OPEN_APPOINTMENTS);
			
			visitTypeGP.setPropertyValue(visitType.getId().toString());
			phoneAttributeGP.setPropertyValue(attributeType.getId().toString());
			refreshIntervalGP.setPropertyValue(refreshDelay.toString());
			slotDurationGP.setPropertyValue(slotDuration.toString());
			hideEndVisitGP.setPropertyValue(hide);
			cleanOpenAppointmentsValueGP.setPropertyValue(cleanOpenAppointmentScheduler);

			Context.getAdministrationService().saveGlobalProperty(visitTypeGP);
			Context.getAdministrationService().saveGlobalProperty(phoneAttributeGP);
			Context.getAdministrationService().saveGlobalProperty(refreshIntervalGP);
			Context.getAdministrationService().saveGlobalProperty(slotDurationGP);
			Context.getAdministrationService().saveGlobalProperty(hideEndVisitGP);
			Context.getAdministrationService().saveGlobalProperty(cleanOpenAppointmentsValueGP);

			request.getSession().setAttribute(WebConstants.OPENMRS_MSG_ATTR,
			    "appointmentscheduling.Appointment.settings.notification.saved");
			return "redirect:/admin/index.htm";
		}
		
		return null;
	}
}
