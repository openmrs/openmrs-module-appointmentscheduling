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

import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;

import java.util.Date;
import java.util.List;

/**
 * Database methods for {@link AppointmentService}.
 */
public interface AppointmentDAO extends SingleClassDAO {
	
	List<Appointment> getAppointmentsByPatient(Patient patient);
	
	Appointment getAppointmentByVisit(Visit visit);
	
	Appointment getLastAppointment(Patient patient);
	
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Provider provider, AppointmentType type,
	        AppointmentStatus status) throws APIException;
	
	List<Appointment> getAppointmentsByStates(List<AppointmentStatus> states);
	
	List<Appointment> getPastAppointmentsByStates(List<AppointmentStatus> states);
	
	List<Appointment> getScheduledAppointmentsForPatient(Patient patient);
	
	List<Appointment> getAppointmentsByAppointmentBlockAndAppointmentType(AppointmentBlock appointmentBlock,
	        AppointmentType appointmentType);
	
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
	List<Appointment> getAppointmentsInTimeSlotByStatus(TimeSlot timeSlot, List<Appointment.AppointmentStatus> statuses);
	
	/**
	 * Retrieve a count of all appointments in a given time slot, filtered by status
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @param statuses - statues to match against
	 * @return a cont of the appointments in the given time slot.
	 * @should not return voided time slots
	 */
	Integer getCountOfAppointmentsInTimeSlotByStatus(TimeSlot timeSlot, List<Appointment.AppointmentStatus> statuses);
}
