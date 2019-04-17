package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.db.TimeSlotDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateTimeSlotDAO extends HibernateSingleClassDAO implements TimeSlotDAO {
	
	public HibernateTimeSlotDAO() {
		super(TimeSlot.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate,
	        Provider provider) throws APIException {

        if (appointmentType == null) {
			throw new APIException("Appointment Type can not be null.");
        }
        else if (fromDate != null && toDate != null && !fromDate.before(toDate)) {
			throw new APIException("fromDate can not be later than toDate");
        }
		else {
			Date startDate = (fromDate == null) ? new Date() : fromDate;
			
			String stringQuery = "SELECT timeSlot FROM TimeSlot AS timeSlot WHERE timeSlot.appointmentBlock IN("
			        + " FROM AppointmentBlock WHERE :appointmentType IN elements(types)) AND voided = 0 AND endDate > :startDate";

			if (toDate != null) {
				stringQuery += " AND endDate <= :endDate";
            }

			if (provider != null) {
				stringQuery += " AND timeSlot.appointmentBlock.provider = :provider";
            }

			stringQuery += " ORDER BY startDate";
			
			Query query = super.sessionFactory.getCurrentSession().createQuery(stringQuery)
			        .setParameter("appointmentType", appointmentType).setParameter("startDate", startDate);
			
			if (toDate != null) {
				query.setParameter("endDate", toDate);
            }

			if (provider != null) {
				query.setParameter("provider", provider);
            }

			return query.list();
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByAppointmentBlock(AppointmentBlock appointmentBlock) {
		if (appointmentBlock == null)
			return new Vector<TimeSlot>();
		return super.sessionFactory.getCurrentSession().createCriteria(TimeSlot.class)
		        .add(Restrictions.eq("appointmentBlock", appointmentBlock)).add(Restrictions.eq("voided", false)).list();
	}

	@Override
	public TimeSlot getResourceTimeslot(Date date, Location location) throws DAOException {
		String stringQuery = "SELECT timeslot FROM TimeSlot AS timeslot WHERE timeslot.voided = 0";

		if (location != null)
			stringQuery += " AND timeslot.appointmentBlock.location=:location";

		if (date != null) {
			if (!getStringTime(date).equals("00:00:00")) {
				stringQuery += " AND :appointmentTime >= TIME(timeslot.startDate) AND :appointmentTime <=  TIME(timeslot.endDate)  ";
			}
		}
		if (date != null) {
			stringQuery += " AND DATE_FORMAT(timeslot.startDate,'%Y-%m-%d') = :startDate";
		}

		Query query = super.sessionFactory.getCurrentSession().createQuery(
				stringQuery);

		if (location != null)
			query.setParameter("location", location);

		if (date != null) {
			if (!getStringTime(date).equals("00:00:00"))
				query.setParameter("appointmentTime", getTimeFromDate(date));
		}

		if (date != null)
			query.setParameter("startDate", new SimpleDateFormat("yyyy-MM-dd").format(date));

		if (query.list().size() > 1) {
			throw new DAOException("Multiple timeslots have been set for that day. Specify appointment time.");
		} else {
			return (TimeSlot) query.uniqueResult();
		}
	}

	private Time getTimeFromDate(Date date) {
		try {
			return new Time(new SimpleDateFormat("HH:mm:ss").parse(getStringTime(date)).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getStringTime(Date date) {
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
	
}
