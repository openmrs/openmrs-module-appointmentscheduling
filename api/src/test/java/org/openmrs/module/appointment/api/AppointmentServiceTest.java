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

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests Appointment methods in the {@link ${AppointmentService}}.
 */
public class AppointmentServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointments", method = "getAllAppointments()")
	public void getAllAppointments_shouldGetAllAppointments() throws Exception {
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(3, appointments.size());
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
		
		appointment = service.getAppointment(4);
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
		Appointment appointment = new Appointment(timeSlot, new Visit(1), new Patient(1), "SCHEDULED");
		service.saveAppointment(appointment);
		
		//Should create a new appointment type row.
		List<Appointment> appointments = service.getAllAppointments();
		assertEquals(4, appointments.size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment", method = "saveAppointment(Appointment)")
	public void saveAppointment_shouldSaveEditedAppointment() throws Exception {
		Appointment appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals((Integer) 1, appointment.getId());
		
		appointment.setStatus("TEST_CHANGED");
		service.saveAppointment(appointment);
		
		appointment = service.getAppointment(1);
		assertNotNull(appointment);
		assertEquals("TEST_CHANGED", appointment.getStatus());
		
		//Should not change the number of appointment types.
		assertEquals(3, service.getAllAppointments().size());
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
		assertEquals(3, service.getAllAppointments().size());
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
		assertEquals(3, service.getAllAppointments().size());
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
		assertEquals(2, service.getAllAppointments().size());
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
}
