package org.openmrs.module.appointment.api.db.hibernate;

import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.db.TimeSlotDAO;

public class HibernateTimeSlotDAO extends HibernateSingleClassDAO implements TimeSlotDAO {
	
	public HibernateTimeSlotDAO() {
		super(TimeSlot.class);
	}
}
