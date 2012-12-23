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

import java.security.Provider.Service;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.api.db.AppointmentBlockDAO;
import org.springframework.transaction.annotation.Transactional;

public class HibernateAppointmentBlockDAO extends HibernateSingleClassDAO implements AppointmentBlockDAO {
	
	public HibernateAppointmentBlockDAO() {
		super(AppointmentBlock.class);
	}
	
	/**
	 * Returns the appointment blocks corresponding to the given date interval and location.
	 * 
	 * @param fromDate the lower bound of the date interval.
	 * @param toDate the upper bound of the date interval.
	 * @param location the location to filter by.
	 * @return the appointment blocks that is on the given date interval and locations.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate, String locations) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(AppointmentBlock.class);
		if (!locations.isEmpty()) {
			String[] locationsAsArray = locations.split(",");
			Disjunction disjunction = Restrictions.disjunction();
			LocationService locationService = Context.getLocationService();
			for (int i = 0; i < locationsAsArray.length; i++) {
				disjunction.add(Restrictions.eq("location", locationService.getLocation(Integer
				        .parseInt(locationsAsArray[i]))));
			}
			criteria.add(disjunction);
		}
		if (fromDate != null) {
			criteria.add(Restrictions.ge("startDate", fromDate));
		}
		if (toDate != null) {
			criteria.add(Restrictions.le("endDate", toDate));
		}
		return criteria.list();
	}
}
