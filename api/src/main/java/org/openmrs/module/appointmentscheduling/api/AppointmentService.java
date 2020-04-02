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
package org.openmrs.module.appointmentscheduling.api;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentDailyCount;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.appointmentscheduling.ProviderSchedule;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service exposes module's core functionality. It is a Spring managed bean
 * which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(AppointmentService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
public interface AppointmentService extends OpenmrsService {

	/**
	 * Gets all appointment types.
	 * 
	 * @return a list of appointment type objects.
	 * <strong>Should</strong> get all appointment types
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	Set<AppointmentType> getAllAppointmentTypes();

	/**
	 * Get all appointment types based on includeRetired flag
	 * 
	 * @param includeRetired
	 * @return List of all appointment types
	 * <strong>Should</strong> get all appointment types based on include retired flag.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	List<AppointmentType> getAllAppointmentTypes(boolean includeRetired);

	/**
	 * Gets an appointment type by its appointment type id.
	 * 
	 * @param appointmentTypeId the appointment type id.
	 * @return the appointment type object found with the given id, else null.
	 * <strong>Should</strong> get correct appointment type
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	AppointmentType getAppointmentType(Integer appointmentTypeId);

	/**
	 * Gets an appointment type by its UUID.
	 * 
	 * @param uuid the appointment type UUID.
	 * @return the appointment type object found with the given uuid, else null.
	 * <strong>Should</strong> get correct appointment type
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	AppointmentType getAppointmentTypeByUuid(String uuid);

	/**
	 * Gets all appointment types (including retired) whose names are similar to or contain the
	 * given search phrase.
	 * 
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @return a list of all appointment types with names similar to or containing the given phrase
	 * <strong>Should</strong> get correct appointment types
	 * <strong>Should</strong> include retired appointment types
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase);

	/**
	 * Gets all appointment types whose names are similar to or contain the given search phrase.
	 * 
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @param includeRetired whether or not to include retired types
	 * @return a list of all appointment types with names similar to or containing the given phrase
	 * <strong>Should</strong> get correct appointment types
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase,
			boolean includeRetired);

	/**
	 * Creates or updates the given appointment type in the database.
	 * 
	 * @param appointmentType the appointment type to create or update.
	 * @return the created or updated appointment type.
	 * <strong>Should</strong> save new appointment type
	 * <strong>Should</strong> save edited appointment type
     * <strong>Should</strong> save confidential appointment type
	 * <strong>Should</strong> throw error when name is null
	 * <strong>Should</strong> throw error when name is empty string
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_TYPES)
	AppointmentType saveAppointmentType(AppointmentType appointmentType)
			throws APIException;

	/**
	 * Retires a given appointment type.
	 * 
	 * @param appointmentType the appointment type to retire.
	 * @param reason the reason why the appointment type is retired.
	 * @return the appointment type that has been retired.
	 * <strong>Should</strong> retire given appointment type
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_TYPES)
	AppointmentType retireAppointmentType(AppointmentType appointmentType,
			String reason);

	/**
	 * Unretires an appointment type.
	 * 
	 * @param appointmentType the appointment type to unretire.
	 * @return the unretired appointment type
	 * <strong>Should</strong> unretire given appointment type
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_TYPES)
	AppointmentType unretireAppointmentType(AppointmentType appointmentType);

	/**
	 * Completely removes an appointment type from the database. This is not reversible.
	 * 
	 * @param appointmentType the appointment type to delete from the database.
	 * <strong>Should</strong> delete given appointment type
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_TYPES)
	void purgeAppointmentType(AppointmentType appointmentType);

	// AppointmentBlock
	/**
	 * Gets all appointment blocks.
	 * 
	 * @return a list of appointment block objects.
	 * <strong>Should</strong> get all appointment blocks
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<AppointmentBlock> getAllAppointmentBlocks();

	/**
	 * Get all appointment blocks based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all appointment blocks
	 * <strong>Should</strong> get all appointment blocks based on include voided flag.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided);

	/**
	 * Gets an appointment block by its appointment block id.
	 * 
	 * @param appointmentBlockId the appointment block id.
	 * @return the appointment block object found with the given id, else null.
	 * <strong>Should</strong> get correct appointment block
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	AppointmentBlock getAppointmentBlock(Integer appointmentBlockId);

	/**
	 * Gets an appointment block by its UUID.
	 * 
	 * @param uuid the appointment block UUID.
	 * @return the appointment block object found with the given uuid, else null.
	 * <strong>Should</strong> get correct appointment block
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	AppointmentBlock getAppointmentBlockByUuid(String uuid);

	/**
	 * Creates or updates the given appointment block in the database.
	 * 
	 * @param appointmentBlock the appointment block to create or update.
	 * @return the created or updated appointment block.
	 * <strong>Should</strong> save new appointment block
	 * <strong>Should</strong> save a providerless appointment block
	 * <strong>Should</strong> save edited appointment block
	 * <strong>Should</strong> throw error when name is null
	 * <strong>Should</strong> throw error when name is empty string
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock)
			throws APIException;

	/**
	 * Voids a given appointment block.
	 * 
	 * @param appointmentBlock the appointment block to void.
	 * @param reason the reason why the appointment block is voided.
	 * @return the appointment block that has been voided.
	 * <strong>Should</strong> void given appointment block
	 * <strong>Should</strong> void all associated time slots
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	AppointmentBlock voidAppointmentBlock(AppointmentBlock appointmentBlock,
			String reason);

	/**
	 * Unvoids an appointment block.
	 * 
	 * @param appointmentBlock the appointment block to unvoid.
	 * @return the unvoided appointment block
	 * <strong>Should</strong> unvoided given appointment block
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	AppointmentBlock unvoidAppointmentBlock(AppointmentBlock appointmentBlock);

	/**
	 * Completely removes an appointment block from the database. This is not reversible.
	 * 
	 * @param appointmentBlock the appointment block to delete from the database.
	 * <strong>Should</strong> delete given appointment block
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	void purgeAppointmentBlock(AppointmentBlock appointmentBlock);

	/**
	 * Gets appointment blocks which have a given date, location, provider and list of appointment
	 * types
	 * 
	 * @return a list of appointment block objects.
	 * <strong>Should</strong> get all appointment blocks which have contains in a given date interval and
	 *         corresponds to a given locations, provider and appointment types.
	 * <strong>Should</strong> not return voided appointment blocks
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<AppointmentBlock> getAppointmentBlocksByTypes(Date fromDate,
			Date toDate, String locations, Provider provider,
			List<AppointmentType> appointmentTypes);

	/**
	 * Gets appointment blocks which have a given date and location.
	 * 
	 * @return a list of appointment block objects.
	 * <strong>Should</strong> get all appointment blocks which have contains in a given date interval and
	 *         corresponds to a given locations, provider and appointment type.
	 * <strong>Should</strong> not return voided appointment blocks
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate,
			String locations, Provider provider, AppointmentType appointmentType);

	/**
	 * Gets appointment blocks which overlap to the given appointment block
	 * 
	 * @return a list of appointment block objects.
	 * <strong>Should</strong> get all appointment blocks which overlap to the given appointment block
	 * <strong>Should</strong> allow overlapping providerless appointment blocks
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<AppointmentBlock> getOverlappingAppointmentBlocks(
			AppointmentBlock appointmentBlock);

	// Appointment
	/**
	 * Gets all appointments.
	 * 
	 * @return a list of appointment objects.
	 * <strong>Should</strong> get all appointment
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAllAppointments();

	/**
	 * Get all appointments based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all appointments
	 * <strong>Should</strong> get all appointments based on include voided flag.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	public List<Appointment> getAllAppointments(boolean includeVoided);

	/**
	 * Gets an appointment by its appointment id.
	 * 
	 * @param appointmentId the appointment id.
	 * @return the appointment object found with the given id, else null.
	 * <strong>Should</strong> get correct appointment
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Appointment getAppointment(Integer appointmentId);

	/**
	 * Gets an appointment by its UUID.
	 * 
	 * @param uuid the appointment UUID.
	 * @return the appointment object found with the given uuid, else null.
	 * <strong>Should</strong> get correct appointment
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Appointment getAppointmentByUuid(String uuid);

	/**
	 * Creates or updates the given appointment in the database.
	 * 
	 * @param appointment the appointment to create or update.
	 * @return the created or updated appointment.
	 * <strong>Should</strong> save new appointment
	 * <strong>Should</strong> save edited appointment
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	Appointment saveAppointment(Appointment appointment) throws APIException;

	/**
	 * Voids a given appointment.
	 * 
	 * @param appointment the appointment to void.
	 * @param reason the reason why the appointment is voided.
	 * @return the appointment that has been voided.
	 * <strong>Should</strong> void given appointment
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	Appointment voidAppointment(Appointment appointment, String reason);

	/**
	 * Unvoids an appointment.
	 * 
	 * @param appointment the appointment to unvoid.
	 * @return the unvoid appointment
	 * <strong>Should</strong> unvoid given appointment
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	Appointment unvoidAppointment(Appointment appointment);

	/**
	 * Completely removes an appointment from the database. This is not reversible.
	 * 
	 * @param appointment the appointment to delete from the database.
	 * <strong>Should</strong> delete given appointment
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	void purgeAppointment(Appointment appointment);

	/**
	 * Returns all Appointments for a given Patient
	 * 
	 * @param patientId the patient id to search by.
	 * @return all the appointments for the given patient id.
	 * <strong>Should</strong> return all of the appointments for the given patient.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsOfPatient(Patient patient);

	/**
	 * Returns the appointment corresponding to the given visit.
	 * 
	 * @param visitId the visit id to search by.
	 * @return the appointment that is related to this visit, null if there isnt any.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Appointment getAppointmentByVisit(Visit visit);

	// TimeSlot

	/**
	 * Gets all time slots.
	 * 
	 * @return a list of time slot objects.
	 * <strong>Should</strong> get all time slots
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<TimeSlot> getAllTimeSlots();

	/**
	 * Get all time slots based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all time slots
	 * <strong>Should</strong> get all time slots based on include voided flag.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	public List<TimeSlot> getAllTimeSlots(boolean includeVoided);

	/**
	 * Creates or updates the given time slot in the database.
	 * 
	 * @param timeSlot the time slot to create or update.
	 * @return the created or updated time slot.
	 * <strong>Should</strong> save new time slot
	 * <strong>Should</strong> save edited time slot
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	TimeSlot saveTimeSlot(TimeSlot timeSlot) throws APIException;

	/**
	 * Gets a a time slot by its id.
	 * 
	 * @param timeSlotId the time slot id.
	 * @return the time slot object found with the given id, else null.
	 * <strong>Should</strong> get correct time slot
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	TimeSlot getTimeSlot(Integer timeSlotId);

	/**
	 * Gets a time slot by its UUID.
	 * 
	 * @param uuid the time slot UUID.
	 * @return the time slot object found with the given uuid, else null.
	 * <strong>Should</strong> get correct time slot
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	TimeSlot getTimeSlotByUuid(String uuid);

	/**
	 * Voids a given time slot.
	 * 
	 * @param timeSlot the time slot to void.
	 * @param reason the reason why the time slot is voided.
	 * @return the time slot that has been voided.
	 * <strong>Should</strong> void given time slot
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	TimeSlot voidTimeSlot(TimeSlot timeSlot, String reason);

	/**
	 * Unvoids a time slot.
	 * 
	 * @param timeSlot the time slot to unvoid.
	 * @return the unvoided time slot
	 * <strong>Should</strong> unvoid given time slot
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	TimeSlot unvoidTimeSlot(TimeSlot timeSlot);

	/**
	 * Completely removes a time slot from the database. This is not reversible.
	 * 
	 * @param timeSlot the time slot to delete from the database.
	 * <strong>Should</strong> delete given time slot
	 */
    @Authorized(AppointmentUtils.PRIV_MANAGE_APPOINTMENT_BLOCKS)
	void purgeTimeSlot(TimeSlot timeSlot);

	/**
	 * Should retrieve all appointments in the given time slot.
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the appointments in the given time slot.
	 * <strong>Should</strong> not return voided appointments
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot);

	/**
	 * Should retrieve all appointments in the given time slot that do not have a status that means
	 * the appointment has been cancelled (ie status=CANCELLED, CANCELLED_AND_NEEDS_RESCHEDULE)
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the appointments in the given time slo
	 * <strong>Should</strong> not return missed, cancelled, and needs_reschedule appointments.
	 * <strong>Should</strong> not return voided appointments
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsInTimeSlotThatAreNotCancelled(
			TimeSlot timeSlot);

	/**
	 * Gets a count of the number of appointments in a time slot
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the count of appointments in the given time slot
	 * <strong>Should</strong> not count voided appointments
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Integer getCountOfAppointmentsInTimeSlot(TimeSlot timeSlot);

	/**
	 * Gets a count of all appointments in the given time slot that do not have a status that means
	 * the appointment has been cancelled (ie status=CANCELLED, CANCELLED_AND_NEEDS_RESCHEDULE)
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the count of appointments in the given time slot
	 * <strong>Should</strong> not count missed, cancelled and needs rescheduled appointments.
	 * <strong>Should</strong> not count voided appointments
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Integer getCountOfAppointmentsInTimeSlotThatAreNotCancelled(
			TimeSlot timeSlot);

	/**
	 * Should retrieve all time slots in the given appointment block.
	 * 
	 * @param appointmentBlock - the appointment block to search by.
	 * @return the time slots in the given appointment block.
	 * <strong>Should</strong> not return voided time slots
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<TimeSlot> getTimeSlotsInAppointmentBlock(
			AppointmentBlock appointmentBlock);

	// Appointment Status History
	/**
	 * Gets all appointment status histories.
	 * 
	 * @return a list of appointment status history objects.
	 * <strong>Should</strong> get all appointment status histories
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<AppointmentStatusHistory> getAllAppointmentStatusHistories();

	/**
	 * Gets an appointment status by its appointment status history id.
	 * 
	 * @param appointmentStatusHistoryId the appointment status history id.
	 * @return the appointment status history object found with the given id, else null.
	 * <strong>Should</strong> get correct appointment status history
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	AppointmentStatusHistory getAppointmentStatusHistory(
			Integer appointmentStatusHistoryId);

	/**
	 * Gets all appointment status histories whose statuses are similar to or contain the given
	 * status.
	 * 
	 * @param status the search phrase to use.
	 * @return a list of all appointment status histories with names identical to or containing the
	 *         given status
	 * <strong>Should</strong> get correct appointment status histories
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<AppointmentStatusHistory> getAppointmentStatusHistories(
			AppointmentStatus status);

	/**
	 * Creates or updates the given appointment status history in the database.
	 * 
	 * @param AppointmentStatusHistory the appointment status history to create or update.
	 * @return the created or updated appointment status history.
	 * <strong>Should</strong> save new appointment status history
	 * <strong>Should</strong> save edited appointment status history
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	AppointmentStatusHistory saveAppointmentStatusHistory(
			AppointmentStatusHistory appointmentStatusHistory)
			throws APIException;

    /**
     * Gets all appointments requests
     *
     * @return a list of appointment requests objects.
     * <strong>Should</strong> get all appointment requests
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    List<AppointmentRequest> getAllAppointmentRequests();

    /**
     * Get all appointments requests based on includeVoided flag
     *
     * @param includeVoided
     * @return List of all appointment requests
     * <strong>Should</strong> get all appointment request based on include voided flag.
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    public List<AppointmentRequest> getAllAppointmentRequests(boolean includeVoided);

    /**
     * Gets an appointment requests by its id.
     *
     * @param appointmentRequestId the appointment request id.
     * @return the appointment request object found with the given id, else null.
     * <strong>Should</strong> get correct appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    AppointmentRequest getAppointmentRequest(Integer appointmentRequestId);

    /**
     * Gets an appointment request by its UUID.
     *
     * @param uuid the appointment request UUID.
     * @return the appointment request object found with the given uuid, else null.
     * <strong>Should</strong> get correct appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    AppointmentRequest getAppointmentRequestByUuid(String uuid);

    /**
     * Retrieves Appointments Requests that satisfy the given constraints
     *
     * @param patient - The patient
     * @param type - The appointment type
     * @param provider - The requested provider
     * @param status - The appointment request status
     * @return a list of appointment requests that satisfy the given constraints
     */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
    List<AppointmentRequest> getAppointmentRequestsByConstraints(Patient patient, AppointmentType type, Provider provider,
                                                    AppointmentRequest.AppointmentRequestStatus status) throws APIException;

    /**
     * Creates or updates the given appointment requests in the database.
     *
     * @param appointmentRequest the appointment request to create or update.
     * @return the created or updated appointment request.
     * <strong>Should</strong> save new appointment request
     * <strong>Should</strong> save edited appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    AppointmentRequest saveAppointmentRequest(AppointmentRequest appointmentRequest) throws APIException;

    /**
     * Voids a given appointment request
     *
     * @param appointmentRequest the appointment request to void.
     * @param reason the reason why the appointment request is voided.
     * @return the appointment request that has been voided.
     * <strong>Should</strong> void given appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    AppointmentRequest voidAppointmentRequest(AppointmentRequest appointmentRequest, String reason);

    /**
     * Unvoids an appointment request
     *
     * @param appointmentRequest the appointment request to unvoid.
     * @return the unvoid appointment request
     * <strong>Should</strong> unvoid given appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    AppointmentRequest unvoidAppointmentRequest(AppointmentRequest appointmentRequest);

    /**
     * Completely removes an appointment request from the database. This is not reversible.
     *
     * @param appointmentRequest the appointment request to delete from the database.
     * <strong>Should</strong> delete given appointment request
     */
    @Authorized(AppointmentUtils.PRIV_REQUEST_APPOINTMENTS)
    void purgeAppointmentRequest(AppointmentRequest appointmentRequest);

	/**
	 * Retrieves the most recent appointment for a given patient.
	 * 
	 * @param patient the patient for which we are retrieving.
	 * @return The most recent appointment for the given patient, null if no appointments were set.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Appointment getLastAppointment(Patient patient);

	/**
	 * Return a list of time slots that stands within the given constraints.
	 * 
	 * @param appointmentType - Type of the appointment
	 * @param fromDate - (optional) earliest start date. (defaults to current date)
	 * @param toDate - (optional) latest start date.
	 * @param provider - (optional) the appointment's provider.
	 * @param location - (optional) the appointment's location. (or predecessor location)
	 * @return List of TimeSlots that stands within the given constraints, null if illegal values
	 *         (fromDate>toDate or null appointmentType)
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType,
			Date fromDate, Date toDate, Provider provider, Location location)
			throws APIException;

    /**
     * Return a list of time slots that stands within the given constraints.
     *
     * @param appointmentType - Type of the appointment
     * @param fromDate - (optional) earliest start date. (defaults to current date)
     * @param toDate - (optional) latest start date.
     * @param provider - (optional) the appointment's provider.
     * @param location - (optional) the appointment's location. (or predecessor location)
     * @param excludeTimeSlotsWithPatient (optional) will exclude all time slots that have an existing appointment for
     *                                    this patient for the selected service type that doesn't have a status type
     *                                    of CANCELLED (to prevent allow duplicate patient appointments for the same service and time)
     * @return List of TimeSlots that stands within the given constraints, null if illegal values
     *         (fromDate>toDate or null appointmentType)
     */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
    List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType,
                                             Date fromDate, Date toDate, Provider provider, Location location,
                                             Patient excludeTimeSlotsWithPatient)
            throws APIException;

	/**
	 * Return a list of time slots that stands within the given constraints.
	 * 
	 * @param appointmentType - Type of the appointment
	 * @param fromDate - (optional) earliest start date.
	 * @param toDate - (optional) latest start date.
	 * @param provider - (optional) the appointment's provider.
	 * @param location - (optional) the appointment's location. (or predecessor location)
	 * @return List of TimeSlots that stands within the given constraints, null if illegal values
	 *         (fromDate>toDate or null appointmentType)
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	List<TimeSlot> getTimeSlotsByConstraintsIncludingFull(
			AppointmentType appointmentType, Date fromDate, Date toDate,
			Provider provider, Location location) throws APIException;

    /**
     * Return a list of time slots that stands within the given constraints.
     *
     * @param appointmentType - Type of the appointment
     * @param fromDate - (optional) earliest start date.
     * @param toDate - (optional) latest start date.
     * @param provider - (optional) the appointment's provider.
     * @param location - (optional) the appointment's location. (or predecessor location)
     * @param excludeTimeSlotsWithPatient optional) will exclude all time slots that have an existing appointment for
     *                                    this patient for the selected service type that doesn't have a status type
     *                                    of CANCELLED (to prevent allow duplicate patient appointments for the same service and time)
     * @return List of TimeSlots that stands within the given constraints, null if illegal values
     *         (fromDate>toDate or null appointmentType)
     */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
    List<TimeSlot> getTimeSlotsByConstraintsIncludingFull(
            AppointmentType appointmentType, Date fromDate, Date toDate,
            Provider provider, Location location, Patient excludeTimeSlotsWithPatient) throws APIException;

	/**
	 * Returns a list of strings, where each string represents an identifier of the given patient
	 * and its value. The preferred identifier will be the first in the list. The format of each
	 * string will be: "<identifier name>: <identifier value>" for example:
	 * "Old Identification Number: 2142"
	 * 
	 * @param patient the patient.
	 * @return a list of strings where each string represents an identifier of the patient.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<String> getPatientIdentifiersRepresentation(Patient patient);

	/**
	 * Returns the amount of minutes left in a given time slot.
	 * 
	 * @param timeSlot the given time slot.
	 * @return The amount of minutes left in the given time slot. Returns null if the given time
	 *         slot was null;
	 * <strong>Should</strong> ignore appointments with statuses that reflect a "cancelled" appointment
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_BLOCKS)
	Integer getTimeLeftInTimeSlot(TimeSlot timeSlot);

	/**
	 * [Utility Method] Returns all the descendants of a given location recursively. Call with null
	 * descendants.
	 * 
	 * @param location the location that is ancestor to all of the location in the returned set.
	 * @param descendants the result set which is being built recursively.
	 * @return A set that contains all of the descendants of the given location.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Set<Location> getAllLocationDescendants(Location location,
			Set<Location> descendants);

	/**
	 * Retrieves Appointments that satisfy the given constraints
	 * 
	 * @param fromDate - The appointment start date
	 * @param toDate - The appointment end date
	 * @param location - The appointment location
	 * @param provider - The appointment provider
	 * @param type - The appointment type
	 * @param status - The appointment status
	 * @return a list of appointments that satisfy the given constraints
     * <strong>Should</strong> sort by associated time slot
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate,
			Location location, Provider provider, AppointmentType type,
			AppointmentStatus status) throws APIException;

	/**
	 * Retrieves Appointments that satisfy the given constraints
	 * 
	 * @param fromDate - The appointment start date
	 * @param toDate - The appointment end date
	 * @param location - The appointment location
	 * @param provider - The appointment provider
	 * @param type - The appointment type
	 * @param status - The appointment status
	 * @param patient - The patient
	 * @return a list of appointments that satisfy the given constraints
     * <strong>Should</strong> sort by associated time slot
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate,
			Location location, Provider provider, AppointmentType type,
			Patient patient, AppointmentStatus status) throws APIException;

	/**
	 * Retrieves Appointments that satisfy the given constraints
	 * 
	 * @param fromDate - The appointment start date
	 * @param toDate - The appointment end date
	 * @param location - The appointment location
	 * @param provider - The appointment provider
	 * @param type - The appointment type
	 * @param patient - The patient
	 * @param appointmentStatuses - The appointment status list
	 * @return a list of appointments that satisfy the given constraints
     * <strong>Should</strong> sort by associated time slot
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate,
			Location location, Provider provider, AppointmentType type,
			Patient patient, List<AppointmentStatus> appointmentStatuses);

	/**
	 * Retrieves Appointments that satisfy the given constraints
	 *
	 * @param fromDate - The appointment start date
	 * @param toDate - The appointment end date
	 * @param location - The appointment location
	 * @param provider - The appointment provider
	 * @param type - The appointment type
	 * @param patient - The patient
	 * @param appointmentStatuses - The appointment status list
	 * @param visitType - The visit type of the appointment
	 * @param visit - The appointment visit
	 * @return a list of appointments that satisfy the given constraints
	 * <strong>Should</strong> sort by associated time slot
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate,
		   Location location, Provider provider, AppointmentType type,
		   Patient patient, List<AppointmentStatus> appointmentStatuses,
		   VisitType visitType, Visit visit) throws APIException;
	/**
	 * Retrives the start date of the current status of a given appointment.
	 * 
	 * @param appointment - The appointment.
	 * @return the start date of the current status of a given appointment.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	Date getAppointmentCurrentStatusStartDate(Appointment appointment);

	/**
	 * Changes the given appointment status.
	 * 
	 * @param appointment - The appointment
	 * @param newStatus - The new status
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	void changeAppointmentStatus(Appointment appointment,
			AppointmentStatus newStatus);

	/**
	 * Computes the average duration (in Minutes) of a status history by appointment type
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return A map of AppointmentType,Average duration pairs.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS_STATISTICS)
	Map<AppointmentType, Double> getAverageHistoryDurationByConditions(
			Date fromDate, Date endDate, AppointmentStatus status);

	/**
	 * Computes the average duration (in Minutes) of a status history by provider
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return A map of Provider,Average duration pairs.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS_STATISTICS)
	public Map<Provider, Double> getAverageHistoryDurationByConditionsPerProvider(
			Date fromDate, Date endDate, AppointmentStatus status);

	/**
	 * Retrieves the amount of status history objects in the given criteria
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return The amount of status history objects in the given criteria
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS_STATISTICS)
	Integer getHistoryCountByConditions(Date fromDate, Date endDate,
			AppointmentStatus status);

	/**
	 * Retrieves the distribution of appointment types in the given appointments dates range.
	 * 
	 * @param fromDate The lower bound of the date range.
	 * @param toDate The upper bound of the date range.
	 * @return Map of <AppointmentType,Integer> that reflects the appointment types distribution in
	 *         the given range.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS_STATISTICS)
	public Map<AppointmentType, Integer> getAppointmentTypeDistribution(
			Date fromDate, Date toDate);

	// Utility Methods

	/**
	 * [Utility Method] Retrieves all providers sorted ascending alphabetically
	 * 
	 * @param includeRetired whether to include retired providers
	 * @return sorted list of providers
    */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Provider> getAllProvidersSorted(boolean includeRetired);

	/**
	 * [Utility Method] Retrieves all appointment types sorted ascending alphabetically
	 * 
	 * @param includeRetired whether to include retired appointment types
	 * @return sorted list of appointment types
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENT_TYPES)
	List<AppointmentType> getAllAppointmentTypesSorted(boolean includeRetired);

	/**
	 * Retrieves list of unvoided appointments that their current status is one of the given states.
	 * 
	 * @param states List of states to retrieve by.
	 * @return list of unvoided appointments that their current status is one of the given states.
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getAppointmentsByStatus(List<AppointmentStatus> states);

	/**
	 * Update the status of PAST appointments according to the following conditions: "SCHEDULED"
	 * will be updated to "MISSED" "WAITING" or "WALKIN" will be updated to "MISSED"
	 * "INCONSULTATION" will be updated to "COMPLETED"
	 * 
	 * @return List of the updated appointments
	 */
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	List<Appointment> cleanOpenAppointments();

    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	boolean verifyDuplicatedAppointmentTypeName(AppointmentType appointmentType);

	/**
	 * Retrieves the list of scheduled (appts in states SCHEDULED or RESCHEDULED for a patient)
	 * 
	 * @param patient
	 * @return
	 */
    @Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getScheduledAppointmentsForPatient(Patient patient);

	/**
	 * Books a new appointment
	 * 
	 * @param appointment
	 * @param allowOverbook
	 * @return The newly-created appointment
	 * <strong>Should</strong> throw exception if this appointment has already been persisted
	 * <strong>Should</strong> throw exception if not enough available time in time slot and
	 *         allowOverbook = false
*/
    @Authorized(AppointmentUtils.PRIV_SCHEDULE_APPOINTMENTS)
	Appointment bookAppointment(Appointment appointment, Boolean allowOverbook)
			throws TimeSlotFullException;

	/**
	 * retrieves all the statuses of an appointment
	 * @param appointment
	 * @return
	 */
	@Authorized
	List<AppointmentStatusHistory> getAppointmentStatusHistories(Appointment appointment);

	/**
	 * retrieves the most recent status of an appointment
	 * @param appointment
	 * @return
	 */
	@Authorized()
	AppointmentStatusHistory getMostRecentAppointmentStatusHistory(Appointment appointment);
	/**
	 * returns list of early appointments
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getEarlyAppointments(Date fromDate, Date toDate,  Location location,
										   Provider provider, AppointmentType appointmentType) throws APIException;

	/**
	 * returns list of late appointments
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<Appointment> getLateAppointments(Date fromDate, Date toDate,  Location location,
										  Provider provider, AppointmentType appointmentType) throws APIException;
	 /** returns list of appointments aggregated by date
	 * @param fromDate
	 * @param toDate
	 * @param location
	 * @param provider
	 * @param status
	 * @return
	  */
	@Authorized(AppointmentUtils.PRIV_VIEW_APPOINTMENTS)
	List<AppointmentDailyCount> getAppointmentDailyCount(String fromDate, String toDate, Location location,
														 Provider provider, AppointmentStatus status) throws APIException;

	// ProviderSchedule

	/**
	 * Gets all provider schedule.
	 *
	 * @return a list of provider schedule objects.
	 * <strong>Should</strong> get all provider schedule
	 */
	@Authorized()
	List<ProviderSchedule> getAllProviderSchedules();

	/**
	 * Get all provider schedule based on includeVoided flag
	 *
	 * @param includeVoided
	 * @return List of all provider schedule
	 * <strong>Should</strong> get all provider schedule based on include voided flag.
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_PROVIDER_SCHEDULES)
	List<ProviderSchedule> getAllProviderSchedules(boolean includeVoided);

	/**
	 * Gets an provider schedule by its provider schedule id.
	 *
	 * @param ProviderScheduleId the provider schedule id.
	 * @return the provider schedule object found with the given id, else null.
	 * <strong>Should</strong> get correct provider schedule
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_PROVIDER_SCHEDULES)
	ProviderSchedule getProviderSchedule(Integer ProviderScheduleId);

	/**
	 * Gets an provider schedule by its UUID.
	 *
	 * @param uuid the provider schedule UUID.
	 * @return the provider schedule object found with the given uuid, else null.
	 * <strong>Should</strong> get correct provider schedule
	 */
	@Authorized()
	ProviderSchedule getProviderScheduleByUuid(String uuid);

	/**
	 * Creates or updates the given provider schedule in the database.
	 *
	 * @param providerSchedule the provider schedule to create or update.
	 * @return the created or updated provider schedule.
	 * <strong>Should</strong> save new provider schedule
	 * <strong>Should</strong> save a providerless provider schedule
	 * <strong>Should</strong> save edited provider schedule
	 * <strong>Should</strong> throw error when name is null
	 * <strong>Should</strong> throw error when name is empty string
	 */
	@Authorized(AppointmentUtils.PRIV_MANAGE_PROVIDER_SCHEDULES)
	ProviderSchedule saveProviderSchedule(ProviderSchedule providerSchedule)
			throws APIException;

	/**
	 * Voids a given provider schedule.
	 *
	 * @param providerSchedule the provider schedule to void.
	 * @param reason              the reason why the provider schedule is voided.
	 * @return the provider schedule that has been voided.
	 * <strong>Should</strong> void given provider schedule
	 * <strong>Should</strong> void all associated time slots
	 */
	@Authorized(AppointmentUtils.PRIV_MANAGE_PROVIDER_SCHEDULES)
	ProviderSchedule voidProviderSchedule(ProviderSchedule providerSchedule,
											 String reason);

	/**
	 * Completely removes an provider schedule from the database. This is not reversible.
	 *
	 * @param providerSchedule the provider schedule to delete from the database.
	 * <strong>Should</strong> delete given provider schedule
	 */
	@Authorized(AppointmentUtils.PRIV_MANAGE_PROVIDER_SCHEDULES)
	void purgeProviderSchedule(ProviderSchedule providerSchedule);

	/**
	 * Gets provider schedule which have a given date, location, provider and list of appointment
	 * types
	 *
	 * @return a list of provider schedule objects.
	 */
	@Authorized(AppointmentUtils.PRIV_VIEW_PROVIDER_SCHEDULES)
	List<ProviderSchedule> getProviderSchedulesByConstraints(Location locations, Provider provider, List<AppointmentType> appointmentTypes);

	@Authorized
	TimeSlot getTimeslotForAppointment(Location location, Provider provider, AppointmentType type, Date appointmentDate);

	@Authorized
	TimeSlot createTimeSlotUsingProviderSchedule(Date appointmentDate, Provider provider, Location location);

}
