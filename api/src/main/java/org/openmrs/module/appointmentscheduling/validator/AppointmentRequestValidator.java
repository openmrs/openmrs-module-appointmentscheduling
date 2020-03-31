package org.openmrs.module.appointmentscheduling.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { AppointmentRequest.class }, order = 50)
public class AppointmentRequestValidator implements Validator {

    /** Log for this class and subclasses */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Determines if the command object being submitted is a valid type
     *
     * @see org.springframework.validation.Validator#supports(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public boolean supports(Class c) {
        return c.equals(AppointmentRequest.class);
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
        AppointmentRequest appointmentRequest = (AppointmentRequest) obj;
        if (appointmentRequest == null) {
            errors.rejectValue("appointmentRequest", "error.general");
        } else {
            ValidationUtils.rejectIfEmpty(errors, "patient", "appointmentscheduling.AppointmentRequest.emptyPatient");
            ValidationUtils.rejectIfEmpty(errors, "appointmentType", "appointmentscheduling.AppointmentRequest.emptyType");
            ValidationUtils.rejectIfEmpty(errors, "requestedOn", "appointmentscheduling.AppointmentRequest.emptyRequestedOn");
            ValidationUtils.rejectIfEmpty(errors, "status", "appointmentscheduling.AppointmentRequest.emptyStatus");

        }
    }

}
