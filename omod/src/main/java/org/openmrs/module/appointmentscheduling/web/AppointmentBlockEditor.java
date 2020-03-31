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
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.util.StringUtils;

public class AppointmentBlockEditor extends PropertyEditorSupport {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public AppointmentBlockEditor() {
	}
	
	/**
	 * <strong>Should</strong> set using id
	 * <strong>Should</strong> set using uuid
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		AppointmentService as = Context.getService(AppointmentService.class);
		if (StringUtils.hasText(text)) {
			try {
				setValue(as.getAppointmentBlock(Integer.valueOf(text)));
			}
			catch (Exception ex) {
				AppointmentBlock ab = as.getAppointmentBlockByUuid(text);
				setValue(ab);
				if (ab == null) {
					log.error("Error setting text: " + text, ex);
					throw new IllegalArgumentException("AppointmentBlock not found: " + ex.getMessage());
				}
			}
		} else {
			setValue(null);
		}
	}
	
	public String getAsText() {
		AppointmentBlock a = (AppointmentBlock) getValue();
		if (a == null) {
			return "";
		} else {
			return a.getAppointmentBlockId().toString();
		}
	}
}
