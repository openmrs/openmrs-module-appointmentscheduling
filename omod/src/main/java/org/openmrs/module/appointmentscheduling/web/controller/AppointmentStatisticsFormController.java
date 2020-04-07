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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for editing appointment types.
 */
@Controller
public class AppointmentStatisticsFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentStatisticsForm", method = RequestMethod.GET)
	public void showForm() {
		
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentStatisticsForm", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, AppointmentType appointmentType, BindingResult result)
	        throws Exception {
		
		return "";
	}
}
