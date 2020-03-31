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
package org.openmrs.module.appointmentscheduling.web;

import java.beans.PropertyEditorSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.util.StringUtils;

public class AppointmentTypeEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public AppointmentTypeEditor() {
	}
	
	/**
	 * <strong>Should</strong> set using id
	 * <strong>Should</strong> set using uuid
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		AppointmentService as = Context.getService(AppointmentService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(as.getAppointmentType(Integer.valueOf(text)));
			}
			catch (Exception ex) {
				AppointmentType ts = as.getAppointmentTypeByUuid(text);
				setValue(ts);
				if (ts == null) {
					log.error("Error setting text: " + text, ex);
					throw new IllegalArgumentException("AppointmentType not found: " + ex.getMessage());
				}
			}
		} else {
			setValue(null);
		}
	}
	
	public String getAsText() {
		AppointmentType t = (AppointmentType) getValue();
		if (t == null) {
			return "";
		} else {
			return t.getId().toString();
		}
	}
}
