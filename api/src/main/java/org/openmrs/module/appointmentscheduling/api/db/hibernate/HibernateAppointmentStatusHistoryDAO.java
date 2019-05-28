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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentStatusHistoryDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateAppointmentStatusHistoryDAO extends HibernateSingleClassDAO implements AppointmentStatusHistoryDAO {
	
	public HibernateAppointmentStatusHistoryDAO() {
		super(AppointmentStatusHistory.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAll(AppointmentStatus status) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
		criteria.add(Restrictions.eq("status", status));
		criteria.addOrder(Order.asc("status"));
		return criteria.list();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Date getStartDateOfCurrentStatus(Appointment appointment) {
		String query = "Select Max(endDate) from AppointmentStatusHistory where appointment=:appointment";
		Date endDate = (Date) super.sessionFactory.getCurrentSession().createQuery(query)
		        .setParameter("appointment", appointment).uniqueResult();
		endDate = (endDate == null && appointment != null) ? appointment.getDateCreated() : endDate;
		
		return endDate;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getHistoriesByInterval(Date fromDate, Date endDate, AppointmentStatus status) {
		List<AppointmentStatusHistory> histories = null;
		if (fromDate == null || endDate == null || status == null)
			return histories;
		
		String stringQuery = "Select history from AppointmentStatusHistory AS history WHERE history.startDate >= :fromDate AND history.endDate <= :endDate AND history.status = :status";
		Query query = super.sessionFactory.getCurrentSession().createQuery(stringQuery);
		
		query.setParameter("fromDate", fromDate).setParameter("endDate", endDate).setParameter("status", status);
		
		histories = query.list();
		
		return histories;
		
	}
	
	@Override
	public void purgeHistoryBy(Appointment appointment) {
		String hql = "delete from AppointmentStatusHistory where appointment= :appointment";
		Query query = super.sessionFactory.getCurrentSession().createQuery(hql);
		query.setParameter("appointment", appointment).executeUpdate();
	}

	@Override
	public List<AppointmentStatusHistory> getAppointmentStatusHistories(Appointment appointment) {
		String query = "Select appointmentHistory from AppointmentStatusHistory AS appointmentHistory where appointmentHistory.appointment=:appointment";
		return  (List<AppointmentStatusHistory>) super.sessionFactory.getCurrentSession().createQuery(query)
				.setParameter("appointment", appointment).list();

	}

	@Override
	public AppointmentStatusHistory getMostRecentAppointmentStatusHistory(Appointment appointment) {

		String stringQuery = "Select history from AppointmentStatusHistory AS history  " +
				"WHERE history.startDate = (select max(statusHistory.startDate) from AppointmentStatusHistory AS statusHistory " +
				"WHERE statusHistory.appointment = :appointment)";
		Query query = super.sessionFactory.getCurrentSession().createQuery(stringQuery).setParameter("appointment", appointment);
		return (AppointmentStatusHistory) query.uniqueResult();
	}

}
