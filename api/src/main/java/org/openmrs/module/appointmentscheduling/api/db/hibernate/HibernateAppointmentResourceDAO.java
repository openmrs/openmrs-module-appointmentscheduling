package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import org.hibernate.Query;
import org.openmrs.Location;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.BlockExcludedDays;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentResourceDAO;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public List<AppointmentResource> getResourceByConstraints(Date startDate, Date endDate, Time startTime, Time endTime, Location location, Date appointmentDate) throws DAOException {
        if (appointmentDate != null && getResourceExcludedDays(appointmentDate, location).contains(new SimpleDateFormat("yyyy-MM-dd").format(appointmentDate))) {
            throw new DAOException("This date is not allowed for booking appointments");
        } else {

            if (location != null) {
                String stringQuery = "SELECT appointmentResource FROM AppointmentResource AS appointmentResource WHERE appointmentResource.voided = 0";

                if (startDate != null)
                    stringQuery += " AND appointmentResource.startDate=:startDate";
                if (endDate != null)
                    stringQuery += " AND appointmentResource.endDate=:endDate";
                if (startTime != null)
                    stringQuery += " AND appointmentResource.startTime=:startTime";
                if (endTime != null)
                    stringQuery += " AND appointmentResource.endTime=:endTime";
                if (location != null)
                    stringQuery += " AND appointmentResource.location=:location";
                if (appointmentDate != null) {
                    if (!getTime(appointmentDate).equals("00:00:00")) {
                        stringQuery += " AND :appointmentTime >= appointmentResource.startTime AND :appointmentTime <= appointmentResource.endTime";
                    }
                }

                Query query = super.sessionFactory.getCurrentSession().createQuery(
                        stringQuery);

                if (startDate != null)
                    query.setParameter("startDate", startDate);
                if (endDate != null)
                    query.setParameter("endDate", endDate);
                if (startTime != null)
                    query.setParameter("startTime", startTime);
                if (endTime != null)
                    query.setParameter("endTime", endTime);
                if (location != null)
                    query.setParameter("location", location);
                if (appointmentDate != null) {
                    if (!getTime(appointmentDate).equals("00:00:00")) {
                        query.setParameter("appointmentTime", getTimeFromDate(appointmentDate));
                    }
                }

                return (List<AppointmentResource>) query.list();

            } else {
                throw new DAOException("location must have a value");
            }
        }
    }

    @Override
    public List<String> getResourceExcludedDays(Date appointmentDate, Location location) throws DAOException {
        if (location != null && appointmentDate != null) {
            String stringQuery = "SELECT DATE_FORMAT(rdays.excluded_date,'%Y-%m-%d') " +
                    "FROM appointmentscheduling_appointment_resource ar, " +
                    "appointmentscheduling_block_excluded_days rdays " +
                    "WHERE rdays.appointment_resource_id = ar.appointment_resource_id " +
                    "AND :appointmentTime >= start_time AND :appointmentTime <= end_time " +
                    "AND ar.location_id = :location AND rdays.voided = 0 ";

            Query query = super.sessionFactory.getCurrentSession().createSQLQuery(
                    stringQuery);

            if (location != null)
                query.setParameter("location", location.getLocationId());
            if (appointmentDate != null) {
                query.setParameter("appointmentTime", getTimeFromDate(appointmentDate));
            }

            List<String> excludedDays = new ArrayList<>();
            List<String> dates = query.list();
            for (String date : dates) {
                BlockExcludedDays days = new BlockExcludedDays();
                excludedDays.add(date);
            }
            return excludedDays;

        } else {
            throw new DAOException("Cannot get excluded days, date and location not provided");
        }
    }

    @Override
    public BlockExcludedDays geyExcludedDayByUuid(String uuid) throws DAOException {
        Query query = super.sessionFactory.getCurrentSession().createSQLQuery(
                "SELECT * from appointmentscheduling_block_excluded_days rdays WHERE rdays.excluded_date = :uuid");

        if (uuid != null)
            query.setParameter("uuid", uuid);
        return (BlockExcludedDays) query.uniqueResult();
    }

    private Time getTimeFromDate(Date date) {
        try {
            return new Time(new SimpleDateFormat("HH:mm:ss").parse(getTime(date)).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTime(Date date) {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }
}