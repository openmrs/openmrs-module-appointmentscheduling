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
package org.openmrs.module.appointmentscheduling.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates attributes on the {@link AppointmentType} object.
 */
@Handler(supports = { AppointmentType.class }, order = 50)
public class AppointmentTypeValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

    @Autowired
    @Qualifier("appointmentService")
    private AppointmentService appointmentService;
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(AppointmentType.class);
	}

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should fail validation if name is null or empty or whitespace
	 * @should pass validation if all required fields have proper values
	 */
	public void validate(Object obj, Errors errors) {
		AppointmentType appointmentType = (AppointmentType) obj;
		if (appointmentType == null) {
			errors.rejectValue("appointmentType", "error.general");
		} else {
			ValidationUtils.rejectIfEmpty(errors, "duration", "appointmentscheduling.AppointmentType.durationEmpty");
            validateFieldName(errors, appointmentType);
		}
	}

    private void validateFieldName(Errors errors, AppointmentType appointmentType) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "error.name");
        if (appointmentService.verifyDuplicatedAppointmentTypeName(appointmentType)) {
            errors.rejectValue("name", "appointmentscheduling.AppointmentType.nameDuplicated");
        }
    }

}
