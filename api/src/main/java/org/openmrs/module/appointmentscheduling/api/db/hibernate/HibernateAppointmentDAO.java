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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentDailyCount;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentDAO;
import org.springframework.transaction.annotation.Transactional;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus.CANCELLED;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus.MISSED;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus.RESCHEDULED;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus.SCHEDULED;

public class HibernateAppointmentDAO extends HibernateSingleClassDAO
		implements
			AppointmentDAO {

	public HibernateAppointmentDAO() {
		super(Appointment.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByPatient(Patient patient) {
		return super.sessionFactory.getCurrentSession()
				.createCriteria(Appointment.class)
				.add(Restrictions.eq("patient", patient)).list();
	}

	@Override
	@Transactional(readOnly = true)
	public Appointment getAppointmentByVisit(Visit visit) {
		return (Appointment) super.sessionFactory
				.getCurrentSession()
				.createQuery(
						"from " + mappedClass.getSimpleName()
								+ " at where at.visit = :visit")
				.setParameter("visit", visit).uniqueResult();
	}

	@Override
	@Transactional(readOnly = true)
	public Appointment getLastAppointment(Patient patient) {
		String query = "select appointment from Appointment as appointment"
				+ " where appointment.patient = :patient and appointment.timeSlot.startDate ="
				+ " (select max(ap.timeSlot.startDate) from Appointment as ap inner join ap.timeSlot"
				+ " where ap.patient = :patient)";

		List<Appointment> appointment = super.sessionFactory
				.getCurrentSession().createQuery(query)
				.setParameter("patient", patient).list();

		if (appointment.size() > 0)
			return (Appointment) appointment.get(0);
		else
			return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByConstraints(Date fromDate,
		  Date toDate, Provider provider, AppointmentType appointmentType,
		  List<AppointmentStatus> statuses, Patient patient, VisitType visitType, Visit visit)
			throws APIException {
		if (fromDate != null && toDate != null && !fromDate.before(toDate))
			throw new APIException("fromDate can not be later than toDate");

		else {
			String stringQuery = "SELECT appointment FROM Appointment AS appointment WHERE appointment.voided = false";

			if (fromDate != null)
				stringQuery += " AND appointment.timeSlot.startDate >= :fromDate";
			if (toDate != null)
				stringQuery += " AND appointment.timeSlot.endDate <= :endDate";
			if (provider != null)
				stringQuery += " AND appointment.timeSlot.appointmentBlock.provider = :provider";
			if (statuses != null && statuses.size() > 0)
				stringQuery += " AND appointment.status IN (:statuses)";
			if (visitType != null)
				stringQuery += " AND appointment.appointmentType.visitType = :visitType";
			if (visit != null)
				stringQuery += " AND appointment.visit = :visit";
			if (appointmentType != null)
				stringQuery += " AND appointment.appointmentType=:appointmentType";
			if (patient != null) {
				stringQuery += " AND appointment.patient=:patient";
			}

            stringQuery += " ORDER BY appointment.timeSlot.startDate";

			Query query = super.sessionFactory.getCurrentSession().createQuery(
					stringQuery);

			if (fromDate != null)
				query.setParameter("fromDate", fromDate);
			if (toDate != null)
				query.setParameter("endDate", toDate);
			if (provider != null)
				query.setParameter("provider", provider);
			if (statuses != null && statuses.size() > 0)
				query.setParameterList("statuses", statuses);
			if (visitType != null)
				query.setParameter("visitType", visitType);
			if (visit != null)
				query.setParameter("visit", visit);
			if (appointmentType != null)
				query.setParameter("appointmentType", appointmentType);
			if (patient != null)
				query.setParameter("patient", patient);

			return query.list();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByConstraints(Date fromDate,
			Date toDate, Provider provider, AppointmentType appointmentType,
			AppointmentStatus status, Patient patient) throws APIException {
		return getAppointmentsByConstraints(fromDate, toDate, provider,
				appointmentType, Arrays.asList(status), patient, null, null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByStates(
			List<AppointmentStatus> states) {
		String sQuery = "from Appointment as appointment where appointment.voided = false and appointment.status in (:states)";

		Query query = super.sessionFactory.getCurrentSession().createQuery(
				sQuery);
		query.setParameterList("states", states);

		return query.list();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getPastAppointmentsByStates(
			List<AppointmentStatus> states) {
		String sQuery = "from Appointment as appointment where appointment.timeSlot.endDate <= :endDate and appointment.voided = false and appointment.status in (:states)";

		Query query = super.sessionFactory.getCurrentSession().createQuery(
				sQuery);
		query.setParameterList("states", states);
		query.setParameter("endDate", Calendar.getInstance().getTime());

		return query.list();
	}

	@Override
	public List<Appointment> getScheduledAppointmentsForPatient(Patient patient) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				mappedClass);
		criteria.add(Restrictions.eq("patient", patient));
		criteria.add(Restrictions.or(Restrictions.eq("status", SCHEDULED),
				Restrictions.eq("status", RESCHEDULED)));
		criteria.add(Restrictions.eq("voided", false));
		criteria.createAlias("timeSlot", "timeSlot");
		criteria.addOrder(Order.asc("timeSlot.startDate"));

		return criteria.list();
	}

	@Override
	public List<Appointment> getAppointmentsByAppointmentBlockAndAppointmentTypes(
			AppointmentBlock appointmentBlock,
			List<AppointmentType> appointmentTypes) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				mappedClass);
		criteria.createAlias("timeSlot", "time_slot");
		criteria.add(Restrictions.eq("time_slot.appointmentBlock",
				appointmentBlock));

		if (appointmentTypes != null)
			criteria.add(Restrictions.in("appointmentType", appointmentTypes));
		criteria.add(Restrictions.eq("voided", false));

		return criteria.list();
	}

	@Override
	public List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return createAppointmentsInTimeSlotCriteria(timeSlot).list();
	}

	@Override
	public List<Appointment> getAppointmentsInTimeSlotByStatus(
			TimeSlot timeSlot, List<AppointmentStatus> statuses) {
		return createAppointmentsInTimeSlotByStatusCriteria(timeSlot, statuses)
				.list();

	}

	@Override
	public Integer getCountOfAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return ((Number) createAppointmentsInTimeSlotCriteria(timeSlot)
				.setProjection(Projections.rowCount()).uniqueResult())
				.intValue();
	}

	@Override
	public Integer getCountOfAppointmentsInTimeSlotByStatus(TimeSlot timeSlot,
			List<AppointmentStatus> statuses) {
		return ((Number) createAppointmentsInTimeSlotByStatusCriteria(timeSlot,
				statuses).setProjection(Projections.rowCount()).uniqueResult())
				.intValue();
	}

	private Criteria createAppointmentsInTimeSlotCriteria(TimeSlot timeSlot) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Appointment.class);
		criteria.add(Restrictions.eq("timeSlot", timeSlot));
		criteria.add(Restrictions.eq("voided", false));
		return criteria;
	}

	private Criteria createAppointmentsInTimeSlotByStatusCriteria(
			TimeSlot timeSlot, List<AppointmentStatus> statuses) {
		Criteria criteria = createAppointmentsInTimeSlotCriteria(timeSlot);
		criteria.add(Restrictions.in("status", statuses));
		return criteria;
	}

	@Override
	public List<AppointmentDailyCount> getAppointmentDailyCount(String fromDate, String toDate, Location location,
																Provider provider, AppointmentStatus status) throws DAOException {
		String stringQuery = "SELECT count(appointment_id), date_format(ts.start_date,'%Y-%m-%d') as monthDate   "
				+ "FROM appointmentscheduling_appointment `ap`   "
				+ "LEFT JOIN  appointmentscheduling_time_slot `ts` ON (ap.time_slot_id = ts.time_slot_id)  "
				+ "LEFT JOIN  appointmentscheduling_appointment_block `bl` ON (ts.appointment_block_id = bl.appointment_block_id)  "
				+ "LEFT JOIN  location `loc` ON (bl.location_id = loc.location_id)   "
		        + "LEFT JOIN  provider `pr` ON (bl.provider_id = pr.provider_id)  WHERE ap.voided = false  ";

		if (fromDate != null && toDate != null)
			stringQuery += "AND date_format(ts.start_date,'%Y-%m-%d') between ? AND ?  ";
		if (status != null)
			stringQuery += "AND ap.status = ?   ";
		if (location != null)
			stringQuery += "AND loc.location_id = ?  ";
		if (provider != null) {
			stringQuery += "AND pr.provider_id = ?   ";
		}

		stringQuery += "GROUP BY monthDate ";

		Query query = super.sessionFactory.getCurrentSession().createSQLQuery(stringQuery);

		if (fromDate != null)
			query.setParameter(0, fromDate);
		if (toDate != null)
			query.setParameter(1, toDate);
		if (status != null)
			query.setParameter(2, String.valueOf(status));
		if (location != null)
			query.setParameter(3, location.getId());
		if (provider != null) {
			if (location != null) {
				query.setParameter(4, provider.getId());
			} else {
				query.setParameter(3, provider.getId());
			}
		}
		List<AppointmentDailyCount> dailyCounts = new ArrayList<AppointmentDailyCount>();
		List<Object[]> values = query.list();
		for (Object[] obj : values) {
			AppointmentDailyCount val = new AppointmentDailyCount();
			val.setDailyCount(((BigInteger) obj[0]).intValue());
			val.setDate(obj[1].toString());
			dailyCounts.add(val);
		}
		return dailyCounts;

	}
}
