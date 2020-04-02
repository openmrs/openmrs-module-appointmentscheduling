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
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.TimeSlot;

/**
 * Validates attributes on the {@link TimeSlot} object.
 */
@Handler(supports = { TimeSlot.class }, order = 50)
public class TimeSlotValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(TimeSlot.class);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * <strong>Should</strong> fail validation if name is null or empty or whitespace
	 * <strong>Should</strong> pass validation if all required fields have proper values
	 */
	
	public void validate(Object obj, Errors errors) {
		TimeSlot timeSlot = (TimeSlot) obj;
		if (timeSlot == null) {
			errors.rejectValue("timeSlot", "error.general");
		} else {
			ValidationUtils.rejectIfEmpty(errors, "startDate", "appointmentscheduling.TimeSlot.emptyStartDate");
			ValidationUtils.rejectIfEmpty(errors, "endDate", "appointmentscheduling.TimeSlot.emptyEndDate");
			ValidationUtils.rejectIfEmpty(errors, "appointmentBlock", "appointmentscheduling.TimeSlot.emptyBlock");
		}
	}
}
