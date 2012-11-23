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
package org.openmrs.module.appointment.api.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.api.db.AppointmentDAO;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link AppointmentService}.
 */
public class AppointmentServiceImpl extends BaseOpenmrsService implements AppointmentService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AppointmentDAO dao;
	
	/**
     * @param dao the dao to set
     */
    public void setDao(AppointmentDAO dao) {
	    this.dao = dao;
    }
    
    /**
     * @return the dao
     */
    public AppointmentDAO getDao() {
	    return dao;
    }
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes() {
		return getDao().getAllAppointmentTypes();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) {
		return dao.getAllAppointmentTypes(includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentType(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentType(Integer appointmentTypeId) {
		return getDao().getAppointmentType(appointmentTypeId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypeByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentTypeByUuid(String uuid) {
		return getDao().getAppointmentTypeByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypes(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase) {
		return getDao().getAppointmentTypes(fuzzySearchPhrase);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException {
		ValidateUtil.validate(appointmentType);
		return getDao().saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#retireAppointmentType(org.openmrs.AppointmentType, java.lang.String)
	 */
	public AppointmentType retireAppointmentType(AppointmentType appointmentType, String reason) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#unretireAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType unretireAppointmentType(AppointmentType appointmentType) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentType(org.openmrs.AppointmentType)
	 */
	public void purgeAppointmentType(AppointmentType appointmentType) {
		getDao().purgeAppointmentType(appointmentType);
	}
}