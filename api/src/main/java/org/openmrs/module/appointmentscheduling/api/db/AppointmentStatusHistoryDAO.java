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
package org.openmrs.module.appointmentscheduling.api.db;

import java.util.Date;
import java.util.List;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database methods for {@link AppointmentService}.
 */
public interface AppointmentStatusHistoryDAO extends SingleClassDAO {
	
	/**
	 * Retrives the start date of the current status of a given appointment.
	 * 
	 * @param appointment - The appointment.
	 * @return the start date of the current status of a given appointment.
	 */
	@Transactional(readOnly = true)
	public Date getStartDateOfCurrentStatus(Appointment appointment);
	
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAll(AppointmentStatus status);
	
	/**
	 * Retrieves the histories in the given interval that describes the given status Does not takes
	 * into consideration current appointments with the given status.
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return A list of AppointmentStatusHistory histories that fits the given criteria.
	 */
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getHistoriesByInterval(Date fromDate, Date endDate, AppointmentStatus status);
	
	/**
	 * Purge all history from a specific appointment
	 * 
	 * @param appointment The appointment which the story should be deleted.
	 * @return
	 */
	public void purgeHistoryBy(Appointment appointment);

	/**
	 * retrieves all the statuses of an appointment
	 * @param appointment
	 * @return
	 */
	@Transactional(readOnly = true)
	List<AppointmentStatusHistory> getAppointmentStatusHistories(Appointment appointment);

	/**
	 * retrieves the most recent status of an appointment
	 * @param appointment
	 * @return
	 */
	@Transactional(readOnly = true)
	AppointmentStatusHistory getMostRecentAppointmentStatusHistory(Appointment appointment);

}
