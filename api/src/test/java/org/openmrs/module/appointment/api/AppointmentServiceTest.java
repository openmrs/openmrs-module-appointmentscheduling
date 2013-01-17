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
package org.openmrs.module.appointment.api;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.Appointment.AppointmentStatus;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests Appointment methods in the {@link $ AppointmentService} .
 */
public class AppointmentServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	private Integer amountOfAppointments = 4;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointments", method = "getAllAppointments()")
	public void getAllAppointments_shouldGetAllAppointments() throws Exception {
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(amountOfAppointments, (Integer) appointments.size());
	}
	
	@Test
	@Verifies(value = "should get correct appointment", method = "getAppointment(Integer)")
	public void getAppointment_shouldGetCorrectAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183601", appointment.getUuid());
		
		appointment = service.getAppointment(2);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602", appointment.getUuid());
		
		appointment = service.getAppointment(3);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183603", appointment.getUuid());
		
		appointment = service.getAppointment(5);
		Assert.assertNull(appointment);
	}
	
	@Test
	@Verifies(value = "should get correct appointment", method = "getAppointmentByUuid(String)")
	public void getAppointmentByUuid_shouldGetCorrectAppointment() throws Exception {
		Appointment appointment = service.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183601");
		assertNotNull(appointment);
		assertEquals((Integer) 1, appointment.getId());
		
		appointment = service.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183602");
		assertNotNull(appointment);
		assertEquals((Integer) 2, appointment.getId());
		
		appointment = service.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183603");
		assertNotNull(appointment);
		assertEquals((Integer) 3, appointment.getId());
		
		appointment = service.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183700");
		Assert.assertNull(appointment);
	}
	
	@Test
	@Verifies(value = "should save new appointment", method = "saveAppointment(Appointment)")
	public void saveAppointment_shouldSaveNewAppointment() throws Exception {
		TimeSlot timeSlot = new TimeSlot();
		timeSlot.setStartDate(new Date());
		timeSlot.setEndDate(new Date());
		timeSlot.setAppointmentBlock(service.getAppointmentBlock(1));
		service.saveTimeSlot(timeSlot);
		AppointmentType appointmentType = service.getAppointmentType(1);
		Appointment appointment = new Appointment(timeSlot, new Visit(1), new Patient(1), appointmentType,
		        AppointmentStatus.SCHEDULED);
		service.saveAppointment(appointment);
		
		//Should create a new appointment type row.
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(amountOfAppointments + 1, appointments.size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment", method = "saveAppointment(Appointment)")
	public void saveAppointment_shouldSaveEditedAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals((Integer) 1, appointment.getId());
		
		appointment.setStatus(AppointmentStatus.MISSED);
		service.saveAppointment(appointment);
		
		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals(AppointmentStatus.MISSED, appointment.getStatus());
		
		//Should not change the number of appointment types.
		assertEquals(amountOfAppointments, (Integer) service.getAllAppointments().size());
	}
	
	@Test
	@Verifies(value = "should void given appointment", method = "voidAppointment(Appointment, String)")
	public void voidAppointment_shouldVoidGivenAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		Assert.assertFalse(appointment.isVoided());
		Assert.assertNull(appointment.getVoidReason());
		
		service.voidAppointment(appointment, "void reason");
		
		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertTrue(appointment.isVoided());
		assertEquals("void reason", appointment.getVoidReason());
		
		//Should not change the number of appointment types.
		assertEquals(4, service.getAllAppointments().size());
	}
	
	@Test
	@Verifies(value = "should unvoid given appointment", method = "unvoidAppointment(Appointment)")
	public void unvoidAppointment_shouldUnvoidGivenAppointment() throws Exception {
		Appointment appointment = service.getAppointment(3);
		assertNotNull(appointment);
		assertTrue(appointment.isVoided());
		assertEquals("some void reason", appointment.getVoidReason());
		
		service.unvoidAppointment(appointment);
		
		appointment = service.getAppointment(3);
		assertNotNull(appointment);
		Assert.assertFalse(appointment.isVoided());
		Assert.assertNull(appointment.getVoidReason());
		
		//Should not change the number of appointment types.
		assertEquals(amountOfAppointments, (Integer) service.getAllAppointments().size());
	}
	
	@Test
	@Verifies(value = "should delete given appointment", method = "purgeAppointment(Appointment)")
	public void purgeAppointment_shouldDeleteGivenAppointment() throws Exception {
		Appointment appointment = service.getAppointment(3);
		assertNotNull(appointment);
		
		service.purgeAppointment(appointment);
		
		appointment = service.getAppointment(3);
		Assert.assertNull(appointment);
		
		//Should reduce the existing number of appointment types.
		assertEquals(amountOfAppointments - 1, service.getAllAppointments().size());
	}
	
	@Test
	@Verifies(value = "should get all appointments of patient", method = "getAppointmentsOfPatient(Integer patientId")
	public void getAppointmentsOfPatient_shouldGetAllAppointmentsOfPatient() throws Exception {
		List<Appointment> appointments = service.getAppointmentsOfPatient(new Patient(1));
		Assert.assertEquals(2, appointments.size());
		
		appointments = service.getAppointmentsOfPatient(new Patient(40));
		Assert.assertEquals(0, appointments.size());
	}
	
	@Test
	@Verifies(value = "should get appointment corresponding to visit", method = "getAppointmentByVisit(Integer visitId)")
	public void getAppointmentByVisit_shouldGetCorrectAppointment() {
		Appointment appointment = service.getAppointmentByVisit(new Visit(1));
		Assert.assertNotNull(appointment);
		
		appointment = service.getAppointmentByVisit(new Visit(13));
		Assert.assertNull(appointment);
	}
	
	@Test
	@Verifies(value = "should retrieve patient's most recent appointment, null if none scheduled", method = "getLastAppointment(Patient)")
	public void getLastAppointment_shouldRetrieveCorrectMostRecentAppointment() {
		Patient patient = Context.getPatientService().getPatient(1);
		assertNotNull(patient);
		
		Appointment appointment = null;
		appointment = service.getLastAppointment(null);
		Assert.assertNull(appointment);
		
		appointment = service.getLastAppointment(new Patient(5));
		Assert.assertNull(appointment);
		
		appointment = service.getLastAppointment(patient);
		assertEquals((Integer) 4, appointment.getAppointmentId());
		
		patient = Context.getPatientService().getPatient(2);
		appointment = service.getLastAppointment(patient);
		assertEquals((Integer) 2, appointment.getAppointmentId());
	}
	
	@Test
	@Verifies(value = "should retrieve correct representation", method = "getPatientIdentifiersRepresentation(Patient)")
	public void getPatientIdentifiersRepresentation_shouldGetCorrectRepresentation() {
		List<String> identifiers = service.getPatientIdentifiersRepresentation(null);
		assertEquals(0, identifiers.size());
		
		Patient patient = Context.getPatientService().getPatient(2);
		Assert.assertNotNull(patient);
		identifiers = service.getPatientIdentifiersRepresentation(patient);
		List<String> toCompare = new LinkedList<String>();
		toCompare.add(0, "OpenMRS Identification Number: 101-6");
		toCompare.add(toCompare.size(), "Old Identification Number: 101");
		assertEquals(identifiers, toCompare);
	}
	
	@Test
	@Verifies(value = "should retrieve correct descendants", method = "getAllLocationDescendants(Location , Set<Location> )")
	public void getAllLocationDescendants_shouldGetCorrectDescendants() {
		Location location = Context.getLocationService().getLocation(2);
		assertNotNull(location);
		
		Set<Location> descendants = new HashSet<Location>();
		descendants.add(Context.getLocationService().getLocation(3));
		descendants.add(Context.getLocationService().getLocation(4));
		
		assertEquals(descendants, service.getAllLocationDescendants(location, null));
	}
	
	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if an illegal date interval was given", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldThrowAPIException_getAppointmentsByConstraints() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2013-01-01 00:00:00.0");
		Date toDate = format.parse("2000-01-01 00:00:00.0");
		service.getAppointmentsByConstraints(fromDate, toDate, null, null, null, null);
	}
	
	@Test
	@Verifies(value = "Should Get All unvoided Appointments", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointments_getAppointmentsByConstraints() {
		List<Appointment> appointments = service.getAppointmentsByConstraints(null, null, null, null, null, null);
		assertEquals(amountOfAppointments - 1, appointments.size());
	}
	
	@Test
	@Verifies(value = "Should Get All unvoided Appointments in the given Date Interval", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByADateInterval_getAppointmentsByConstraints() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2006-01-01 00:00:00.0");
		
		List<Appointment> appointments = service.getAppointmentsByConstraints(fromDate, null, null, null, null, null);
		assertEquals(3, appointments.size());
		
		Date toDate = format.parse("2006-01-01 01:00:00.3");
		appointments = service.getAppointmentsByConstraints(null, toDate, null, null, null, null);
		assertEquals(3, appointments.size());
		
		fromDate = format.parse("2007-01-01 00:00:00.0");
		toDate = format.parse("2007-01-01 01:00:00.1");
		Appointment specificAppointment = service.getAppointment(4);
		assertNotNull(specificAppointment);
		appointments = service.getAppointmentsByConstraints(fromDate, toDate, null, null, null, null);
		assertEquals(specificAppointment, appointments.iterator().next());
		assertEquals(1, appointments.size());
	}
	
	@Test
	@Verifies(value = "Should get all unvoided appointments by provider", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByProvider_getAppointmentsByConstraints() {
		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);
		List<Appointment> appointments = service.getAppointmentsByConstraints(null, null, null, provider, null, null);
		assertEquals((Integer) (amountOfAppointments - 1), (Integer) appointments.size());
	}
	
	@Test
	@Verifies(value = "Should get all unvoided appointments by appointment type", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByType_getAppointmentsByConstraints() {
		AppointmentType type = service.getAppointmentType(1);
		assertNotNull(type);
		List<Appointment> appointments = service.getAppointmentsByConstraints(null, null, null, null, type, null);
		assertEquals((Integer) 2, (Integer) appointments.size());
		
		type = service.getAppointmentType(3);
		Appointment specificAppointment = service.getAppointment(4);
		appointments = service.getAppointmentsByConstraints(null, null, null, null, type, null);
		assertEquals(specificAppointment, appointments.iterator().next());
		assertEquals(1, appointments.size());
	}
	
	@Test
	@Verifies(value = "Should get all unvoided appointments by location", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, AppointmentStatus)")
	public void shouldGetAllUnvoidedAppointmentsByLocation_getAppointmentsByConstraints() {
		Location location = Context.getLocationService().getLocation(3);
		assertNotNull(location);
		List<Appointment> appointments = service.getAppointmentsByConstraints(null, null, location, null, null, null);
		assertEquals(3, appointments.size());
		
		location = Context.getLocationService().getLocation(2);
		assertNotNull(location);
		appointments = service.getAppointmentsByConstraints(null, null, location, null, null, null);
		assertEquals(3, appointments.size());
		
		location = Context.getLocationService().getLocation(4);
		assertNotNull(location);
		appointments = service.getAppointmentsByConstraints(null, null, location, null, null, null);
		assertTrue(appointments.isEmpty());
		
	}
	
	@Test
	@Verifies(value = "Should get all unvoided appointments by status", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, AppointmentStatus)")
	public void should_getAppointmentsByConstraints() {
		
		List<Appointment> appointments = service.getAppointmentsByConstraints(null, null, null, null, null,
		    AppointmentStatus.SCHEDULED);
		assertEquals(2, appointments.size());
		
		appointments = service.getAppointmentsByConstraints(null, null, null, null, null, AppointmentStatus.MISSED);
		Appointment specificAppointment = service.getAppointment(2);
		assertEquals(specificAppointment, appointments.iterator().next());
		assertEquals(1, appointments.size());
	}
	
}
