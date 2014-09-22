package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentRequestDAO;

import java.util.List;

public class HibernateAppointmentRequestDAO extends HibernateSingleClassDAO<AppointmentRequest> implements AppointmentRequestDAO{

    public HibernateAppointmentRequestDAO() {
        super(AppointmentRequest.class);
    }

    public List<AppointmentRequest> getAppointmentRequestsByConstraints(Patient patient, AppointmentType type, Provider provider,
                                                                 AppointmentRequest.AppointmentRequestStatus status) throws APIException {

        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentRequest.class);

        if (patient != null) {
            criteria.add(Restrictions.eq("patient", patient));
        }

        if (type != null) {
            criteria.add(Restrictions.eq("appointmentType", type));
        }

        if (provider != null) {
            criteria.add(Restrictions.eq("provider", provider));
        }

        if (status != null) {
            criteria.add(Restrictions.eq("status", status));
        }

        // always exclude voided
        criteria.add(Restrictions.eq("voided", Boolean.FALSE));

        return criteria.list();
    }

}
