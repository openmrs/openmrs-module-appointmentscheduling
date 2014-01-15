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

import java.util.Date;

import org.openmrs.BaseOpenmrsData;

public class TimeSlot extends BaseOpenmrsData {
	
	private static final long serialVersionUID = 1L;
	
	private Integer timeSlotId;
	
	private AppointmentBlock appointmentBlock;
	
	private Date startDate;
	
	private Date endDate;
	
	public TimeSlot() {
		
	}
	
	public TimeSlot(Integer timeSlotId) {
		setId(timeSlotId);
	}
	
	public TimeSlot(AppointmentBlock appointmentBlock, Date startDate, Date endDate) {
		setAppointmentBlock(appointmentBlock);
		setStartDate(startDate);
		setEndDate(endDate);
	}
	
	public Integer getTimeSlotId() {
		return timeSlotId;
	}
	
	public void setTimeSlotId(Integer timeSlotId) {
		this.timeSlotId = timeSlotId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getTimeSlotId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setTimeSlotId(id);
	}
	
	public AppointmentBlock getAppointmentBlock() {
		return appointmentBlock;
	}
	
	public void setAppointmentBlock(AppointmentBlock appointmentBlock) {
		this.appointmentBlock = appointmentBlock;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
