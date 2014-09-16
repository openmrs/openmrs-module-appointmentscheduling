package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentRequestDAO;

public class HibernateAppointmentRequestDAO extends HibernateSingleClassDAO<AppointmentRequest> implements AppointmentRequestDAO{

    public HibernateAppointmentRequestDAO() {
        super(AppointmentRequest.class);
    }

}
