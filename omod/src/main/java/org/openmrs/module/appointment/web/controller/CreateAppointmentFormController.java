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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for editing appointment types.
 */
@Controller
public class CreateAppointmentFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointment/createAppointmentForm", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
		//default empty Object
		Set<AppointmentType> appointmentTypeList = new HashSet<AppointmentType>();
		List<Provider> providerList = new LinkedList<Provider>();
		
		//only fill the Object is the user has authenticated properly
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			appointmentTypeList = appointmentService.getAllAppointmentTypes();
			providerList = Context.getProviderService().getAllProviders();
		}
		
		model.addAttribute("appointmentTypeList", appointmentTypeList);
		model.addAttribute("providerList", providerList);
	}
	
}
