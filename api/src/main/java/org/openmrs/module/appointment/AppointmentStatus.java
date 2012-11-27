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

public class AppointmentStatus extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private Integer appointmentStatusId;
	
	private boolean createdGroup;
	
	private boolean changedGroup;
	
	private boolean canceledGroup;
	
	private boolean inProgressGroup;
	
	private boolean missedGroup;
	
	private boolean finishedGroup;
	
	public Integer getAppointmentStatusId() {
		return appointmentStatusId;
	}
	
	public void setAppointmentStatusId(Integer appointmentStatusId) {
		this.appointmentStatusId = appointmentStatusId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAppointmentStatusId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAppointmentStatusId(id);
	}
	
	public boolean isCreatedGroup() {
		return createdGroup;
	}
	
	public void setCreatedGroup(boolean createdGroup) {
		this.createdGroup = createdGroup;
	}
	
	public boolean isChangedGroup() {
		return changedGroup;
	}
	
	public void setChangedGroup(boolean changedGroup) {
		this.changedGroup = changedGroup;
	}
	
	public boolean isCanceledGroup() {
		return canceledGroup;
	}
	
	public void setCanceledGroup(boolean canceledGroup) {
		this.canceledGroup = canceledGroup;
	}
	
	public boolean isInProgressGroup() {
		return inProgressGroup;
	}
	
	public void setInProgressGroup(boolean inProgressGroup) {
		this.inProgressGroup = inProgressGroup;
	}
	
	public boolean isMissedGroup() {
		return missedGroup;
	}
	
	public void setMissedGroup(boolean missedGroup) {
		this.missedGroup = missedGroup;
	}
	
	public boolean isFinishedGroup() {
		return finishedGroup;
	}
	
	public void setFinishedGroup(boolean finishedGroup) {
		this.finishedGroup = finishedGroup;
	}
	
}
