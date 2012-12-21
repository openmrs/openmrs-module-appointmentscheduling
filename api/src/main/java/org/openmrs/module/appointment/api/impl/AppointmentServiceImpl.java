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
package org.openmrs.module.appointment.api.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentStatusHistory;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.api.db.AppointmentBlockDAO;
import org.openmrs.module.appointment.api.db.AppointmentDAO;
import org.openmrs.module.appointment.api.db.AppointmentStatusHistoryDAO;
import org.openmrs.module.appointment.api.db.AppointmentTypeDAO;
import org.openmrs.module.appointment.api.db.TimeSlotDAO;
import org.openmrs.validator.ValidateUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * It is a default implementation of {@link AppointmentService}.
 */
public class AppointmentServiceImpl extends BaseOpenmrsService implements AppointmentService {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private AppointmentTypeDAO appointmentTypeDAO;
	
	private AppointmentBlockDAO appointmentBlockDAO;
	
	private AppointmentDAO appointmentDAO;
	
	private TimeSlotDAO timeSlotDAO;
	
	private AppointmentStatusHistoryDAO appointmentStatusHistoryDAO;
	
	/**
	 * @param dao the appointment type dao to set
	 */
	public void setAppointmentTypeDAO(AppointmentTypeDAO appointmentTypeDAO) {
		this.appointmentTypeDAO = appointmentTypeDAO;
	}
	
