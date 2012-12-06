package org.openmrs.module.appointment.api.db.hibernate;

import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.db.TimeSlotDAO;

public class HibernateTimeSlotDAO extends HibernateSingleClassDAO implements TimeSlotDAO {
	
	public HibernateTimeSlotDAO() {
		super(TimeSlot.class);
	}
	
	@Override
	public List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return super.sessionFactory.getCurrentSession().createCriteria(Appointment.class).add(
		    Restrictions.eq("timeSlot", timeSlot)).list();
	}
}
