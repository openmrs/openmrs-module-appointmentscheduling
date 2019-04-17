package org.openmrs.module.appointmentscheduling.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Set;

/**
 * Validates attributes on the {@link AppointmentBlock} object.
 */
@Handler(supports = {AppointmentResource.class}, order = 50)
public class AppointmentResourceValidator implements Validator {

    /**
     * Log for this class and subclasses
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class c) {
        return c.equals(AppointmentResource.class);
    }

    /**
     * Checks the form object for any inconsistencies/errors
     *
     * @should pass validation if all required fields have proper values
     * @should fail validation if start date is not before end date
     * @see org.springframework.validation.Validator#validate(java.lang.Object,
     * org.springframework.validation.Errors)
     */

    public void validate(Object obj, Errors errors) {
        AppointmentResource appointmentResource = (AppointmentResource) obj;
        if (appointmentResource == null) {
            errors.rejectValue("appointmentResource", "error.general");
        } else {
            ValidationUtils.rejectIfEmpty(errors, "startDate", "appointmentscheduling.AppointmentResource.emptyStartDate");
            ValidationUtils.rejectIfEmpty(errors, "endDate", "appointmentscheduling.AppointmentResource.emptyEndDate");
            ValidationUtils.rejectIfEmpty(errors, "startTime", "appointmentscheduling.AppointmentResource.emptyStartTime");
            ValidationUtils.rejectIfEmpty(errors, "endTime", "appointmentscheduling.AppointmentResource.emptyEndTime");
            ValidationUtils.rejectIfEmpty(errors, "location", "appointmentscheduling.AppointmentResource.emptyLocation");

            Set<AppointmentType> types = appointmentResource.getTypes();
            if (types == null) {
                ValidationUtils.rejectIfEmpty(errors, "types", "appointmentscheduling.AppointmentResource.emptyTypes");
            }
        }
    }
}