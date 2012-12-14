/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.appointment.api.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.db.AppointmentDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateAppointmentDAO extends HibernateSingleClassDAO implements AppointmentDAO {
	
	public HibernateAppointmentDAO() {
		super(Appointment.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByPatient(Patient patient) {
		return super.sessionFactory.getCurrentSession().createCriteria(Appointment.class).add(
		    Restrictions.eq("patient", patient)).list();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getAppointmentByVisit(Visit visit) {
		return (Appointment) super.sessionFactory.getCurrentSession().createQuery(
		    "from " + mappedClass.getSimpleName() + " at where at.visit = :visit").setParameter("visit", visit)
		        .uniqueResult();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getLastAppointment(Patient patient) {
		String query = "select appointment from Appointment as appointment"
		        + " where appointment.patient = :patient and appointment.timeSlot.startDate ="
		        + " (select max(timeSlot.startDate) from Appointment as appointment inner join appointment.timeSlot"
		        + " where appointment.patient = :patient)";
		
		List<Appointment> appointment = super.sessionFactory.getCurrentSession().createQuery(query).setParameter("patient",
		    patient).list();
		
		if (appointment.size() > 0)
			return (Appointment) appointment.get(0);
		else
			return null;
	}
}
