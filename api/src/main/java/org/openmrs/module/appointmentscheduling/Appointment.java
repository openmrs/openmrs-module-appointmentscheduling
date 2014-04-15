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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.module.appointmentscheduling.serialize.AppointmentStatusSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * It is a model class. It should extend either {@link BaseOpenmrsObject} or
 * {@link BaseOpenmrsMetadata}.
 */
public class Appointment extends BaseOpenmrsData implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum AppointmentStatusType {
		SCHEDULED, ACTIVE, CANCELLED, MISSED, COMPLETED
	}

	// TODO confirm that "WALK-IN" should be considered active and "RESCHEDULED"
	// should be scheduled
	@JsonSerialize(using = AppointmentStatusSerializer.class)
	public enum AppointmentStatus {

		SCHEDULED("Scheduled", AppointmentStatusType.SCHEDULED),
        RESCHEDULED("Rescheduled", AppointmentStatusType.SCHEDULED),
        WALKIN("Walk-In", AppointmentStatusType.ACTIVE),
        WAITING("Waiting", AppointmentStatusType.ACTIVE),
        INCONSULTATION("In-Consultation", AppointmentStatusType.ACTIVE),
        CANCELLED("Cancelled", AppointmentStatusType.CANCELLED),
        CANCELLED_AND_NEEDS_RESCHEDULE("Cancelled and Needs Reschedule", AppointmentStatusType.CANCELLED),
        MISSED("Missed", AppointmentStatusType.MISSED),
        COMPLETED("Completed", AppointmentStatusType.COMPLETED);

		private final String name;

		private final AppointmentStatusType type;

		private AppointmentStatus(final String name,
				final AppointmentStatusType type) {
			this.name = name;
			this.type = type;
		}

		public String getName() {
			return this.name;
		}

		public AppointmentStatusType getType() {
			return this.type;
		}

		@Override
		public String toString() {
			return name;
		}

		public static List<AppointmentStatus> getAppointmentsStatusByTypes(
				List<AppointmentStatusType> appointmentStatusTypes) {
			List<AppointmentStatus> appointmentStatuses = new ArrayList<AppointmentStatus>();

			for (AppointmentStatus appointmentStatus : AppointmentStatus
					.values()) {
				if (appointmentStatusTypes
						.contains(appointmentStatus.getType())) {
					appointmentStatuses.add(appointmentStatus);
				}
			}

			return appointmentStatuses;
		}

		public static List<AppointmentStatus> getAppointmentsStatusByType(
				AppointmentStatusType appointmentStatusType) {
			return getAppointmentsStatusByTypes(Collections
					.singletonList(appointmentStatusType));
		}

		public static List<AppointmentStatus> getNotCancelledAppointmentStatuses() {
			return getAppointmentsStatusByTypes(Arrays.asList(
					AppointmentStatusType.ACTIVE,
					AppointmentStatusType.COMPLETED,
					AppointmentStatusType.MISSED,
					AppointmentStatusType.SCHEDULED));
		}

	}

	private Integer appointmentId;

	private TimeSlot timeSlot;

	private Visit visit;

	private Patient patient;

	private AppointmentStatus status;

	private String reason;

	private String cancelReason;

	private AppointmentType appointmentType;

	public Appointment() {

	}

	public Appointment(Integer appointmentId) {
		setId(appointmentId);
	}

	public Appointment(TimeSlot timeSlot, Visit visit, Patient patient,
			AppointmentType appointmentType, AppointmentStatus status) {
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

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public AppointmentType getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(AppointmentType appointmentType) {
		this.appointmentType = appointmentType;
	}

}
