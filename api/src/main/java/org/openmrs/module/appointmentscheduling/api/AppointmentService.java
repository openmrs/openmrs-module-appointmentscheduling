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
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.ScheduledAppointmentBlock;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured
 * in moduleApplicationContext.xml.
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
	 * @should get all appointment types
	 */
	Set<AppointmentType> getAllAppointmentTypes();
	
	/**
	 * Get all appointment types based on includeRetired flag
	 * 
	 * @param includeRetired
	 * @return List of all appointment types
	 * @should get all appointment types based on include retired flag.
	 */
	List<AppointmentType> getAllAppointmentTypes(boolean includeRetired);
	
	/**
	 * Gets an appointment type by its appointment type id.
	 * 
	 * @param appointmentTypeId the appointment type id.
	 * @return the appointment type object found with the given id, else null.
	 * @should get correct appointment type
	 */
	AppointmentType getAppointmentType(Integer appointmentTypeId);
	
	/**
	 * Gets an appointment type by its UUID.
	 * 
	 * @param uuid the appointment type UUID.
	 * @return the appointment type object found with the given uuid, else null.
	 * @should get correct appointment type
	 */
	AppointmentType getAppointmentTypeByUuid(String uuid);
	
	/**
	 * Gets all appointment types (including retired) whose names are similar to or contain the
	 * given search phrase.
	 * 
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @return a list of all appointment types with names similar to or containing the given phrase
	 * @should get correct appointment types
	 * @should include retired appointment types
	 */
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase);
	
	/**
	 * Gets all appointment types whose names are similar to or contain the given search phrase.
	 * 
	 * @param fuzzySearchPhrase the search phrase to use.
	 * @param includeRetired whether or not to include retired types
	 * @return a list of all appointment types with names similar to or containing the given phrase
	 * @should get correct appointment types
	 */
	List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase, boolean includeRetired);
	
	/**
	 * Creates or updates the given appointment type in the database.
	 * 
	 * @param appointmentType the appointment type to create or update.
	 * @return the created or updated appointment type.
	 * @should save new appointment type
	 * @should save edited appointment type
	 * @should throw error when name is null
	 * @should throw error when name is empty string
	 */
	AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException;
	
	/**
	 * Retires a given appointment type.
	 * 
	 * @param appointmentType the appointment type to retire.
	 * @param reason the reason why the appointment type is retired.
	 * @return the appointment type that has been retired.
	 * @should retire given appointment type
	 */
	AppointmentType retireAppointmentType(AppointmentType appointmentType, String reason);
	
	/**
	 * Unretires an appointment type.
	 * 
	 * @param appointmentType the appointment type to unretire.
	 * @return the unretired appointment type
	 * @should unretire given appointment type
	 */
	AppointmentType unretireAppointmentType(AppointmentType appointmentType);
	
	/**
	 * Completely removes an appointment type from the database. This is not reversible.
	 * 
	 * @param appointmentType the appointment type to delete from the database.
	 * @should delete given appointment type
	 */
	void purgeAppointmentType(AppointmentType appointmentType);
	
	//AppointmentBlock	
	/**
	 * Gets all appointment blocks.
	 * 
	 * @return a list of appointment block objects.
	 * @should get all appointment blocks
	 */
	List<AppointmentBlock> getAllAppointmentBlocks();
	
	/**
	 * Get all appointment blocks based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all appointment blocks
	 * @should get all appointment blocks based on include voided flag.
	 */
	List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided);
	
	/**
	 * Gets an appointment block by its appointment block id.
	 * 
	 * @param appointmentBlockId the appointment block id.
	 * @return the appointment block object found with the given id, else null.
	 * @should get correct appointment block
	 */
	AppointmentBlock getAppointmentBlock(Integer appointmentBlockId);
	
	/**
	 * Gets an appointment block by its UUID.
	 * 
	 * @param uuid the appointment block UUID.
	 * @return the appointment block object found with the given uuid, else null.
	 * @should get correct appointment block
	 */
	AppointmentBlock getAppointmentBlockByUuid(String uuid);
	
	/**
	 * Creates or updates the given appointment block in the database.
	 * 
	 * @param appointmentBlock the appointment block to create or update.
	 * @return the created or updated appointment block.
	 * @should save new appointment block
	 * @should save a providerless appointment block
	 * @should save edited appointment block
	 * @should throw error when name is null
	 * @should throw error when name is empty string
	 */
	AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock) throws APIException;
	
	/**
	 * Voids a given appointment block.
	 * 
	 * @param appointmentBlock the appointment block to void.
	 * @param reason the reason why the appointment block is voided.
	 * @return the appointment block that has been voided.
	 * @should void given appointment block
	 * @should void all associated time slots
	 */
	AppointmentBlock voidAppointmentBlock(AppointmentBlock appointmentBlock, String reason);
	
	/**
	 * Unvoids an appointment block.
	 * 
	 * @param appointmentBlock the appointment block to unvoid.
	 * @return the unvoided appointment block
	 * @should unvoided given appointment block
	 */
	AppointmentBlock unvoidAppointmentBlock(AppointmentBlock appointmentBlock);
	
	/**
	 * Completely removes an appointment block from the database. This is not reversible.
	 * 
	 * @param appointmentBlock the appointment block to delete from the database.
	 * @should delete given appointment block
	 */
	void purgeAppointmentBlock(AppointmentBlock appointmentBlock);
	
	/**
	 * Gets appointment blocks which have a given date and location.
	 * 
	 * @return a list of appointment block objects.
	 * @should get all appointment blocks which have contains in a given date interval and
	 *         corresponds to a given locations, provider and appointment type.
	 * @should not return voided appointment blocks
	 */
	List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate, String locations, Provider provider,
	        AppointmentType appointmentType);
	
	/**
	 * Gets appointment blocks which overlap to the given appointment block
	 * 
	 * @return a list of appointment block objects.
	 * @should get all appointment blocks which overlap to the given appointment block
	 * @should allow overlapping providerless appointment blocks
	 */
	List<AppointmentBlock> getOverlappingAppointmentBlocks(AppointmentBlock appointmentBlock);
	
	//Appointment
	/**
	 * Gets all appointments.
	 * 
	 * @return a list of appointment objects.
	 * @should get all appointment
	 */
	List<Appointment> getAllAppointments();
	
	/**
	 * Get all appointments based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all appointments
	 * @should get all appointments based on include voided flag.
	 */
	public List<Appointment> getAllAppointments(boolean includeVoided);
	
	/**
	 * Gets an appointment by its appointment id.
	 * 
	 * @param appointmentId the appointment id.
	 * @return the appointment object found with the given id, else null.
	 * @should get correct appointment
	 */
	Appointment getAppointment(Integer appointmentId);
	
	/**
	 * Gets an appointment by its UUID.
	 * 
	 * @param uuid the appointment UUID.
	 * @return the appointment object found with the given uuid, else null.
	 * @should get correct appointment
	 */
	Appointment getAppointmentByUuid(String uuid);
	
	/**
	 * Creates or updates the given appointment in the database.
	 * 
	 * @param appointment the appointment to create or update.
	 * @return the created or updated appointment.
	 * @should save new appointment
	 * @should save edited appointment
	 */
	Appointment saveAppointment(Appointment appointment) throws APIException;
	
	/**
	 * Voids a given appointment.
	 * 
	 * @param appointment the appointment to void.
	 * @param reason the reason why the appointment is voided.
	 * @return the appointment that has been voided.
	 * @should void given appointment
	 */
	Appointment voidAppointment(Appointment appointment, String reason);
	
	/**
	 * Unvoids an appointment.
	 * 
	 * @param appointment the appointment to unvoid.
	 * @return the unvoid appointment
	 * @should unvoid given appointment
	 */
	Appointment unvoidAppointment(Appointment appointment);
	
	/**
	 * Completely removes an appointment from the database. This is not reversible.
	 * 
	 * @param appointment the appointment to delete from the database.
	 * @should delete given appointment
	 */
	void purgeAppointment(Appointment appointment);
	
	/**
	 * Returns all Appointments for a given Patient
	 * 
	 * @param patientId the patient id to search by.
	 * @return all the appointments for the given patient id.
	 * @should return all of the appointments for the given patient.
	 */
	List<Appointment> getAppointmentsOfPatient(Patient patient);
	
	/**
	 * Returns the appointment corresponding to the given visit.
	 * 
	 * @param visitId the visit id to search by.
	 * @return the appointment that is related to this visit, null if there isnt any.
	 */
	Appointment getAppointmentByVisit(Visit visit);
	
	//TimeSlot
	
	/**
	 * Gets all time slots.
	 * 
	 * @return a list of time slot objects.
	 * @should get all time slots
	 */
	List<TimeSlot> getAllTimeSlots();
	
	/**
	 * Get all time slots based on includeVoided flag
	 * 
	 * @param includeVoided
	 * @return List of all time slots
	 * @should get all time slots based on include voided flag.
	 */
	public List<TimeSlot> getAllTimeSlots(boolean includeVoided);
	
	/**
	 * Creates or updates the given time slot in the database.
	 * 
	 * @param timeSlot the time slot to create or update.
	 * @return the created or updated time slot.
	 * @should save new time slot
	 * @should save edited time slot
	 */
	TimeSlot saveTimeSlot(TimeSlot timeSlot) throws APIException;
	
	/**
	 * Gets a a time slot by its id.
	 * 
	 * @param timeSlotId the time slot id.
	 * @return the time slot object found with the given id, else null.
	 * @should get correct time slot
	 */
	TimeSlot getTimeSlot(Integer timeSlotId);
	
	/**
	 * Gets a time slot by its UUID.
	 * 
	 * @param uuid the time slot UUID.
	 * @return the time slot object found with the given uuid, else null.
	 * @should get correct time slot
	 */
	TimeSlot getTimeSlotByUuid(String uuid);
	
	/**
	 * Voids a given time slot.
	 * 
	 * @param timeSlot the time slot to void.
	 * @param reason the reason why the time slot is voided.
	 * @return the time slot that has been voided.
	 * @should void given time slot
	 */
	TimeSlot voidTimeSlot(TimeSlot timeSlot, String reason);
	
	/**
	 * Unvoids a time slot.
	 * 
	 * @param timeSlot the time slot to unvoid.
	 * @return the unvoided time slot
	 * @should unvoid given time slot
	 */
	TimeSlot unvoidTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Completely removes a time slot from the database. This is not reversible.
	 * 
	 * @param timeSlot the time slot to delete from the database.
	 * @should delete given time slot
	 */
	void purgeTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Should retrieve all appointments in the given time slot.
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the appointments in the given time slot.
	 * @should not return voided appointments
	 */
	List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Should retrieve all appointments in the given time slot that do not have a status that means
	 * the appointment has been cancelled (ie status=CANCELLED, CANCELLED_AND_NEEDS_RESCHEDULE,
	 * MISSED, etc)
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the appointments in the given time slo
	 * @should not return missed, cancelled, and needs_reschedule appointments.
	 * @should not return voided appointments
	 */
	List<Appointment> getAppointmentsInTimeSlotThatAreNotCancelled(TimeSlot timeSlot);
	
	/**
	 * Gets a count of the number of appointments in a time slot
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the count of appointments in the given time slot
	 * @should not count voided appointments
	 */
	Integer getCountOfAppointmentsInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * Gets a count of all appointments in the given time slot that do not have a status that means
	 * the appointment has been cancelled (ie status=CANCELLED, CANCELLED_AND_NEEDS_RESCHEDULE,
	 * MISSED, etc)
	 * 
	 * @param timeSlot the time slot to search by.
	 * @return the count of appointments in the given time slot
	 * @should not count missed, cancelled and needs rescheduled appointments.
	 * @should not count voided appointments
	 */
	Integer getCountOfAppointmentsInTimeSlotThatAreNotCancelled(TimeSlot timeSlot);
	
	/**
	 * Should retrieve all time slots in the given appointment block.
	 * 
	 * @param appointmentBlock - the appointment block to search by.
	 * @return the time slots in the given appointment block.
	 * @should not return voided time slots
	 */
	List<TimeSlot> getTimeSlotsInAppointmentBlock(AppointmentBlock appointmentBlock);
	
	//Appointment Status History
	/**
	 * Gets all appointment status histories.
	 * 
	 * @return a list of appointment status history objects.
	 * @should get all appointment status histories
	 */
	List<AppointmentStatusHistory> getAllAppointmentStatusHistories();
	
	/**
	 * Gets an appointment status by its appointment status history id.
	 * 
	 * @param appointmentStatusHistoryId the appointment status history id.
	 * @return the appointment status history object found with the given id, else null.
	 * @should get correct appointment status history
	 */
	AppointmentStatusHistory getAppointmentStatusHistory(Integer appointmentStatusHistoryId);
	
	/**
	 * Gets all appointment status histories whose statuses are similar to or contain the given
	 * status.
	 * 
	 * @param status the search phrase to use.
	 * @return a list of all appointment status histories with names identical to or containing the
	 *         given status
	 * @should get correct appointment status histories
	 */
	List<AppointmentStatusHistory> getAppointmentStatusHistories(AppointmentStatus status);
	
	/**
	 * Creates or updates the given appointment status history in the database.
	 * 
	 * @param AppointmentStatusHistory the appointment status history to create or update.
	 * @return the created or updated appointment status history.
	 * @should save new appointment status history
	 * @should save edited appointment status history
	 */
	AppointmentStatusHistory saveAppointmentStatusHistory(AppointmentStatusHistory appointmentStatusHistory)
	        throws APIException;
	
	/**
	 * Retrieves the most recent appointment for a given patient.
	 * 
	 * @param patient the patient for which we are retrieving.
	 * @return The most recent appointment for the given patient, null if no appointments were set.
	 */
	Appointment getLastAppointment(Patient patient);
	
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
	List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate, Provider provider,
	        Location location) throws APIException;
	
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
	List<TimeSlot> getTimeSlotsByConstraintsIncludingFull(AppointmentType appointmentType, Date fromDate, Date toDate,
	        Provider provider, Location location) throws APIException;
	
	/**
	 * Returns a list of strings, where each string represents an identifier of the given patient
	 * and its value. The preferred identifier will be the first in the list. The format of each
	 * string will be: "<identifier name>: <identifier value>" for example:
	 * "Old Identification Number: 2142"
	 * 
	 * @param patient the patient.
	 * @return a list of strings where each string represents an identifier of the patient.
	 */
	List<String> getPatientIdentifiersRepresentation(Patient patient);
	
	/**
	 * Returns the amount of minutes left in a given time slot.
	 * 
	 * @param timeSlot the given time slot.
	 * @return The amount of minutes left in the given time slot. Returns null if the given time
	 *         slot was null;
	 * @should ignore appointments with statuses that reflect a "cancelled" appointment
	 */
	Integer getTimeLeftInTimeSlot(TimeSlot timeSlot);
	
	/**
	 * [Utility Method] Returns all the descendants of a given location recursively. Call with null
	 * descendants.
	 * 
	 * @param location the location that is ancestor to all of the location in the returned set.
	 * @param descendants the result set which is being built recursively.
	 * @return A set that contains all of the descendants of the given location.
	 */
	Set<Location> getAllLocationDescendants(Location location, Set<Location> descendants);
	
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
	 */
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Location location, Provider provider,
	        AppointmentType type, AppointmentStatus status) throws APIException;
	
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
	 */
	List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Location location, Provider provider,
	        AppointmentType type, AppointmentStatus status, Patient patient) throws APIException;
	
	/**
	 * Retrives the start date of the current status of a given appointment.
	 * 
	 * @param appointment - The appointment.
	 * @return the start date of the current status of a given appointment.
	 */
	Date getAppointmentCurrentStatusStartDate(Appointment appointment);
	
	/**
	 * Changes the given appointment status.
	 * 
	 * @param appointment - The appointment
	 * @param newStatus - The new status
	 */
	void changeAppointmentStatus(Appointment appointment, AppointmentStatus newStatus);
	
	/**
	 * Computes the average duration (in Minutes) of a status history by appointment type
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return A map of AppointmentType,Average duration pairs.
	 */
	Map<AppointmentType, Double> getAverageHistoryDurationByConditions(Date fromDate, Date endDate, AppointmentStatus status);
	
	/**
	 * Computes the average duration (in Minutes) of a status history by provider
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return A map of Provider,Average duration pairs.
	 */
	public Map<Provider, Double> getAverageHistoryDurationByConditionsPerProvider(Date fromDate, Date endDate,
	        AppointmentStatus status);
	
	/**
	 * Retrieves the amount of status history objects in the given criteria
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return The amount of status history objects in the given criteria
	 */
	Integer getHistoryCountByConditions(Date fromDate, Date endDate, AppointmentStatus status);
	
	/**
	 * Retrieves the distribution of appointment types in the given appointments dates range.
	 * 
	 * @param fromDate The lower bound of the date range.
	 * @param toDate The upper bound of the date range.
	 * @return Map of <AppointmentType,Integer> that reflects the appointment types distribution in
	 *         the given range.
	 */
	public Map<AppointmentType, Integer> getAppointmentTypeDistribution(Date fromDate, Date toDate);
	
	// Utility Methods
	
	/**
	 * [Utility Method] Retrieves all providers sorted ascending alphabetically
	 * 
	 * @param includeRetired whether to include retired providers
	 * @return sorted list of providers
	 */
	List<Provider> getAllProvidersSorted(boolean includeRetired);
	
	/**
	 * [Utility Method] Retrieves all appointment types sorted ascending alphabetically
	 * 
	 * @param includeRetired whether to include retired appointment types
	 * @return sorted list of appointment types
	 */
	List<AppointmentType> getAllAppointmentTypesSorted(boolean includeRetired);
	
	/**
	 * Retrieves list of unvoided appointments that their current status is one of the given states.
	 * 
	 * @param states List of states to retrieve by.
	 * @return list of unvoided appointments that their current status is one of the given states.
	 */
	List<Appointment> getAppointmentsByStatus(List<AppointmentStatus> states);
	
	/**
	 * Update the status of PAST appointments according to the following conditions: "SCHEDULED"
	 * will be updated to "MISSED" "WAITING" or "WALKIN" will be updated to "MISSED"
	 * "INCONSULTATION" will be updated to "COMPLETED"
	 * 
	 * @return List of the updated appointments
	 */
	List<Appointment> cleanOpenAppointments();
	
	boolean verifyDuplicatedAppointmentTypeName(AppointmentType appointmentType);
	
	/**
	 * Retrieves the list of scheduled (appts in states SCHEDULED or RESCHEDULED for a patient)
	 * 
	 * @param patient
	 * @return
	 */
	List<Appointment> getScheduledAppointmentsForPatient(Patient patient);
	
	/**
	 * Given an appointment block, this method creates a ScheduledAppointmentBlock convenience
	 * object that contains all the appointments in the block that are not voided or in one of the
	 * "cancelled" states
	 * 
	 * @param appointmentBlock
	 * @return
	 */
	ScheduledAppointmentBlock createScheduledAppointmentBlock(AppointmentBlock appointmentBlock);
	
	/**
	 * Gets all scheduled appointment blocks for a certain day at a certain location. Ignores any
	 * appointments that are voided or in one of the "cancelled" states
	 * 
	 * @param location
	 * @param date
	 * @return
	 */
	List<ScheduledAppointmentBlock> getDailyAppointmentBlocks(Location location, Date date);
	
	/**
	 * Books a new appointment
	 * 
	 * @param appointment
	 * @param allowOverbook
	 * @return The newly-created appointment
	 * @should throw exception if this appointment has already been persisted
	 * @should throw exception if not enough available time in time slot and allowOverbook = false
	 */
	Appointment bookAppointment(Appointment appointment, Boolean allowOverbook) throws TimeSlotFullException;
	
}