	/**
	 * @return the appointment type dao
	 */
	public AppointmentTypeDAO getAppointmentTypeDAO() {
		return appointmentTypeDAO;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes()
	 */
	@Transactional(readOnly = true)
	public Set<AppointmentType> getAllAppointmentTypes() {
		HashSet set = new HashSet();
		set.addAll(getAppointmentTypeDAO().getAll());
		return set;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) {
		return getAppointmentTypeDAO().getAll(includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentType(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentType(Integer appointmentTypeId) {
		return (AppointmentType) getAppointmentTypeDAO().getById(appointmentTypeId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypeByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentTypeByUuid(String uuid) {
		return (AppointmentType) getAppointmentTypeDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentTypes(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase) {
		return getAppointmentTypeDAO().getAll(fuzzySearchPhrase);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException {
		ValidateUtil.validate(appointmentType);
		return (AppointmentType) getAppointmentTypeDAO().saveOrUpdate(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#retireAppointmentType(org.openmrs.AppointmentType,
	 *      java.lang.String)
	 */
	public AppointmentType retireAppointmentType(AppointmentType appointmentType, String reason) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#unretireAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType unretireAppointmentType(AppointmentType appointmentType) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentType(org.openmrs.AppointmentType)
	 */
	public void purgeAppointmentType(AppointmentType appointmentType) {
		getAppointmentTypeDAO().delete(appointmentType);
	}
	
	//Appointment Block
	
	/**
	 * @param dao the appointment block dao to set
	 */
	public void setAppointmentBlockDAO(AppointmentBlockDAO appointmentBlockDAO) {
		this.appointmentBlockDAO = appointmentBlockDAO;
	}
	
	/**
	 * @return the appointment block dao
	 */
	public AppointmentBlockDAO getAppointmentBlockDAO() {
		return appointmentBlockDAO;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks() {
		return getAppointmentBlockDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentBlocks(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided) {
		return getAppointmentBlockDAO().getAllData(includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlock(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlock(Integer appointmentBlockId) {
		return (AppointmentBlock) getAppointmentBlockDAO().getById(appointmentBlockId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlockByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlockByUuid(String uuid) {
		return (AppointmentBlock) getAppointmentBlockDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock) throws APIException {
		ValidateUtil.validate(appointmentBlock);
		return (AppointmentBlock) getAppointmentBlockDAO().saveOrUpdate(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#voidAppointmentBlock(org.openmrs.AppointmentBlock,
	 *      java.lang.String)
	 */
	public AppointmentBlock voidAppointmentBlock(AppointmentBlock appointmentBlock, String reason) {
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#unvoidAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public AppointmentBlock unvoidAppointmentBlock(AppointmentBlock appointmentBlock) {
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#purgeAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public void purgeAppointmentBlock(AppointmentBlock appointmentBlock) {
		getAppointmentBlockDAO().delete(appointmentBlock);
	}
	
	public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
		this.appointmentDAO = appointmentDAO;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentBlocks(java.util.Date,java.util.Date,org.openmrs.Location)
	 *      )
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate, Location location) {
		return getAppointmentBlockDAO().getAppointmentBlocks(fromDate, toDate, location);
	}
	
	//Appointment
	/**
	 * @return the appointment dao
	 */
	public AppointmentDAO getAppointmentDAO() {
		return appointmentDAO;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAllAppointments() {
		return getAppointmentDAO().getAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAllAppointments(boolean includeVoided) {
		return getAppointmentDAO().getAllData(includeVoided);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getAppointment(Integer appointmentId) {
		return (Appointment) getAppointmentDAO().getById(appointmentId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getAppointmentByUuid(String uuid) {
		return (Appointment) getAppointmentDAO().getByUuid(uuid);
	}
	
	@Override
	public Appointment saveAppointment(Appointment appointment) throws APIException {
		ValidateUtil.validate(appointment);
		return (Appointment) getAppointmentDAO().saveOrUpdate(appointment);
	}
	
	@Override
	public Appointment voidAppointment(Appointment appointment, String reason) {
		return saveAppointment(appointment);
	}
	
	@Override
	public Appointment unvoidAppointment(Appointment appointment) {
		return saveAppointment(appointment);
	}
	
	@Override
	public void purgeAppointment(Appointment appointment) {
		getAppointmentDAO().delete(appointment);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsOfPatient(Patient patient) {
		return getAppointmentDAO().getAppointmentsByPatient(patient);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getAppointmentByVisit(Visit visit) {
		return getAppointmentDAO().getAppointmentByVisit(visit);
	}
	
	//TimeSlot
	/**
	 * @param dao the time slot dao to set
	 */
	public void setTimeSlotDAO(TimeSlotDAO timeSlotDAO) {
		this.timeSlotDAO = timeSlotDAO;
	}
	
	/**
	 * @return the time slot dao
	 */
	public TimeSlotDAO getTimeSlotDAO() {
		return timeSlotDAO;
	}
	
	@Override
	public TimeSlot saveTimeSlot(TimeSlot timeSlot) throws APIException {
		ValidateUtil.validate(timeSlot);
		return (TimeSlot) getTimeSlotDAO().saveOrUpdate(timeSlot);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getAllTimeSlots() {
		return getTimeSlotDAO().getAll();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getAllTimeSlots(boolean includeVoided) {
		return getTimeSlotDAO().getAllData(includeVoided);
	}
	
	@Override
	@Transactional(readOnly = true)
	public TimeSlot getTimeSlot(Integer timeSlotId) {
		return (TimeSlot) getTimeSlotDAO().getById(timeSlotId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public TimeSlot getTimeSlotByUuid(String uuid) {
		return (TimeSlot) getTimeSlotDAO().getByUuid(uuid);
	}
	
	@Override
	public TimeSlot voidTimeSlot(TimeSlot timeSlot, String reason) {
		return saveTimeSlot(timeSlot);
	}
	
	@Override
	public TimeSlot unvoidTimeSlot(TimeSlot timeSlot) {
		return saveTimeSlot(timeSlot);
	}
	
	@Override
	public void purgeTimeSlot(TimeSlot timeSlot) {
		getTimeSlotDAO().delete(timeSlot);
	}
	
	@Override
	public List<Appointment> getAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return getTimeSlotDAO().getAppointmentsInTimeSlot(timeSlot);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsInAppointmentBlock(AppointmentBlock appointmentBlock) {
		return getTimeSlotDAO().getTimeSlotsByAppointmentBlock(appointmentBlock);
	}
	
	//AppointmentStatusHistory
	/**
	 * @param dao the appointment status history dao to set
	 */
	public void setAppointmentStatusHistoryDAO(AppointmentStatusHistoryDAO appointmentStatusHistoryDAO) {
		this.appointmentStatusHistoryDAO = appointmentStatusHistoryDAO;
	}
	
	/**
	 * @return the appointment status dao
	 */
	public AppointmentStatusHistoryDAO getAppointmentStatusHistoryDAO() {
		return appointmentStatusHistoryDAO;
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAllAppointmentStatusHistories()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAllAppointmentStatusHistories() {
		return getAppointmentStatusHistoryDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentStatusHistory(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentStatusHistory getAppointmentStatusHistory(Integer appointmentStatusHistoryId) {
		return (AppointmentStatusHistory) getAppointmentStatusHistoryDAO().getById(appointmentStatusHistoryId);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#getAppointmentStatusHistories(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAppointmentStatusHistories(String status) {
		return getAppointmentStatusHistoryDAO().getAll(status);
	}
	
	/**
	 * @see org.openmrs.module.appointment.api.AppointmentService#saveAppointmentStatusHistory(org.openmrs.AppointmentStatusHistory)
	 */
	public AppointmentStatusHistory saveAppointmentStatusHistory(AppointmentStatusHistory appointmentStatusHistory)
	        throws APIException {
		ValidateUtil.validate(appointmentStatusHistory);
		return (AppointmentStatusHistory) getAppointmentStatusHistoryDAO().saveOrUpdate(appointmentStatusHistory);
	}
	
	@Override
	public Appointment getLastAppointment(Patient patient) {
		return getAppointmentDAO().getLastAppointment(patient);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate,
	        Provider provider, Location location) throws APIException {
		List<TimeSlot> suitableTimeSlots = getTimeSlotDAO().getTimeSlotsByConstraints(appointmentType, fromDate, toDate,
		    provider);
		
		List<TimeSlot> availableTimeSlots = new LinkedList<TimeSlot>();
		Set<Location> relevantLocations = getAllLocationDescendants(location, null);
		Integer duration = appointmentType.getDuration();
		for (TimeSlot slot : suitableTimeSlots) {
			boolean satisfyingConstraints = true;
			
			//Filter by location
			if (location != null) {
				if (!relevantLocations.contains(slot.getAppointmentBlock().getLocation()))
					satisfyingConstraints = false;
			}
			
			//Filter by time left
			if (satisfyingConstraints && getTimeLeftInTimeSlot(slot) < duration)
				satisfyingConstraints = false;
			
			if (satisfyingConstraints)
				availableTimeSlots.add(slot);
		}
		
		return availableTimeSlots;
		
	}
	
	@Override
	public List<String> getPatientIdentifiersRepresentation(Patient patient) {
		LinkedList<String> identifiers = new LinkedList<String>();
		
		if (patient == null)
			return identifiers;
		
		for (PatientIdentifier identifier : patient.getIdentifiers()) {
			//Representation format: <identifier type name> : <identifier value> 
			//for example: "OpenMRS Identification Number: 7532AM-1" 
			String representation = identifier.getIdentifierType().getName() + ": " + identifier.getIdentifier();
			//Put preferred identifier first.
			if (identifier.getPreferred())
				identifiers.add(0, representation);
			//Insert to the end of the list
			else
				identifiers.add(identifiers.size(), representation);
		}
		
		return identifiers;
	}
	
	@Override
	public Integer getTimeLeftInTimeSlot(TimeSlot timeSlot) {
		Integer timeLeft = null;
		
		if (timeSlot == null)
			return timeLeft;
		
		Date startDate = timeSlot.getStartDate();
		Date endDate = timeSlot.getEndDate();
		
		//Calculate total number of minutes in the time slot.
		timeLeft = (int) ((endDate.getTime() / 60000) - (startDate.getTime() / 60000));
		
		//Subtract from time left the amounts of minutes already scheduled
		List<Appointment> appointments = getAppointmentsInTimeSlot(timeSlot);
		for (Appointment appointment : appointments) {
			timeLeft -= appointment.getAppointmentType().getDuration();
		}
		
		return timeLeft;
	}
	
	@Override
	public Set<Location> getAllLocationDescendants(Location location, Set<Location> descendants) {
		if (descendants == null)
			descendants = new HashSet<Location>();
		
		if (location != null) {
			for (Location childLocation : location.getChildLocations()) {
				descendants.add(childLocation);
				getAllLocationDescendants(childLocation, descendants);
			}
		}
		
		return descendants;
	}
	
}
