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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.PatientAppointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.web.controller.PortletController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Controller for editing appointment types.
 */
@Controller
public class AppointmentsPortletController extends PortletController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@Override
	protected void populateModel(HttpServletRequest request, Map<String, Object> model) {
		int patientId = ServletRequestUtils.getIntParameter(request, "patientId", 0);
		List<PatientAppointment> patientAppointments = null;
		if (Context.isAuthenticated()) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			if (patient != null) {
				patientAppointments = appointmentService.getAppointmentsOfPatient(patient);
			}
		}
		model.put("appointmentList", patientAppointments);
		model.put("patientId", patientId);
	}
	
}
