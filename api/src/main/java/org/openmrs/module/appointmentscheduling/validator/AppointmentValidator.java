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
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates attributes on the {@link Appointment} object.
 */
@Handler(supports = { Appointment.class }, order = 50)
public class AppointmentValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(Appointment.class);
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
		Appointment appointment = (Appointment) obj;
		if (appointment == null) {
			errors.rejectValue("appointment", "error.general");
		} else {
			ValidationUtils.rejectIfEmpty(errors, "timeSlot", "appointmentscheduling.Appointment.emptyTimeSlot");
			ValidationUtils.rejectIfEmpty(errors, "patient", "appointmentscheduling.Appointment.emptyPatient");
			ValidationUtils.rejectIfEmpty(errors, "appointmentType", "appointmentscheduling.Appointment.emptyType");
			
			//Check whether the slot supports this appointment type
			AppointmentType type = appointment.getAppointmentType();
			if (type == null)
				errors.rejectValue("appointmentType", "appointmentscheduling.Appointment.emptyType");
			if (appointment.getTimeSlot() == null)
				errors.rejectValue("timeSlot", "appointmentscheduling.Appointment.emptyTimeSlot");
			else if (type != null && !appointment.getTimeSlot().getAppointmentBlock().getTypes().contains(type))
				errors.rejectValue("appointmentType", "appointmentscheduling.Appointment.notSupportedType");
		}
	}
}
