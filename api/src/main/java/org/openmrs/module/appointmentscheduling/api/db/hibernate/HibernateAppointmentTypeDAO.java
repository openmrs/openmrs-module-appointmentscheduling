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
package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentTypeDAO;

public class HibernateAppointmentTypeDAO extends HibernateSingleClassDAO implements AppointmentTypeDAO {
	
	public HibernateAppointmentTypeDAO() {
		super(AppointmentType.class);
	}
	
	public Integer getAppointmentTypeCount(Date fromDate, Date endDate, AppointmentType type) {
		String stringQuery = "SELECT count(*) As counter FROM Appointment AS appointment" + " WHERE appointment.voided = 0 "
		        + "AND appointment.timeSlot.startDate >= :fromDate " + "AND appointment.timeSlot.endDate <= :endDate "
		        + "AND appointment.appointmentType=:appointmentType";
		
		Query query = super.sessionFactory.getCurrentSession().createQuery(stringQuery).setParameter("fromDate", fromDate)
		        .setParameter("endDate", endDate).setParameter("appointmentType", type);
		
		return ((Long) query.uniqueResult()).intValue();
	}
}
