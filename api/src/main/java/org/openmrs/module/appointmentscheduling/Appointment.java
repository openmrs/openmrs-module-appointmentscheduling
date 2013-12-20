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

import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Visit;

import java.io.Serializable;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or
 * {@link BaseOpenmrsMetadata}.
 */
public class Appointment extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public enum AppointmentStatus {
		SCHEDULED("Scheduled"), RESCHEDULED("Rescheduled"), WALKIN("Walk-In"), CANCELLED("Cancelled"), WAITING("Waiting"), INCONSULTATION(
		        "In-Consultation"), COMPLETED("Completed"), MISSED("Missed");
		
		private final String name;
		
		public String getName() {
			return this.name;
		}
		
		private AppointmentStatus(final String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}
	
	private Integer appointmentId;
	
	private TimeSlot timeSlot;
	
	private Visit visit;
	
	private Patient patient;
	
	private AppointmentStatus status;
	
	private String reason;
	
	private AppointmentType appointmentType;
	
	public Appointment() {
		
	}
	
	public Appointment(Integer appointmentId) {
		setId(appointmentId);
	}
	
	public Appointment(TimeSlot timeSlot, Visit visit, Patient patient, AppointmentType appointmentType,
	    AppointmentStatus status) {
		setTimeSlot(timeSlot);
		setVisit(visit);
		setPatient(patient);
		setStatus(status);
		setAppointmentType(appointmentType);
	}
	
	public Integer getAppointmentId() {
		return appointmentId;
	}
	
	public void setAppointmentId(Integer appointmentId) {
		this.appointmentId = appointmentId;
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#getId()
	 */
	@Override
	public Integer getId() {
		return getAppointmentId();
	}
	
	/**
	 * @see org.openmrs.OpenmrsObject#setId(java.lang.Integer)
	 */
	@Override
	public void setId(Integer id) {
		setAppointmentId(id);
	}
	
	public TimeSlot getTimeSlot() {
		return timeSlot;
	}
	
	public void setTimeSlot(TimeSlot timeSlot) {
		this.timeSlot = timeSlot;
	}
	
	public Visit getVisit() {
		return visit;
	}
	
	public void setVisit(Visit visit) {
		this.visit = visit;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public AppointmentStatus getStatus() {
		return status;
	}
	
	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public AppointmentType getAppointmentType() {
		return appointmentType;
	}
	
	public void setAppointmentType(AppointmentType appointmentType) {
		this.appointmentType = appointmentType;
	}
	
}
