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
package org.openmrs.module.appointment.api.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Provider;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;

public interface TimeSlotDAO extends SingleClassDAO {
	
	/**
	 * 
	 * Retrieve all appointments in a given time slot.
	 * 
	 * @param timeSlot - The time slot to look into.
	 * @return a list of the appointments in the given time slot.
	 */
	List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Return a list of time slots that stands within the given constraints.
	 * 
	 * @param appointmentType - Type of the appointment
	 * @param fromDate - (optional) earliest start date.
	 * @param toDate - (optional) latest start date.
	 * @param provider - (optional) the appointment's provider.
	 * @return List of TimeSlots that stands within the given constraints, null if illegal values
	 *         (fromDate>toDate or null appointmentType)
	 */
	List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate, Provider provider)
	        throws Exception;
}
