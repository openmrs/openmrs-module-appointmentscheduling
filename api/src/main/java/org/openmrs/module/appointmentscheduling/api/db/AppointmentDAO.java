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

import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Database methods for {@link AppointmentService}.
 */
public interface AppointmentDAO extends SingleClassDAO {
	
	List<Appointment> getAppointmentsByPatient(Patient patient);
	
	Appointment getAppointmentByVisit(Visit visit);
	
	Appointment getLastAppointment(Patient patient);
	
	@Transactional(readOnly = true)
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Provider provider,
	        AppointmentType appointmentType, List<AppointmentStatus> statuses, Patient patient,
		    VisitType visitType, Visit visit) throws APIException;
	
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Provider provider, AppointmentType type,
		   AppointmentStatus status, Patient patient) throws APIException;
	
	List<Appointment> getAppointmentsByStates(List<AppointmentStatus> states);
	
	List<Appointment> getPastAppointmentsByStates(List<AppointmentStatus> states);
	
	List<Appointment> getScheduledAppointmentsForPatient(Patient patient);
	
	List<Appointment> getAppointmentsByAppointmentBlockAndAppointmentTypes(AppointmentBlock appointmentBlock,
	        List<AppointmentType> appointmentTypes);
	
	/**
	 * Retrieve all appointments in a given time slot.
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @return a list of the appointments in the given time slot.
	 * @should not return voided time slots
	 */
	List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Retrieve a count of appointments in a given time slot.
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @return a count of the appointments in the given time slot.
	 * @should not return voided time slots
	 */
	Integer getCountOfAppointmentsInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Retrieve all appointments in a given time slot, filtered by status
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @param statuses - statues to match against
	 * @return a list of the appointments in the given time slot.
	 * @should not return voided time slots
	 */
	List<Appointment> getAppointmentsInTimeSlotByStatus(TimeSlot timeSlot, List<AppointmentStatus> statuses);
	
	/**
	 * Retrieve a count of all appointments in a given time slot, filtered by status
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @param statuses - statues to match against
	 * @return a cont of the appointments in the given time slot.
	 * @should not return voided time slots
	 */
	Integer getCountOfAppointmentsInTimeSlotByStatus(TimeSlot timeSlot, List<AppointmentStatus> statuses);

	/**
	 * returns list of patient who have not come for the visits using
	 * minDays and maxDays range provided
	 */
	@Transactional(readOnly = true)
	List<Appointment> getDefaultersList(int minDays, int maxDays, Provider provider, AppointmentType type,
										VisitType visitType) throws DAOException;
}
