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

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests Appointment methods in the {@link$ AppointmentService} .
 */
public class AppointmentServiceTest extends BaseModuleContextSensitiveTest {

	private AppointmentService service;

	private static int TOTAL_APPOINTMENTS = 14;

	private static int TOTAL_APPOINTMENTS_EXCLUDING_VOIDED = 12;

	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}

	@Test
	@Verifies(value = "should get all appointments", method = "getAllAppointments()")
	public void getAllAppointments_shouldGetAllAppointments() throws Exception {
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(TOTAL_APPOINTMENTS, appointments.size());
	}

	@Test
	@Verifies(value = "should get correct appointment", method = "getAppointment(Integer)")
	public void getAppointment_shouldGetCorrectAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183601",
				appointment.getUuid());

		appointment = service.getAppointment(2);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointment.getUuid());

		appointment = service.getAppointment(3);
		assertNotNull(appointment);
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183603",
				appointment.getUuid());

		appointment = service.getAppointment(15);
		assertNull(appointment);
	}

	@Test
	@Verifies(value = "should get correct appointment", method = "getAppointmentByUuid(String)")
	public void getAppointmentByUuid_shouldGetCorrectAppointment()
			throws Exception {
		Appointment appointment = service
				.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183601");
		assertNotNull(appointment);
		assertEquals((Integer) 1, appointment.getId());

		appointment = service
				.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183602");
		assertNotNull(appointment);
		assertEquals((Integer) 2, appointment.getId());

		appointment = service
				.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183603");
		assertNotNull(appointment);
		assertEquals((Integer) 3, appointment.getId());

		appointment = service
				.getAppointmentByUuid("c0c579b0-8e59-401d-8a4a-976a0b183700");
		assertNull(appointment);
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
		Appointment appointment = new Appointment(timeSlot, new Visit(1),
				new Patient(1), appointmentType, AppointmentStatus.SCHEDULED);
		service.saveAppointment(appointment);

		// Should create a new appointment type row.
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(TOTAL_APPOINTMENTS + 1, appointments.size());
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

		// Should not change the number of appointment types.
		assertEquals(TOTAL_APPOINTMENTS, service.getAllAppointments().size());
	}

	@Test
	@Verifies(value = "should void given appointment", method = "voidAppointment(Appointment, String)")
	public void voidAppointment_shouldVoidGivenAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		Assert.assertFalse(appointment.isVoided());
		assertNull(appointment.getVoidReason());

		service.voidAppointment(appointment, "void reason");

		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertTrue(appointment.isVoided());
		assertEquals("void reason", appointment.getVoidReason());

		assertEquals(TOTAL_APPOINTMENTS, service.getAllAppointments().size());
	}

	@Test
	@Verifies(value = "should unvoid given appointment", method = "unvoidAppointment(Appointment)")
	public void unvoidAppointment_shouldUnvoidGivenAppointment()
			throws Exception {
		Appointment appointment = service.getAppointment(3);
		assertNotNull(appointment);
		assertTrue(appointment.isVoided());
		assertEquals("some void reason", appointment.getVoidReason());

		service.unvoidAppointment(appointment);

		appointment = service.getAppointment(3);
		assertNotNull(appointment);
		Assert.assertFalse(appointment.isVoided());
		assertNull(appointment.getVoidReason());

		// Should not change the number of appointment types.
		assertEquals(TOTAL_APPOINTMENTS, service.getAllAppointments().size());
	}

	@Test
	@Verifies(value = "should delete given appointment", method = "purgeAppointment(Appointment)")
	public void purgeAppointment_shouldDeleteGivenAppointment()
			throws Exception {
		Appointment appointment = service.getAppointment(3);
		assertNotNull(appointment);

		service.purgeAppointment(appointment);

		appointment = service.getAppointment(3);
		assertNull(appointment);

		// Should reduce the existing number of appointment types.
		assertEquals(TOTAL_APPOINTMENTS - 1, service.getAllAppointments()
				.size());
	}

	@Test
	@Verifies(value = "should get all appointments of patient", method = "getAppointmentsOfPatient(Integer patientId")
	public void getAppointmentsOfPatient_shouldGetAllAppointmentsOfPatient()
			throws Exception {
		List<Appointment> appointments = service
				.getAppointmentsOfPatient(new Patient(1));
		assertEquals(6, appointments.size());

		appointments = service.getAppointmentsOfPatient(new Patient(40));
		assertEquals(0, appointments.size());
	}

	@Test
	@Verifies(value = "should get appointment corresponding to visit", method = "getAppointmentByVisit(Integer visitId)")
	public void getAppointmentByVisit_shouldGetCorrectAppointment() {
		Appointment appointment = service.getAppointmentByVisit(new Visit(1));
		Assert.assertNotNull(appointment);

		appointment = service.getAppointmentByVisit(new Visit(13));
		assertNull(appointment);
	}

	@Test
	@Verifies(value = "should retrieve patient's most recent appointment, null if none scheduled", method = "getLastAppointment(Patient)")
	public void getLastAppointment_shouldRetrieveCorrectMostRecentAppointment() {

		Appointment appointment = null;
		appointment = service.getLastAppointment(null);
		assertNull(appointment);

		appointment = service.getLastAppointment(new Patient(5));
		assertNull(appointment);

		Patient patient = Context.getPatientService().getPatient(1);
		assertNotNull(patient);

		appointment = service.getLastAppointment(patient);
		assertEquals(7, appointment.getAppointmentId().intValue());

		patient = Context.getPatientService().getPatient(2);
		appointment = service.getLastAppointment(patient);
		assertEquals(9, appointment.getAppointmentId().intValue());
	}

	@Test
	@Verifies(value = "should retrieve correct representation", method = "getPatientIdentifiersRepresentation(Patient)")
	public void getPatientIdentifiersRepresentation_shouldGetCorrectRepresentation() {
		List<String> identifiers = service
				.getPatientIdentifiersRepresentation(null);
		assertEquals(0, identifiers.size());

		Patient patient = Context.getPatientService().getPatient(2);
		Assert.assertNotNull(patient);
		identifiers = service.getPatientIdentifiersRepresentation(patient);
		List<String> toCompare = new LinkedList<String>();
		toCompare.add(0, "OpenMRS Identification Number: 101-6");
		toCompare.add(toCompare.size(), "Old Identification Number: 101");
		assertEquals(identifiers, toCompare);
	}

	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if an illegal date interval was given", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldThrowAPIException_getAppointmentsByConstraints()
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2013-01-01 00:00:00.0");
		Date toDate = format.parse("2000-01-01 00:00:00.0");
		service.getAppointmentsByConstraints(fromDate, toDate, null, null,
				null, null);
	}

	@Test
	@Verifies(value = "Should Get All unvoided Appointments", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointments_getAppointmentsByConstraints() {
		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, null, null);
		assertEquals(TOTAL_APPOINTMENTS_EXCLUDING_VOIDED, appointments.size());
	}

	@Test
	@Verifies(value = "Should Get All unvoided Appointments in the given Date Interval", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByADateInterval_getAppointmentsByConstraints()
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2006-01-01 00:00:00.0");

		List<Appointment> appointments = service.getAppointmentsByConstraints(
				fromDate, null, null, null, null, null);
		assertEquals(8, appointments.size());

		Date toDate = format.parse("2006-01-01 01:00:00.3");
		appointments = service.getAppointmentsByConstraints(null, toDate, null,
				null, null, null);
		assertEquals(5, appointments.size());

		fromDate = format.parse("2007-01-01 00:00:00.0");
		toDate = format.parse("2007-01-01 01:00:00.1");
		Appointment specificAppointment = service.getAppointment(4);
		assertNotNull(specificAppointment);
		appointments = service.getAppointmentsByConstraints(fromDate, toDate,
				null, null, null, null);
		assertEquals(specificAppointment, appointments.iterator().next());
		assertEquals(1, appointments.size());
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by provider", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByProvider_getAppointmentsByConstraints() {
		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);
		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, provider, null, null);
		assertEquals((Integer) (11), (Integer) appointments.size());
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by appointment type", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, String)")
	public void shouldGetAllUnvoidedAppointmentsByType_getAppointmentsByConstraints() {
		AppointmentType type = service.getAppointmentType(1);
		assertNotNull(type);
		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, type, null);
		assertEquals(7, appointments.size());

		type = service.getAppointmentType(3);
		Appointment specificAppointment = service.getAppointment(5);
		appointments = service.getAppointmentsByConstraints(null, null, null,
				null, type, null);   // apt #5 should be first because we are sorting by time slot
		assertEquals(specificAppointment, appointments.iterator().next());
		assertEquals(5, appointments.size());
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by location", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, AppointmentStatus)")
	public void shouldGetAllUnvoidedAppointmentsByLocation_getAppointmentsByConstraints() {
		Location location = Context.getLocationService().getLocation(3);
		assertNotNull(location);
		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, location, null, null, null);
		assertEquals(8, appointments.size());

		location = Context.getLocationService().getLocation(2);
		assertNotNull(location);
		appointments = service.getAppointmentsByConstraints(null, null,
				location, null, null, null);
		assertEquals(12, appointments.size());

		location = Context.getLocationService().getLocation(4);
		assertNotNull(location);
		appointments = service.getAppointmentsByConstraints(null, null,
				location, null, null, null);
		assertTrue(appointments.isEmpty());
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by status", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, AppointmentStatus)")
	public void shouldgetAllUnvoidedAppointmentsByStatus_getAppointmentsByConstraints() {

		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, null, AppointmentStatus.SCHEDULED);
		assertEquals(3, appointments.size());

		appointments = service.getAppointmentsByConstraints(null, null, null,
				null, null, AppointmentStatus.MISSED);
		assertEquals(2, appointments.size());;
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by patient", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, AppointmentStatus, Patient)")
	public void shouldgetAllUnvoidedAppointmentsByPatient_getAppointmentsByConstraints() {

		Patient patient = Context.getPatientService().getPatient(2);

		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, null, patient, new ArrayList<AppointmentStatus>());
		assertEquals(4, appointments.size());
		assertEquals(patient, appointments.get(0).getPatient());
		assertEquals(patient, appointments.get(1).getPatient());
	}

	@Test
	@Verifies(value = "Should get all unvoided appointments by patient with Scheduled and Rescheduled statuses", method = "getAppointmentsByConstraints(Date, Date, Location, Provider, AppointmentType, List<AppointmentStatus>, Patient)")
	public void shouldgetAllUnvoidedAppointmentsByPatientAndStatus_getAppointmentsByConstraints() {

		Patient patient = Context.getPatientService().getPatient(1);

		List<AppointmentStatus> appointmentStatuses = Arrays.asList(
				AppointmentStatus.SCHEDULED, AppointmentStatus.RESCHEDULED);

		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, null, patient, appointmentStatuses);
		assertEquals(4, appointments.size());
		assertEquals(patient, appointments.get(0).getPatient());
		assertEquals(patient, appointments.get(1).getPatient());
		assertEquals(patient, appointments.get(2).getPatient());
		assertEquals(patient, appointments.get(3).getPatient());
	}

    @Test
    public void shouldProperlySortAppointmentsByTimeSlotDate_getAppointmentsByConstraints() {

        Patient patient = Context.getPatientService().getPatient(1);

        List<AppointmentStatus> appointmentStatuses = Arrays.asList(
                AppointmentStatus.SCHEDULED, AppointmentStatus.RESCHEDULED);

        List<Appointment> appointments = service.getAppointmentsByConstraints(
                null, null, null, null, null, patient, appointmentStatuses);

        assertNotNull(appointments);
        assertEquals(4, appointments.size());

        // verify they are in order
        assertEquals(new Integer(6), appointments.get(0).getId());
        assertEquals(new Integer(1), appointments.get(1).getId());
        assertEquals(new Integer(4), appointments.get(2).getId());
        assertEquals(new Integer(7), appointments.get(3).getId());
    }

	@Test
	public void shouldProperlySortAppointmentsByVisit_getAppointmentsByConstraints() {

		Visit visit = Context.getVisitService().getVisit(1);

		List<Appointment> appointments = service.getAppointmentsByConstraints(
				null, null, null, null, null, null, null, null, visit);

		assertNotNull(appointments);
		assertEquals(1, appointments.size());

		visit = Context.getVisitService().getVisit(2);
		assertNotNull(visit);
		appointments = service.getAppointmentsByConstraints(null, null,
				null, null, null, null, null, null, visit);
		assertTrue(!appointments.isEmpty());
	}


	@Test
	@Verifies(value = "Should get correct appointments", method = "getAppointmentsByStatus(List<AppointmentStatus>)")
	public void shouldGetCorrectAppointments_getAppointmentsByStatus() {
		List<AppointmentStatus> states = new LinkedList<AppointmentStatus>();
		Appointment appointment = null;
		List<Appointment> result = null;

		// "SCHEDULED": 1, 4, 6
		states.add(AppointmentStatus.SCHEDULED);
		result = service.getAppointmentsByStatus(states);
		assertEquals(3, result.size());
		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(4);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(7);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));

		// +"MISSED": 2 5
		states.add(AppointmentStatus.MISSED);
		result = service.getAppointmentsByStatus(states);
		assertEquals(5, result.size());
		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(4);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(7);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(2);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
		appointment = service.getAppointment(5);
		assertNotNull(appointment);
		assertTrue(result.contains(appointment));
	}

	@Test
	@Verifies(value = "Should change correct appointments", method = "cleanOpenAppointments()")
	public void shouldChangeCorrectAppointments_cleanOpenAppointments() {
		List<Appointment> result = service.cleanOpenAppointments();
		assertNotNull(result);
		assertEquals(5, result.size());

		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals(AppointmentStatus.MISSED, appointment.getStatus());

		appointment = service.getAppointment(4);
		assertNotNull(appointment);
		assertEquals(AppointmentStatus.MISSED, appointment.getStatus());

	}

	@Test
	public void shouldGetScheduledAppointmentsForPatientOrderedByStartData() {
		Patient patient = Context.getPatientService().getPatient(1);
		List<Appointment> appointments = service
				.getScheduledAppointmentsForPatient(patient);
		int expectedNumberOfScheduledAppointments = 4;

		assertNotNull(appointments);
		assertEquals(expectedNumberOfScheduledAppointments, appointments.size());

		// verify they are in order
		assertEquals(new Integer(6), appointments.get(0).getId());
		assertEquals(new Integer(1), appointments.get(1).getId());
		assertEquals(new Integer(4), appointments.get(2).getId());
		assertEquals(new Integer(7), appointments.get(3).getId());
	}

	@Test
	@Verifies(value = "retrieve all appointments scheduled in a given time slot", method = "getAppointmentsInTimeSlot(TimeSlot)")
	public void getAppointmentsInTimeSlot_shouldGetCorrectAppointments() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);

		List<Appointment> appointments = service
				.getAppointmentsInTimeSlot(timeSlot);
		assertNotNull(appointments);
		assertEquals(1, appointments.size()); // there are two appointments in this time slot, but one is voided

		timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		appointments = service.getAppointmentsInTimeSlot(timeSlot);
		assertNotNull(appointments);
		assertEquals(1, appointments.size());

	}

	@Test
	public void getCountOfAppointmentInTimeSlot_shouldGetCorrectCount() {
		TimeSlot timeSlot = service.getTimeSlot(8);
		assertEquals(new Integer(4), service.getCountOfAppointmentsInTimeSlot(timeSlot)); // there are five appointments in this time slot, but one is voided
	}

	@Test
	public void getAppointmentsInTimeSlotThatAreNotCancelled_shouldGetCorrectAppointments() {
		TimeSlot timeSlot = service.getTimeSlot(8);
		assertNotNull(timeSlot);

		List<Appointment> appointments = service
				.getAppointmentsInTimeSlotThatAreNotCancelled(timeSlot);
		assertNotNull(appointments);
		assertEquals(2, appointments.size()); // there are five appointments in this time slot, but one is voided, one is cancelled, and one is needs_reschedule

		Appointment appointment = service.getAppointment(7);
		assertTrue(appointments.contains(appointment));
		appointment = service.getAppointment(11);
		assertTrue(appointments.contains(appointment));
	}

	@Test
	public void getCountOfAppointmentsInTimeSlotThatAreNotCancelled_shouldGetCorrectAppointments() {
		TimeSlot timeSlot = service.getTimeSlot(8);
		assertEquals(new Integer(2), service.getCountOfAppointmentsInTimeSlotThatAreNotCancelled(timeSlot)); // there are five appointments in this time slot, but one is voided, one is need_reschedule, and one is missed
	}

	@Test(expected = APIException.class)
	public void bookAppointment_shouldFailIfAppointmentHasAlreadyBeenPersisted()
			throws Exception {
		Appointment appointment = service.getAppointment(1);
		service.bookAppointment(appointment, false);
	}

	@Test
	public void bookAppointment_shouldBookNewAppointment() throws Exception {

		TimeSlot timeSlot = service.getTimeSlot(4);
		AppointmentType appointmentType = service.getAppointmentType(1);
		Patient patient = Context.getPatientService().getPatient(1);

		Appointment appointment = new Appointment();
		appointment.setTimeSlot(timeSlot);
		appointment.setPatient(patient);
		appointment.setReason("follow-up");
		appointment.setAppointmentType(appointmentType);
		service.bookAppointment(appointment, false);

		assertNotNull(appointment.getAppointmentId());
		assertEquals(timeSlot, appointment.getTimeSlot());
		assertEquals(patient, appointment.getPatient());
		assertEquals("follow-up", appointment.getReason());
		assertEquals(appointmentType, appointment.getAppointmentType());
		assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
		assertNull(appointment.getVisit());
	}

	@Test(expected = TimeSlotFullException.class)
	public void bookAppointment_shouldNotBookNewAppointmentIfTimeSlotFull()
			throws Exception {

		TimeSlot timeSlot = service.getTimeSlot(1);
		AppointmentType appointmentType = service.getAppointmentType(1);
		Patient patient = Context.getPatientService().getPatient(1);

		Appointment appointment = new Appointment();
		appointment.setTimeSlot(timeSlot);
		appointment.setPatient(patient);
		appointment.setReason("follow-up");
		appointment.setAppointmentType(appointmentType);
		service.bookAppointment(appointment, false);

	}

	@Test
	public void bookAppointment_shouldBookNewAppointmentEvenIfTimeSlotFullIfOverbookSetToTrue()
			throws Exception {

		TimeSlot timeSlot = service.getTimeSlot(1);
		AppointmentType appointmentType = service.getAppointmentType(1);
		Patient patient = Context.getPatientService().getPatient(1);

		Appointment appointment = new Appointment();
		appointment.setTimeSlot(timeSlot);
		appointment.setPatient(patient);
		appointment.setReason("follow-up");
		appointment.setAppointmentType(appointmentType);
		service.bookAppointment(appointment, true);

		assertNotNull(appointment.getAppointmentId());
		assertEquals(timeSlot, appointment.getTimeSlot());
		assertEquals(patient, appointment.getPatient());
		assertEquals("follow-up", appointment.getReason());
		assertEquals(appointmentType, appointment.getAppointmentType());
		assertEquals(AppointmentStatus.SCHEDULED, appointment.getStatus());
		assertNull(appointment.getVisit());

	}

	@Test
	@Verifies(value = "should get all Early Appointments", method = "getEarlyAppointments(Date fromDate,Date toDate, Location location, Provider provider," +
			" AppointmentType appointmentType, AppointmentStatus status, VisitType visitType")
	public void getEarlyAppointments_shouldGetEarlyAppointments()
			throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = format.parse("2005-01-01");
		Date toDate = format.parse("2006-02-02");
		List<Appointment> appointments = service
				.getEarlyAppointments(fromDate, toDate, null, null, null);
		assertEquals(1, appointments.size());

	}

	@Test
	@Verifies(value = "should get all Late Appointments", method = "getLateAppointments(Date fromDate,Date toDate, Location location, Provider provider," +
			" AppointmentType appointmentType, AppointmentStatus status, VisitType visitType")
	public void getLateAppointments_shoulGetLateAppointments()
			throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = format.parse("2005-01-01");
		Date toDate = format.parse("2006-02-02");
		List<Appointment> appointments = service
				.getLateAppointments(fromDate, toDate, null, null, null);
		assertEquals(1, appointments.size());

	}
}
