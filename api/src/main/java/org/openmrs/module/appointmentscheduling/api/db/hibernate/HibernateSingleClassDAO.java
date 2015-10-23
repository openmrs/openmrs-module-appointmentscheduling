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
package org.openmrs.module.appointmentscheduling.api.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.openmrs.api.db.hibernate.DbSessionFactory;  
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.appointmentscheduling.api.db.SingleClassDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class HibernateSingleClassDAO<T> implements SingleClassDAO<T> {
	
	@Autowired
	protected DbSessionFactory sessionFactory;
	
	protected Class<T> mappedClass;
	
	/**
	 * Marked private because you *must* provide the class at runtime when instantiating one of
	 * these, using the next constructor
	 */
	@SuppressWarnings("unused")
	private HibernateSingleClassDAO() {
	}
	
	/**
	 * You must call this before using any of the data access methods, since it's not actually
	 * possible to write them all with compile-time class information.
	 * 
	 * @param mappedClass
	 */
	protected HibernateSingleClassDAO(Class<T> mappedClass) {
		this.mappedClass = mappedClass;
	}
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public T getById(Integer id) {
		return (T) sessionFactory.getCurrentSession().get(mappedClass, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public T getByUuid(String uuid) {
		return (T) sessionFactory.getCurrentSession()
		        .createQuery("from " + mappedClass.getSimpleName() + " at where at.uuid = :uuid").setString("uuid", uuid)
		        .uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<T> getAll() {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(mappedClass).list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<T> getAll(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
		return (List<T>) (includeRetired ? criteria.list() : criteria.add(Restrictions.eq("retired", includeRetired)).list());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<T> getAllData(boolean includeVoided) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
		return (List<T>) (includeVoided ? criteria.list() : criteria.add(Restrictions.eq("voided", includeVoided)).list());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<T> getAll(String fuzzySearchPhrase) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(mappedClass);
		criteria.add(Restrictions.ilike("name", fuzzySearchPhrase, MatchMode.ANYWHERE));
		criteria.addOrder(Order.asc("name"));
		return criteria.list();
	}
	
	@Override
	@Transactional
	public T saveOrUpdate(T object) {
		sessionFactory.getCurrentSession().saveOrUpdate(object);
		return object;
	}
	
	@Override
	@Transactional
	public T update(T object) {
		sessionFactory.getCurrentSession().update(object);
		return object;
	}
	
	@Override
	@Transactional
	public void delete(T object) {
		sessionFactory.getCurrentSession().delete(object);
	}
}
