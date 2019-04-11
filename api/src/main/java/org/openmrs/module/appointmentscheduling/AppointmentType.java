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
package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.VisitType;

public class AppointmentType extends BaseOpenmrsMetadata {
	
	private static final long serialVersionUID = 1L;
	
	private Integer appointmentTypeId;

	private VisitType visitType;
	
	private Integer duration;

    private boolean confidential = false;

    public AppointmentType() {
		
	}
	
	public AppointmentType(Integer appointmentTypeId) {
		setId(appointmentTypeId);
	}
	
	public AppointmentType(String name, String description, Integer duration) {
		setName(name);
		setDescription(description);
		setDuration(duration);
	}

	public AppointmentType(String name, String description, Integer duration, VisitType type) {
		setName(name);
		setDescription(description);
		setDuration(duration);
		setVisitType(type);
	}

	
	public Integer getAppointmentTypeId() {
		return appointmentTypeId;
	}
	
	public void setAppointmentTypeId(Integer appointmentTypeId) {
		this.appointmentTypeId = appointmentTypeId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAppointmentTypeId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAppointmentTypeId(id);
	}
	
	public Integer getDuration() {
		return duration;
	}
	
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	
	public String getDisplayString() {
		return getName();
	}

    public void setConfidential(boolean confidential) {
        this.confidential = confidential;
    }

    public boolean isConfidential() {
        return confidential;
    }

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}
}
