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
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.api.db.AppointmentBlockDAO;
import org.openmrs.module.appointment.api.db.AppointmentTypeDAO;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link AppointmentService}.
 */
public class AppointmentServiceImpl extends BaseOpenmrsService implements AppointmentService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AppointmentTypeDAO appointmentTypeDAO;
	private AppointmentBlockDAO appointmentBlockDAO;
	
	/**
     * @param dao the appointment type dao to set
     */
    public void setAppointmentTypeDAO(AppointmentTypeDAO appointmentTypeDAO) {
	    this.appointmentTypeDAO = appointmentTypeDAO;
    }
    
    /**
     * @return the appointment type dao
     */
    public AppointmentTypeDAO getAppointmentTypeDAO() {
	    return appointmentTypeDAO;
    }
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes() {
		return getAppointmentTypeDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) {
		return getAppointmentTypeDAO().getAll(includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentType(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentType(Integer appointmentTypeId) {
		return (AppointmentType)getAppointmentTypeDAO().getById(appointmentTypeId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypeByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentTypeByUuid(String uuid) {
		return (AppointmentType)getAppointmentTypeDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypes(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase) {
		return getAppointmentTypeDAO().getAll(fuzzySearchPhrase);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException {
		ValidateUtil.validate(appointmentType);
		return (AppointmentType)getAppointmentTypeDAO().saveOrUpdate(appointmentType);
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
		getAppointmentTypeDAO().delete(appointmentType);
	}
	
	//Appointment Block
	
	/**
     * @param dao the appointment block dao to set
     */
    public void setAppointmentBlockDAO(AppointmentBlockDAO appointmentBlockDAO) {
	    this.appointmentBlockDAO = appointmentBlockDAO;
    }
    
    /**
     * @return the appointment block dao
     */
    public AppointmentBlockDAO getAppointmentBlockDAO() {
	    return appointmentBlockDAO;
    }
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks() {
		return getAppointmentBlockDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided) {
		return getAppointmentBlockDAO().getAll(includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlock(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlock(Integer appointmentBlockId) {
		return (AppointmentBlock)getAppointmentBlockDAO().getById(appointmentBlockId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlockByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlockByUuid(String uuid) {
		return (AppointmentBlock)getAppointmentBlockDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlocks(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAppointmentBlocks(String fuzzySearchPhrase) {
		return getAppointmentBlockDAO().getAll(fuzzySearchPhrase);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock) throws APIException {
		ValidateUtil.validate(appointmentBlock);
		return (AppointmentBlock)getAppointmentBlockDAO().saveOrUpdate(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#voidAppointmentBlock(org.openmrs.AppointmentBlock, java.lang.String)
	 */
	public AppointmentBlock voidAppointmentBlock(AppointmentBlock appointmentBlock, String reason) {
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#unvoidAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public AppointmentBlock unvoidAppointmentBlock(AppointmentBlock appointmentBlock) {
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public void purgeAppointmentBlock(AppointmentBlock appointmentBlock) {
		getAppointmentBlockDAO().delete(appointmentBlock);
	}

}