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
package org.openmrs.module.appointment.api;

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.appointment.AppointmentType;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(AppointmentService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface AppointmentService extends OpenmrsService {
     
	/**
	 * Gets all appointment types.
	 * 
	 * @return a list of appointment type objects.
	 * @should get all appointment types
	 */
	@Transactional(readOnly=true)
	List<AppointmentType> getAllAppointmentTypes();
	
	/**
	 * Get all appointment types based on includeRetired flag
	 * 
	 * @param includeRetired
	 * @return List of all appointment types
	 * @should get all appointment types based on include retired flag.
	 */
	@Transactional(readOnly=true)
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired);
	
	/**
	 * Gets an appointment type by its appointment type id.
	 * 
	 * @param appointmentTypeId the appointment type id.
	 * @return the appointment type object found with the given id, else null.
	 * @should get correct appointment type
	 */
	@Transactional(readOnly=true)
	AppointmentType getAppointmentType(Integer appointmentTypeId);
	
	/**
	 * Gets an appointment type by its UUID.
	 * 
	 * @param uuid the appointment type UUID.
	 * @return the appointment type object found with the given uuid, else null.
	 * @should get correct appointment type
	 */
	@Transactional(readOnly=true)
	AppointmentType getAppointmentTypeByUuid(String uuid);
	
	/**
	 * Gets all appointment types whose names are similar to or contain the given search phrase.
	 * 
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @return a list of all appointment types with names similar to or containing the given phrase
	 * @should get correct appointment types
	 */
	@Transactional(readOnly=true)
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase);
	
	/**
	 * Creates or updates the given appointment type in the database.
	 * 
	 * @param appointmentType the appointment type to create or update.
	 * @return the created or updated appointment type.
	 * @should save new appointment type
	 * @should save edited appointment type
	 * @should throw error when name is null
	 * @should throw error when name is empty string
	 */
	AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException;
	
	/**
	 * Retires a given appointment type.
	 * 
	 * @param appointmentType the appointment type to retire.
	 * @param reason the reason why the appointment type is retired.
	 * @return the appointment type that has been retired.
	 * @should retire given appointment type
	 */
	AppointmentType retireAppointmentType(AppointmentType appointmentType, String reason);
	
	/**
	 * Unretires an appointment type.
	 * 
	 * @param appointmentType the appointment type to unretire.
	 * @return the unretired appointment type
	 * @should unretire given appointment type
	 */
	AppointmentType unretireAppointmentType(AppointmentType appointmentType);
	
	/**
	 * Completely removes an appointment type from the database. This is not reversible.
	 * 
	 * @param appointmentType the appointment type to delete from the database.
	 * @should delete given appointment type
	 */
	void purgeAppointmentType(AppointmentType appointmentType);
}