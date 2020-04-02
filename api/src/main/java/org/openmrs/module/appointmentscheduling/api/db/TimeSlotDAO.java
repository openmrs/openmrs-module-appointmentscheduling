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

import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;

public interface TimeSlotDAO extends SingleClassDAO {
	
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
	        throws APIException;
	
	/**
	 * Return a list of time slots that are associated with the given Appointment Block
	 * 
	 * @param appointmentBlock - a given appointment block.
	 * @return List of TimeSlots that are associated with the given Appointment Block, null if
	 *         illegal values (null appointmentBlock)
	 * <strong>Should</strong> not return voided time slots
	 */
	List<TimeSlot> getTimeSlotsByAppointmentBlock(AppointmentBlock appointmentBlock);
}
