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
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentStatus;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests {@link ${AppointmentService}}.
 */
public class  AppointmentStatusServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointment statuses", method = "getAllAppointmentStatuses()")
	public void getAllAppointmentStatuses_shouldGetAllAppointmentStatuses() throws Exception {
		List<AppointmentStatus> appointmentStatuses = service.getAllAppointmentStatuses();
		assertEquals(3, appointmentStatuses.size());
	}
	
	@Test
	@Verifies(value = "should get correct appointment status", method = "getAppointmentStatus(Integer)")
	public void getAppointmentStatus_shouldGetCorrectAppointmentStatus() throws Exception {
		AppointmentStatus appointmentStatus = service.getAppointmentStatus(1);
		assertNotNull(appointmentStatus);
		assertEquals("Waiting", appointmentStatus.getStatus());
		
		appointmentStatus = service.getAppointmentStatus(2);
		assertNotNull(appointmentStatus);
		assertEquals("In-Consultation", appointmentStatus.getStatus());
		
		appointmentStatus = service.getAppointmentStatus(3);
		assertNotNull(appointmentStatus);
		assertEquals("Missed", appointmentStatus.getStatus());
		
		appointmentStatus = service.getAppointmentStatus(5);
		Assert.assertNull(appointmentStatus);
	}
	
	
	@Test
	@Verifies(value = "should get correct appointment statuses", method = "getAppointmentStatuses(String)")
	public void getAppointmentStatuses_shouldGetCorrentAppointmentStatuses() throws Exception {
		List<AppointmentStatus> appointmentStatuses = service.getAppointmentStatuses("Waiting");
		assertNotNull(appointmentStatuses);
		assertEquals(1, appointmentStatuses.size());
		assertEquals("Waiting", appointmentStatuses.get(0).getStatus());
		
		appointmentStatuses = service.getAppointmentStatuses("Missed");
		assertNotNull(appointmentStatuses);
		assertEquals(1, appointmentStatuses.size());
		assertEquals("Missed", appointmentStatuses.get(0).getStatus());
		
		appointmentStatuses = service.getAppointmentStatuses("newStatus");
		assertNotNull(appointmentStatuses);
		assertEquals(0, appointmentStatuses.size());
	}
	
	@Test
	@Verifies(value = "should save new appointment status", method = "saveAppointmentStatus(AppointmentStatus)")
	public void saveAppointmentStatus_shouldSaveNewAppointmentStatus() throws Exception {
		List<AppointmentStatus> appointmentStatuses = service.getAppointmentStatuses("Some Status");
		assertEquals(0, appointmentStatuses.size());
		
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		TimeSlot timeSlot = new TimeSlot(appointmentBlock, new Date(), new Date());
		Appointment appointment = service.getAppointment(1);		
		AppointmentStatus appointmentStatus = new AppointmentStatus(appointment,"Some Status", new Date(),new Date());
		service.saveAppointmentStatus(appointmentStatus);
		
		appointmentStatuses = service.getAppointmentStatuses("Some Status");
		assertEquals(1, appointmentStatuses.size());
		
		//Should create a new appointment status row.
		assertEquals(4, service.getAllAppointmentStatuses().size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment status", method = "saveAppointmentStatus(AppointmentStatus)")
	public void saveAppointmentStatus_shouldSaveEditedAppointmentStatus() throws Exception {
		AppointmentStatus appointmentStatus = service.getAppointmentStatus(1);
		assertNotNull(appointmentStatus);
		assertEquals("Waiting", appointmentStatus.getStatus());
		
		appointmentStatus.setStatus("Edited Status");
		service.saveAppointmentStatus(appointmentStatus);
		
		appointmentStatus = service.getAppointmentStatus(1);
		assertNotNull(appointmentStatus);
		assertEquals("Edited Status", appointmentStatus.getStatus());
		
		//Should not change the number of appointment types.
		assertEquals(3, service.getAllAppointmentStatuses().size());
	}
	
	}
