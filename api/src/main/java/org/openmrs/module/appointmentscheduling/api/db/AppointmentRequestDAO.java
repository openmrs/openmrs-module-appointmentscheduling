package org.openmrs.module.appointmentscheduling.api.db;

import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.AppointmentType;

import java.util.List;

public interface AppointmentRequestDAO  extends SingleClassDAO<AppointmentRequest> {


    /**
     * Retrieves Appointments Requests that satisfy the given constraints
     *
     * @param patient - The patient
     * @param type - The appointment type
     * @param provider - The requested provider
     * @param status - The appointment request status
     * @return a list of appointment requests that satisfy the given constraints
     */
    List<AppointmentRequest> getAppointmentRequestsByConstraints(Patient patient, AppointmentType type, Provider provider,
                                                                 AppointmentRequest.AppointmentRequestStatus status) throws APIException;


}
