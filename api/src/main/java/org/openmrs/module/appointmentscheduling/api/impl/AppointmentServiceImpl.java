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
package org.openmrs.module.appointmentscheduling.api.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.ScheduledAppointmentBlock;
import org.openmrs.module.appointmentscheduling.StudentT;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentBlockDAO;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentDAO;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentStatusHistoryDAO;
import org.openmrs.module.appointmentscheduling.api.db.AppointmentTypeDAO;
import org.openmrs.module.appointmentscheduling.api.db.TimeSlotDAO;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;
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
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAllAppointmentTypes()
	 */
	@Override
	@Transactional(readOnly = true)
	public Set<AppointmentType> getAllAppointmentTypes() {
		HashSet set = new HashSet();
		set.addAll(getAppointmentTypeDAO().getAll());
		return set;
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAllAppointmentTypes(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypes(boolean includeRetired) {
		return getAppointmentTypeDAO().getAll(includeRetired);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentType(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentType(Integer appointmentTypeId) {
		return (AppointmentType) getAppointmentTypeDAO().getById(appointmentTypeId);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentTypeByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentType getAppointmentTypeByUuid(String uuid) {
		return (AppointmentType) getAppointmentTypeDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentTypes(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase, boolean includeRetired) {
		return getAppointmentTypeDAO().getAppointmentTypes(fuzzySearchPhrase, includeRetired);
	}
	
	@Transactional(readOnly = true)
	public List<AppointmentType> getAppointmentTypes(String fuzzySearchPhrase) {
		return getAppointmentTypes(fuzzySearchPhrase, true);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#saveAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType saveAppointmentType(AppointmentType appointmentType) throws APIException {
		ValidateUtil.validate(appointmentType);
		return (AppointmentType) getAppointmentTypeDAO().saveOrUpdate(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#retireAppointmentType(org.openmrs.AppointmentType,
	 *      java.lang.String)
	 */
	public AppointmentType retireAppointmentType(AppointmentType appointmentType, String reason) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#unretireAppointmentType(org.openmrs.AppointmentType)
	 */
	public AppointmentType unretireAppointmentType(AppointmentType appointmentType) {
		return saveAppointmentType(appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#purgeAppointmentType(org.openmrs.AppointmentType)
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
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAllAppointmentBlocks()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks() {
		return getAppointmentBlockDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAllAppointmentBlocks(boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAllAppointmentBlocks(boolean includeVoided) {
		return getAppointmentBlockDAO().getAllData(includeVoided);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentBlock(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlock(Integer appointmentBlockId) {
		return (AppointmentBlock) getAppointmentBlockDAO().getById(appointmentBlockId);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentBlockByUuid(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public AppointmentBlock getAppointmentBlockByUuid(String uuid) {
		return (AppointmentBlock) getAppointmentBlockDAO().getByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#saveAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public AppointmentBlock saveAppointmentBlock(AppointmentBlock appointmentBlock) throws APIException {
		ValidateUtil.validate(appointmentBlock);
		return (AppointmentBlock) getAppointmentBlockDAO().saveOrUpdate(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#voidAppointmentBlock(org.openmrs.AppointmentBlock,
	 *      java.lang.String)
	 */
	public AppointmentBlock voidAppointmentBlock(AppointmentBlock appointmentBlock, String reason) {
		
		// first void all associated time slots
		for (TimeSlot timeSlot : Context.getService(AppointmentService.class).getTimeSlotsInAppointmentBlock(
		    appointmentBlock)) {
			Context.getService(AppointmentService.class).voidTimeSlot(timeSlot, reason);
		}
		
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#unvoidAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	// TODO what should this do regarding voided time slots within this block?
	public AppointmentBlock unvoidAppointmentBlock(AppointmentBlock appointmentBlock) {
		return saveAppointmentBlock(appointmentBlock);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#purgeAppointmentBlock(org.openmrs.AppointmentBlock)
	 */
	public void purgeAppointmentBlock(AppointmentBlock appointmentBlock) {
		getAppointmentBlockDAO().delete(appointmentBlock);
	}
	
	public void setAppointmentDAO(AppointmentDAO appointmentDAO) {
		this.appointmentDAO = appointmentDAO;
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentBlocks(java.util.Date,java.util.Date,java.util.String,org.openmrs.Provider,org.openmrs.AppointmentType)
	 *      )
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate, String locations, Provider provider,
	        AppointmentType appointmentType) {
		return getAppointmentBlockDAO().getAppointmentBlocks(fromDate, toDate, locations, provider, appointmentType);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getOverlappingAppointmentBlocks(org.openmrs.AppointmentBlock)
	 *      )
	 */
	@Transactional(readOnly = true)
	public List<AppointmentBlock> getOverlappingAppointmentBlocks(AppointmentBlock appointmentBlock) {
		return getAppointmentBlockDAO().getOverlappingAppointmentBlocks(appointmentBlock);
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
		getAppointmentStatusHistoryDAO().purgeHistoryBy(appointment);
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
		return getAppointmentDAO().getAppointmentsInTimeSlot(timeSlot);
	}
	
	@Override
	public List<Appointment> getAppointmentsInTimeSlotThatAreNotCancelled(TimeSlot timeSlot) {
		return getAppointmentDAO().getAppointmentsInTimeSlotByStatus(timeSlot,
		    AppointmentStatus.filter(AppointmentStatus.cancelledPredicate));
	}
	
	@Override
	public Integer getCountOfAppointmentsInTimeSlot(TimeSlot timeSlot) {
		return getAppointmentDAO().getCountOfAppointmentsInTimeSlot(timeSlot);
	}
	
	@Override
	public Integer getCountOfAppointmentsInTimeSlotThatAreNotCancelled(TimeSlot timeSlot) {
		return getAppointmentDAO().getCountOfAppointmentsInTimeSlotByStatus(timeSlot,
		    AppointmentStatus.filter(AppointmentStatus.cancelledPredicate));
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
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAllAppointmentStatusHistories()
	 */
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAllAppointmentStatusHistories() {
		return getAppointmentStatusHistoryDAO().getAll();
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentStatusHistory(java.lang.Integer)
	 */
	@Transactional(readOnly = true)
	public AppointmentStatusHistory getAppointmentStatusHistory(Integer appointmentStatusHistoryId) {
		return (AppointmentStatusHistory) getAppointmentStatusHistoryDAO().getById(appointmentStatusHistoryId);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#getAppointmentStatusHistories(java.lang.String)
	 */
	@Transactional(readOnly = true)
	public List<AppointmentStatusHistory> getAppointmentStatusHistories(AppointmentStatus status) {
		return getAppointmentStatusHistoryDAO().getAll(status);
	}
	
	/**
	 * @see org.openmrs.module.appointmentscheduling.api.AppointmentService#saveAppointmentStatusHistory(org.openmrs.AppointmentStatusHistory)
	 */
	public AppointmentStatusHistory saveAppointmentStatusHistory(AppointmentStatusHistory appointmentStatusHistory)
	        throws APIException {
		ValidateUtil.validate(appointmentStatusHistory);
		return (AppointmentStatusHistory) getAppointmentStatusHistoryDAO().saveOrUpdate(appointmentStatusHistory);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Appointment getLastAppointment(Patient patient) {
		return getAppointmentDAO().getLastAppointment(patient);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByConstraints(AppointmentType appointmentType, Date fromDate, Date toDate,
	        Provider provider, Location location) throws APIException {
		List<TimeSlot> suitableTimeSlots = getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate, toDate,
		    provider, location);
		
		List<TimeSlot> availableTimeSlots = new LinkedList<TimeSlot>();
		
		Integer duration = appointmentType.getDuration();
		for (TimeSlot slot : suitableTimeSlots) {
			
			//Filter by time left
			if (getTimeLeftInTimeSlot(slot) >= duration)
				availableTimeSlots.add(slot);
		}
		
		return availableTimeSlots;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<TimeSlot> getTimeSlotsByConstraintsIncludingFull(AppointmentType appointmentType, Date fromDate,
	        Date toDate, Provider provider, Location location) throws APIException {
		List<TimeSlot> suitableTimeSlots = getTimeSlotDAO().getTimeSlotsByConstraints(appointmentType, fromDate, toDate,
		    provider);
		
		List<TimeSlot> availableTimeSlots = new LinkedList<TimeSlot>();
		
		// Used to update the session to the correct one
		if (location != null)
			location = Context.getLocationService().getLocation(location.getId());
		
		Set<Location> relevantLocations = getAllLocationDescendants(location, null);
		relevantLocations.add(location);
		
		for (TimeSlot slot : suitableTimeSlots) {
			
			//Filter by location
			if (location != null) {
				if (relevantLocations.contains(slot.getAppointmentBlock().getLocation()))
					availableTimeSlots.add(slot);
			} else
				availableTimeSlots.add(slot);
		}
		
		return availableTimeSlots;
		
	}
	
	@Override
	@Transactional(readOnly = true)
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
	@Transactional(readOnly = true)
	public Integer getTimeLeftInTimeSlot(TimeSlot timeSlot) {
		
		if (timeSlot == null) {
			return null;
		}
		
		Integer minutes = Minutes.minutesBetween(new DateTime(timeSlot.getStartDate()), new DateTime(timeSlot.getEndDate()))
		        .getMinutes();
		
		for (Appointment appointment : Context.getService(AppointmentService.class)
		        .getAppointmentsInTimeSlotThatAreNotCancelled(timeSlot)) {
			minutes = minutes - appointment.getAppointmentType().getDuration();
		}
		
		return minutes;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Set<Location> getAllLocationDescendants(Location location, Set<Location> descendants) {
		if (descendants == null)
			descendants = new HashSet<Location>();
		
		if (location != null) {
			Set<Location> childLocations = location.getChildLocations();
			for (Location childLocation : childLocations) {
				descendants.add(childLocation);
				getAllLocationDescendants(childLocation, descendants);
			}
		}
		
		return descendants;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByConstraints(Date fromDate, Date toDate, Location location, Provider provider,
	        AppointmentType type, AppointmentStatus status) throws APIException {
		
		List<Appointment> appointments = appointmentDAO.getAppointmentsByConstraints(fromDate, toDate, provider, type,
		    status);
		
		List<Appointment> appointmentsInLocation = new LinkedList<Appointment>();
		
		// Used to update the session to the correct one
		if (location != null)
			location = Context.getLocationService().getLocation(location.getId());
		
		Set<Location> relevantLocations = getAllLocationDescendants(location, null);
		relevantLocations.add(location);
		
		for (Appointment appointment : appointments) {
			boolean satisfyingConstraints = true;
			
			//Filter by location
			if (location != null) {
				if (relevantLocations.contains(appointment.getTimeSlot().getAppointmentBlock().getLocation()))
					appointmentsInLocation.add(appointment);
			} else
				appointmentsInLocation.add(appointment);
			
		}
		
		return appointmentsInLocation;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public Date getAppointmentCurrentStatusStartDate(Appointment appointment) {
		return appointmentStatusHistoryDAO.getStartDateOfCurrentStatus(appointment);
	}
	
	@Override
	public void changeAppointmentStatus(Appointment appointment, AppointmentStatus newStatus) {
		if (appointment != null) {
			AppointmentStatusHistory history = new AppointmentStatusHistory();
			history.setAppointment(appointment);
			history.setEndDate(new Date());
			history.setStartDate(getAppointmentCurrentStatusStartDate(appointment));
			history.setStatus(appointment.getStatus());
			
			saveAppointmentStatusHistory(history);
			
			appointment.setStatus(newStatus);
			saveAppointment(appointment);
			
			if (newStatus == AppointmentStatus.COMPLETED || newStatus == AppointmentStatus.CANCELLED
			        || newStatus == AppointmentStatus.MISSED) {
				Visit visit = appointment.getVisit();
				if (visit != null && visit.getStopDatetime() != null) {
					visit.setStopDatetime(new Date());
					Context.getVisitService().saveVisit(visit);
				}
			}
		}
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Provider> getAllProvidersSorted(boolean includeRetired) {
		List<Provider> providers = Context.getProviderService().getAllProviders(includeRetired);
		Collections.sort(providers, new Comparator<Provider>() {
			
			public int compare(Provider pr1, Provider pr2) {
				return pr1.getName().toLowerCase().compareTo(pr2.getName().toLowerCase());
			}
		});
		return providers;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<AppointmentType> getAllAppointmentTypesSorted(boolean includeRetired) {
		List<AppointmentType> appointmentTypes = Context.getService(AppointmentService.class).getAllAppointmentTypes(
		    includeRetired);
		Collections.sort(appointmentTypes, new Comparator<AppointmentType>() {
			
			public int compare(AppointmentType at1, AppointmentType at2) {
				return at1.getName().toLowerCase().compareTo(at2.getName().toLowerCase());
			}
		});
		return appointmentTypes;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Appointment> getAppointmentsByStatus(List<AppointmentStatus> states) {
		return appointmentDAO.getAppointmentsByStates(states);
	}
	
	@Override
	@Transactional(readOnly = false)
	public List<Appointment> cleanOpenAppointments() {
		List<AppointmentStatus> states = new LinkedList<AppointmentStatus>();
		states.add(AppointmentStatus.SCHEDULED);
		states.add(AppointmentStatus.WAITING);
		states.add(AppointmentStatus.WALKIN);
		states.add(AppointmentStatus.INCONSULTATION);
		
		List<Appointment> appointmentsInStates = appointmentDAO.getPastAppointmentsByStates(states);
		if (appointmentsInStates == null)
			return new LinkedList<Appointment>();
		Iterator<Appointment> iter = appointmentsInStates.iterator();
		Date now = Calendar.getInstance().getTime();
		while (iter.hasNext()) {
			Appointment appointment = iter.next();
			//Check if past appointment
			if (now.after(appointment.getTimeSlot().getEndDate())) {
				AppointmentStatus status = appointment.getStatus();
				switch (status) {
					case SCHEDULED:
					case WAITING:
					case WALKIN:
						changeAppointmentStatus(appointment, AppointmentStatus.MISSED);
						break;
					case INCONSULTATION:
						changeAppointmentStatus(appointment, AppointmentStatus.COMPLETED);
						break;
					default:
						//No need to change
						appointmentsInStates.remove(appointment);
						break;
				}
				
			} else {
				appointmentsInStates.remove(appointment);
			}
		}
		
		return appointmentsInStates;
	}
	
	public boolean verifyDuplicatedAppointmentTypeName(AppointmentType appointmentTypeName) {
		return appointmentTypeDAO.verifyDuplicatedAppointmentTypeName(appointmentTypeName);
	}
	
	public List<Appointment> getScheduledAppointmentsForPatient(Patient patient) {
		return appointmentDAO.getScheduledAppointmentsForPatient(patient);
	}
	
	public Map<AppointmentType, Double> getAverageHistoryDurationByConditions(Date fromDate, Date endDate,
	        AppointmentStatus status) {
		Map<AppointmentType, Double> averages = new HashMap<AppointmentType, Double>();
		Map<AppointmentType, Integer> counters = new HashMap<AppointmentType, Integer>();
		
		List<AppointmentStatusHistory> histories = appointmentStatusHistoryDAO.getHistoriesByInterval(fromDate, endDate,
		    status);
		
		//Clean Not-Reasonable Durations
		Map<AppointmentStatusHistory, Double> durations = new HashMap<AppointmentStatusHistory, Double>();
		// 60 seconds * 1000 milliseconds in 1 minute
		int minutesConversion = 60000;
		int minutesInADay = 1440;
		for (AppointmentStatusHistory history : histories) {
			Date startDate = history.getStartDate();
			Date toDate = history.getEndDate();
			Double duration = (double) ((toDate.getTime() / minutesConversion) - (startDate.getTime() / minutesConversion));
			
			//Not reasonable to be more than a day
			if (duration < minutesInADay)
				durations.put(history, duration);
		}
		
		Double[] data = new Double[durations.size()];
		
		int i = 0;
		for (Map.Entry<AppointmentStatusHistory, Double> entry : durations.entrySet()) {
			//Added Math.sqrt in order to lower the mean and variance
			data[i] = Math.sqrt(entry.getValue());
			i++;
		}
		
		// Compute Intervals
		double[] boundaries = confidenceInterval(data);
		//
		
		//sum up the durations by type
		for (Map.Entry<AppointmentStatusHistory, Double> entry : durations.entrySet()) {
			AppointmentType type = entry.getKey().getAppointment().getAppointmentType();
			Double duration = entry.getValue();
			
			//Added Math.sqrt in order to lower the mean and variance
			if ((Math.sqrt(duration) <= boundaries[1])) {
				if (averages.containsKey(type)) {
					averages.put(type, averages.get(type) + duration);
					counters.put(type, counters.get(type) + 1);
				} else {
					averages.put(type, duration);
					counters.put(type, 1);
				}
			}
		}
		
		// Compute average
		for (Map.Entry<AppointmentType, Integer> counter : counters.entrySet())
			averages.put(counter.getKey(), averages.get(counter.getKey()) / counter.getValue());
		
		return averages;
	}
	
	@Transactional(readOnly = true)
	public Map<Provider, Double> getAverageHistoryDurationByConditionsPerProvider(Date fromDate, Date endDate,
	        AppointmentStatus status) {
		Map<Provider, Double> averages = new HashMap<Provider, Double>();
		Map<Provider, Integer> counters = new HashMap<Provider, Integer>();
		
		List<AppointmentStatusHistory> histories = appointmentStatusHistoryDAO.getHistoriesByInterval(fromDate, endDate,
		    status);
		
		//Clean Not-Reasonable Durations
		Map<AppointmentStatusHistory, Double> durations = new HashMap<AppointmentStatusHistory, Double>();
		// 60 seconds * 1000 milliseconds in 1 minute
		int minutesConversion = 60000;
		int minutesInADay = 1440;
		for (AppointmentStatusHistory history : histories) {
			Date startDate = history.getStartDate();
			Date toDate = history.getEndDate();
			Double duration = (double) ((toDate.getTime() / minutesConversion) - (startDate.getTime() / minutesConversion));
			//Not reasonable to be more than a day
			if (duration > 0 && duration < minutesInADay)
				durations.put(history, duration);
		}
		
		Double[] data = new Double[durations.size()];
		
		int i = 0;
		for (Map.Entry<AppointmentStatusHistory, Double> entry : durations.entrySet()) {
			//Added Math.sqrt in order to lower the mean and variance
			data[i] = Math.sqrt(entry.getValue());
			i++;
		}
		
		// Compute Intervals
		double[] boundaries = confidenceInterval(data);
		//
		
		//sum up the durations by type
		for (Map.Entry<AppointmentStatusHistory, Double> entry : durations.entrySet()) {
			Provider provider = entry.getKey().getAppointment().getTimeSlot().getAppointmentBlock().getProvider();
			Double duration = entry.getValue();
			
			//Added Math.sqrt in order to lower the mean and variance
			if ((Math.sqrt(duration) <= boundaries[1])) {
				if (averages.containsKey(provider)) {
					averages.put(provider, averages.get(provider) + duration);
					counters.put(provider, counters.get(provider) + 1);
				} else {
					averages.put(provider, duration);
					counters.put(provider, 1);
				}
			}
		}
		
		// Compute average
		for (Map.Entry<Provider, Integer> counter : counters.entrySet())
			averages.put(counter.getKey(), averages.get(counter.getKey()) / counter.getValue());
		
		return averages;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Integer getHistoryCountByConditions(Date fromDate, Date endDate, AppointmentStatus status) {
		List<AppointmentStatusHistory> histories = appointmentStatusHistoryDAO.getHistoriesByInterval(fromDate, endDate,
		    status);
		
		return histories.size();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<AppointmentType, Integer> getAppointmentTypeDistribution(Date fromDate, Date toDate) {
		List<AppointmentType> unretiredTypes = Context.getService(AppointmentService.class).getAllAppointmentTypes(false);
		Map<AppointmentType, Integer> distribution = new HashMap<AppointmentType, Integer>();
		
		for (AppointmentType type : unretiredTypes) {
			Integer countOfType = appointmentTypeDAO.getAppointmentTypeCount(fromDate, toDate, type);
			if (countOfType > 0)
				distribution.put(type, countOfType);
			
		}
		
		return distribution;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ScheduledAppointmentBlock> getDailyAppointmentBlocks(Location location, Date date) {
		AppointmentDAO appointmentDao = getAppointmentDAO();
		
		List<ScheduledAppointmentBlock> scheduledAppointmentBlockList = new ArrayList<ScheduledAppointmentBlock>();
		
		for (AppointmentBlock appointmentBlock : getAppointmentBlockList(location, date)) {
			
			ScheduledAppointmentBlock scheduledAppointmentBlock = createScheduledAppointmentBlock(appointmentBlock);
			
			if (!scheduledAppointmentBlock.getAppointments().isEmpty()) {
				scheduledAppointmentBlockList.add(scheduledAppointmentBlock);
			}
		}
		
		return scheduledAppointmentBlockList;
	}
	
	@Override
	@Transactional(readOnly = true)
	public ScheduledAppointmentBlock createScheduledAppointmentBlock(AppointmentBlock appointmentBlock) {
		List<Appointment> appointmentList = getAppointmentDAO().getAppointmentsByAppointmentBlock(appointmentBlock);
		
		// only include appointments that aren't "cancelled"
		CollectionUtils.filter(appointmentList, new Predicate() {
			
			List<AppointmentStatus> cancelledStatuses = AppointmentStatus.filter(AppointmentStatus.cancelledPredicate);
			
			@Override
			public boolean evaluate(Object o) {
				return cancelledStatuses.contains(((Appointment) o).getStatus());
			}
		});
		
		return new ScheduledAppointmentBlock(appointmentList, appointmentBlock);
	}
	
	@Override
	@Transactional
	public Appointment bookAppointment(Appointment appointment, Boolean allowOverbook) throws TimeSlotFullException {
		
		// can only book new appointments
		if (appointment.getId() != null) {
			throw new APIException("Cannot book appointment that has already been persisted");
		}
		
		// annoying that we have to do this, since it will be called during save, but otherwise we might get a NPE below if time slot or appointment type == null
		ValidateUtil.validate(appointment);
		
		if (!allowOverbook) {
			if (getTimeLeftInTimeSlot(appointment.getTimeSlot()) < appointment.getAppointmentType().getDuration()) {
				throw new TimeSlotFullException();
			}
		}
		
		appointment.setStatus(AppointmentStatus.SCHEDULED);
		return Context.getService(AppointmentService.class).saveAppointment(appointment);
	}
	
	private List<AppointmentBlock> getAppointmentBlockList(Location location, Date date) {
		return getAppointmentBlocks(setDateToStartOfDay(date), setDateToEndOfDay(date), location.getId().toString(), null,
		    null);
	}
	
	private Date setDateToEndOfDay(Date date) {
		return setupDate(date, 23, 59, 59);
	}
	
	private Date setDateToStartOfDay(Date date) {
		return setupDate(date, 0, 0, 0);
	}
	
	private Date setupDate(Date date, int hour, int minute, int second) {
		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(date);
		
		endDateCalendar.set(Calendar.HOUR, hour);
		endDateCalendar.set(Calendar.MINUTE, minute);
		endDateCalendar.set(Calendar.SECOND, second);
		return endDateCalendar.getTime();
	}
	
	private double[] confidenceInterval(Double[] data) {
		//Empty Dataset
		if (data.length == 0)
			return new double[] { 0.0, 0.0 };
		
		//Initialization
		double mean = 0;
		int count = data.length;
		int df = count - 1;
		//If Dataset consists of only one item
		if (df == 0)
			return new double[] { Double.MIN_VALUE, Double.MAX_VALUE };
		
		double alpha = 0.05;
		double tStat = StudentT.tTable(df, alpha);
		
		//Compute Mean
		for (double val : data)
			mean += val;
		mean = mean / count;
		
		//Compute Variance
		double variance = 0;
		for (double val : data)
			variance += Math.pow((val - mean), 2);
		variance = variance / df;
		//If deviation is small - Suspected as "Clean of Noise"
		if (Math.sqrt(variance) <= 1)
			return new double[] { Double.MIN_VALUE, Double.MAX_VALUE };
		
		//Compute Confidence Interval Bounds.
		double[] boundaries = new double[2];
		double factor = tStat * (Math.sqrt(variance) / Math.sqrt(count));
		boundaries[0] = mean - factor;
		boundaries[1] = mean + factor;
		
		return boundaries;
	}
}
