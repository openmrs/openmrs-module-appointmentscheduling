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

import java.util.List;

import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;


/**
 *  Database methods for {@link AppointmentService}.
 */
public interface AppointmentDAO {
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes()
	 */
	List<AppointmentType> getAllAppointmentTypes() throws APIException;
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes(boolean)
	 */
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) throws DAOException;
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentType(java.lang.Integer)
	 */
	AppointmentType getAppointmentType(Integer appointmentTypeId);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypeByUuid(java.lang.String)
	 */
	AppointmentType getAppointmentTypeByUuid(String uuid);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypes(java.lang.String)
	 */
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	AppointmentType saveAppointmentType(AppointmentType appointmentType);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentType(org.openmrs.AppointmentType)
	 */
	void purgeAppointmentType(AppointmentType appointmentType);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks()
	 */
	List<AppointmentBlock> getAllAppointmentBlocks() throws APIException;
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks(boolean)
	 */
	public List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided) throws DAOException;
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlock(java.lang.Integer)
	 */
	AppointmentBlock getAppointmentBlock(Integer appointmentBlockId);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlockByUuid(java.lang.String)
	 */
	AppointmentBlock getAppointmentBlockByUuid(String uuid);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlocks(java.lang.String)
	 */
	List<AppointmentBlock> getAppointmentBlocks(String fuzzySearchPhrase);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock);
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	void purgeAppointmentBlock(AppointmentBlock appointmentBlock);
}