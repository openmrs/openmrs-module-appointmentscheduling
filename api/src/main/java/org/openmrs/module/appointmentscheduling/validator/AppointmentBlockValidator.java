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
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Set;

/**
 * Validates attributes on the {@link AppointmentBlock} object.
 */
@Handler(supports = { AppointmentBlock.class }, order = 50)
public class AppointmentBlockValidator implements Validator {
	
	/** Log for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Determines if the command object being submitted is a valid type
	 * 
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public boolean supports(Class c) {
		return c.equals(AppointmentBlock.class);
	}
	
	/**
	 * Checks the form object for any inconsistencies/errors
	 * 
	 * @see org.springframework.validation.Validator#validate(java.lang.Object,
	 *      org.springframework.validation.Errors)
	 * @should pass validation if all required fields have proper values
	 * @should fail validation if start date is not before end date
	 */
	
	public void validate(Object obj, Errors errors) {
		AppointmentBlock appointmentBlock = (AppointmentBlock) obj;
		if (appointmentBlock == null) {
			errors.rejectValue("appointmentBlock", "error.general");
		} else {
			ValidationUtils.rejectIfEmpty(errors, "startDate", "appointmentscheduling.AppointmentBlock.emptyStartDate");
			ValidationUtils.rejectIfEmpty(errors, "endDate", "appointmentscheduling.AppointmentBlock.emptyEndDate");
			ValidationUtils.rejectIfEmpty(errors, "location", "appointmentscheduling.AppointmentBlock.emptyLocation");

            if (Context.getService(AppointmentService.class).getOverlappingAppointmentBlocks(appointmentBlock).size() > 0) {
				errors.rejectValue("provider", "appointmentscheduling.AppointmentBlock.error.appointmentBlockOverlap");
			}

            Set<AppointmentType> types = appointmentBlock.getTypes();
            if (types == null) {
                ValidationUtils.rejectIfEmpty(errors, "types", "appointmentscheduling.AppointmentBlock.emptyTypes");
            }

            if (appointmentBlock.getId() != null) {   // only test this on appointment blocks that have previously been saved (otherwise will get transient exception)
                for (TimeSlot timeSlot : Context.getService(AppointmentService.class).getTimeSlotsInAppointmentBlock(appointmentBlock)) {
                    for (Appointment appointment : Context.getService(AppointmentService.class).getAppointmentsInTimeSlotThatAreNotCancelled(timeSlot)) {
                        if (!types.contains(appointment.getAppointmentType())) {
                            errors.rejectValue("types", "appointmentscheduling.AppointmentBlock.error.cannotRemoveTypeFromBlockIfAppointmentScheduled");
                        }
                    }
                }
            }
		}
	}
}
