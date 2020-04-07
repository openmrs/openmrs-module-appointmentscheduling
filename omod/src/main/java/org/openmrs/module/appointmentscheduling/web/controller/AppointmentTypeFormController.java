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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.validator.ValidateUtil;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for editing appointment types.
 */
@Controller
public class AppointmentTypeFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentTypeForm", method = RequestMethod.GET)
	public void showForm() {
		
	}
	
	@ModelAttribute("appointmentType")
	public AppointmentType getAppointmentType(
	        @RequestParam(value = "appointmentTypeId", required = false) Integer appointmentTypeId) {
		AppointmentType appointmentType = null;
		
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentTypeId != null)
				appointmentType = as.getAppointmentType(appointmentTypeId);
		}
		
		if (appointmentType == null)
			appointmentType = new AppointmentType();
		
		return appointmentType;
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentTypeForm", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, AppointmentType appointmentType, BindingResult result)
	        throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			
			if (request.getParameter("save") != null) {
				ValidateUtil.validate(appointmentType, result);
				if (result.hasErrors()) {
					return null;
				} else {
					appointmentService.saveAppointmentType(appointmentType);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR, "appointmentscheduling.AppointmentType.saved");
				}
			}
			
			// if the user is retiring out the AppointmentType
			else if (request.getParameter("retire") != null) {
				String retireReason = request.getParameter("retireReason");
				if (appointmentType.getAppointmentTypeId() != null && !(StringUtils.hasText(retireReason))) {
					result.reject("retireReason", "general.retiredReason.empty");
					return null;
				}
				
				appointmentService.retireAppointmentType(appointmentType, retireReason);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentType.retiredSuccessfully");
			}
			
			// if the user is unretiring the AppointmentType
			else if (request.getParameter("unretire") != null) {
				appointmentService.unretireAppointmentType(appointmentType);
				httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
				    "appointmentscheduling.AppointmentType.unretiredSuccessfully");
			}
			
			// if the user is purging the appointmentType
			else if (request.getParameter("purge") != null) {
				
				try {
					appointmentService.purgeAppointmentType(appointmentType);
					httpSession.setAttribute(WebConstants.OPENMRS_MSG_ATTR,
					    "appointmentscheduling.AppointmentType.purgedSuccessfully");
				}
				catch (DataIntegrityViolationException e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.object.inuse.cannot.purge");
					//view = "appointmentType.form?appointmentTypeId=" + appointmentType.getAppointmentTypeId();
				}
				catch (APIException e) {
					httpSession.setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "error.general: " + e.getLocalizedMessage());
					//view = "appointmentType.form?appointmentTypeId=" + appointmentType.getAppointmentTypeId();
				}
			}
			
		}
		
		return "redirect:appointmentTypeList.list";
	}
}
