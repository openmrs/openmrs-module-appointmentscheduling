package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import org.hibernate.Query;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentResourceDAO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HibernateAppointmentResourceDAO extends HibernateSingleClassDAO
        implements AppointmentResourceDAO {

    /**
     * You must call this before using any of the data access methods, since it's not actually
     * possible to write them all with compile-time class information.
     *
     * @param
     */
    public HibernateAppointmentResourceDAO() {
        super(AppointmentResource.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResource> getResourceByConstraints(Location location, Provider provider, Date appointmentDate) throws DAOException {

        if (location != null) {
            String stringQuery = "SELECT appointmentResource FROM AppointmentResource AS appointmentResource WHERE appointmentResource.voided = 0";

            if (location != null)
                stringQuery += " AND appointmentResource.location=:location";
            if (provider != null)
                stringQuery += " AND appointmentResource.provider=:provider";
            if (appointmentDate != null) {
                if (!new SimpleDateFormat("HH:mm:ss").format(appointmentDate).equals("00:00:00")) {
                    stringQuery += " AND :appointmentTime >= appointmentResource.startTime AND :appointmentTime <= appointmentResource.endTime";
                }
            }
            Query query = super.sessionFactory.getCurrentSession().createQuery(
                    stringQuery);

            if (location != null)
                query.setParameter("location", location);
            if (provider != null)
                query.setParameter("provider", provider);
            if (appointmentDate != null) {
                if (!new SimpleDateFormat("HH:mm:ss").format(appointmentDate).equals("00:00:00")) {
                    query.setParameter("appointmentTime", getTimeFromDate(appointmentDate));
                }
            }

            return (List<AppointmentResource>) query.list();

        } else {
            throw new DAOException("location must have a value");
        }
    }

    private Time getTimeFromDate(Date date) {
        try {
            return new Time(new SimpleDateFormat("HH:mm:ss")
                    .parse(new SimpleDateFormat("HH:mm:ss").format(date)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}