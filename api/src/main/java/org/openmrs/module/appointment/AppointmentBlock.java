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
package org.openmrs.module.appointment;

import org.openmrs.BaseOpenmrsData;


public class AppointmentBlock extends BaseOpenmrsData {

	private static final long serialVersionUID = 1L;
	
	private Integer appointmentBlockId;

	
    public Integer getAppointmentBlockId() {
    	return appointmentBlockId;
    }

	
    public void setAppointmentBlockId(Integer appointmentBlockId) {
    	this.appointmentBlockId = appointmentBlockId;
    }
	
    /**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAppointmentBlockId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAppointmentBlockId(id);
	}
}
