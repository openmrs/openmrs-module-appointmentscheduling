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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Visit;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or
 * {@link BaseOpenmrsMetadata}.
 */
public class Appointment extends BaseOpenmrsData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// TODO confirm that "WALK-IN" should be considered active
	public enum AppointmentStatus {
		SCHEDULED("Scheduled", true, false), RESCHEDULED("Rescheduled", true, false), WALKIN("Walk-In", true, true), CANCELLED(
		        "Cancelled", false, false), WAITING("Waiting", true, true), INCONSULTATION("In-Consultation", true, true), COMPLETED(
		        "Completed", true, false), MISSED("Missed", false, false), CANCELLED_AND_NEEDS_RESCHEDULE(
		        "Cancelled and Needs Reschedule", false, false);
		
		private final String name;
		
		/**
		 * Whether or not an appointment with this status should be considered "cancelled" Right now
		 * we consider CANCELLED, CANCELLED_AND_NEEDS_RESCHEDULE, and MISSED appts as cancelled
		 */
		private Boolean cancelled;
		
		/**
		 * Whether or not this appointment represents an "active" appointment, where active=patient
		 * checked-in and present within the health facility
		 */
		private Boolean active;
		
		private AppointmentStatus(final String name, final Boolean cancelled, final Boolean active) {
			this.name = name;
			this.cancelled = cancelled;
			this.active = active;
		}
		
		public String getName() {
			return this.name;
		}
		
		public Boolean isCancelled() {
			return this.cancelled;
		}
		
		public Boolean isActive() {
			return this.active;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public static List<AppointmentStatus> filter(Predicate predicate) {
			List<AppointmentStatus> appointmentStatuses = new ArrayList(Arrays.asList(AppointmentStatus.values())); // need to assign to a new array because Arrays.asList is fixed length
			CollectionUtils.filter(appointmentStatuses, predicate);
			return appointmentStatuses;
		}
		
		public static Predicate cancelledPredicate = new Predicate() {
			
			@Override
			public boolean evaluate(Object o) {
				return ((AppointmentStatus) o).isCancelled();
			}
		};
		
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
