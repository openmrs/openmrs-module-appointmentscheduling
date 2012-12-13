package org.openmrs.module.appointment.api.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.db.TimeSlotDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateTimeSlotDAO extends HibernateSingleClassDAO implements TimeSlotDAO {
	
	public HibernateTimeSlotDAO() {
		super(TimeSlot.class);
	}
	
	@Override
	public List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return super.sessionFactory.getCurrentSession().createCriteria(Appointment.class).add(
		    Restrictions.eq("timeSlot", timeSlot)).list();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate,
	        Provider provider) throws APIException {
		if (appointmentType == null)
			throw new APIException("Appointment Type can not be null.");
		else if (fromDate != null && !fromDate.before(toDate))
			throw new APIException("fromDate can not be later than toDate");
		else {
			//TODO change this
			return getAll();
		}
	}
}
