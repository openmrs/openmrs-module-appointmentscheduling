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
package org.openmrs.module.appointment.api.db.hibernate;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.APIException;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.db.AppointmentDAO;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of  {@link AppointmentDAO}.
 */
public class HibernateAppointmentServiceDAO implements AppointmentDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }
    /**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAllAppointmentTypes()
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes() throws APIException {
		return getSessionFactory().getCurrentSession().createCriteria(AppointmentType.class).list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAllAppointmentTypes(boolean)
	 */
	@Override
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentType.class);
		return includeRetired ? criteria.list() : criteria.add(Restrictions.eq("retired", includeRetired)).list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentType(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentType(Integer appointmentTypeId) {
		return (AppointmentType) sessionFactory.getCurrentSession().get(AppointmentType.class, appointmentTypeId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentTypeByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentTypeByUuid(String uuid) {
		return (AppointmentType) sessionFactory.getCurrentSession().createQuery("from AppointmentType at where at.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentTypes(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentType.class);
		criteria.add(Restrictions.ilike("name", fuzzySearchPhrase, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	@Transactional
	public AppointmentType saveAppointmentType(AppointmentType appointmentType) {
		sessionFactory.getCurrentSession().saveOrUpdate(appointmentType);
		return appointmentType;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#purgeAppointmentType(org.openmrs.AppointmentType)
	 */
	@Transactional
	public void purgeAppointmentType(AppointmentType appointmentType) {
		sessionFactory.getCurrentSession().delete(appointmentType);
	}
	
    /**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAllAppointmentBlocks()
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks() throws APIException {
		return getSessionFactory().getCurrentSession().createCriteria(AppointmentBlock.class).list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAllAppointmentBlocks(boolean)
	 */
	@Override
	public List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided) throws DAOException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentBlock.class);
		return includeVoided ? criteria.list() : criteria.add(Restrictions.eq("voided", includeVoided)).list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentBlock(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlock(Integer appointmentBlockId) {
		return (AppointmentBlock) sessionFactory.getCurrentSession().get(AppointmentBlock.class, appointmentBlockId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentBlockByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlockByUuid(String uuid) {
		return (AppointmentBlock) sessionFactory.getCurrentSession().createQuery("from AppointmentBlock at where at.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#getAppointmentBlocks(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAppointmentBlocks(String fuzzySearchPhrase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentBlock.class);
		criteria.add(Restrictions.ilike("name", fuzzySearchPhrase, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#saveAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	@Transactional
	public AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock) {
		sessionFactory.getCurrentSession().saveOrUpdate(appointmentBlock);
		return appointmentBlock;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.db.AppointmentDAO#purgeAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	@Transactional
	public void purgeAppointmentBlock(AppointmentBlock appointmentBlock) {
		sessionFactory.getCurrentSession().delete(appointmentBlock);
	}
}