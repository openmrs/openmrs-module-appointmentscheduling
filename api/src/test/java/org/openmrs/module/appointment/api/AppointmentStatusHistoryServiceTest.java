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
import org.openmrs.module.appointment.AppointmentStatusHistory;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests Appointment Status History methods in the {@link ${AppointmentService}}.
 */
public class  AppointmentStatusHistoryServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointment status histories", method = "getAllAppointmentStatusHistories()")
	public void getAllAppointmentStatusHistories_shouldGetAllAppointmentStatusHistories() throws Exception {
		List<AppointmentStatusHistory> appointmentStatusHistories = service.getAllAppointmentStatusHistories();
		assertEquals(3, appointmentStatusHistories.size());
	}
	
	@Test
	@Verifies(value = "should get correct appointment status history", method = "getAppointmentStatusHistory(Integer)")
	public void getAppointmentStatusHistory_shouldGetCorrectAppointmentStatusHistory() throws Exception {
		AppointmentStatusHistory appointmentStatusHistory = service.getAppointmentStatusHistory(1);
		assertNotNull(appointmentStatusHistory);
		assertEquals("Waiting", appointmentStatusHistory.getStatus());
		
		appointmentStatusHistory = service.getAppointmentStatusHistory(2);
		assertNotNull(appointmentStatusHistory);
		assertEquals("In-Consultation", appointmentStatusHistory.getStatus());
		
		appointmentStatusHistory = service.getAppointmentStatusHistory(3);
		assertNotNull(appointmentStatusHistory);
		assertEquals("Missed", appointmentStatusHistory.getStatus());
		
		appointmentStatusHistory = service.getAppointmentStatusHistory(5);
		Assert.assertNull(appointmentStatusHistory);
	}
	
	
	@Test
	@Verifies(value = "should get correct appointment status histories", method = "getAppointmentStatusHistories(String)")
	public void getAppointmentStatusHistories_shouldGetCorrentAppointmentStatusHistories() throws Exception {
		List<AppointmentStatusHistory> appointmentStatusHistories = service.getAppointmentStatusHistories("Waiting");
		assertNotNull(appointmentStatusHistories);
		assertEquals(1, appointmentStatusHistories.size());
		assertEquals("Waiting", appointmentStatusHistories.get(0).getStatus());
		
		appointmentStatusHistories = service.getAppointmentStatusHistories("Missed");
		assertNotNull(appointmentStatusHistories);
		assertEquals(1, appointmentStatusHistories.size());
		assertEquals("Missed", appointmentStatusHistories.get(0).getStatus());
		
		appointmentStatusHistories = service.getAppointmentStatusHistories("newStatus");
		assertNotNull(appointmentStatusHistories);
		assertEquals(0, appointmentStatusHistories.size());
	}
	
	@Test
	@Verifies(value = "should save new appointment status history", method = "saveAppointmentStatusHistory(AppointmentStatusHistory)")
	public void saveAppointmentStatusHistory_shouldSaveNewAppointmentStatusHistory() throws Exception {
		List<AppointmentStatusHistory> appointmentStatusHistories = service.getAppointmentStatusHistories("Some Status");
		assertEquals(0, appointmentStatusHistories.size());
		
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		TimeSlot timeSlot = new TimeSlot(appointmentBlock, new Date(), new Date());
		Appointment appointment = service.getAppointment(1);		
		AppointmentStatusHistory appointmentStatusHistory = new AppointmentStatusHistory(appointment,"Some Status", new Date(),new Date());
		service.saveAppointmentStatusHistory(appointmentStatusHistory);
		
		appointmentStatusHistories = service.getAppointmentStatusHistories("Some Status");
		assertEquals(1, appointmentStatusHistories.size());
		
		//Should create a new appointment status history row.
		assertEquals(4, service.getAllAppointmentStatusHistories().size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment status history", method = "saveAppointmentStatusHistory(AppointmentStatusHistory)")
	public void saveAppointmentStatusHistory_shouldSaveEditedAppointmentStatusHistory() throws Exception {
		AppointmentStatusHistory appointmentStatusHistory = service.getAppointmentStatusHistory(1);
		assertNotNull(appointmentStatusHistory);
		assertEquals("Waiting", appointmentStatusHistory.getStatus());
		
		appointmentStatusHistory.setStatus("Edited Status");
		service.saveAppointmentStatusHistory(appointmentStatusHistory);
		
		appointmentStatusHistory = service.getAppointmentStatusHistory(1);
		assertNotNull(appointmentStatusHistory);
		assertEquals("Edited Status", appointmentStatusHistory.getStatus());
		
		//Should not change the number of appointment status histories.
		assertEquals(3, service.getAllAppointmentStatusHistories().size());
	}
	
	}
