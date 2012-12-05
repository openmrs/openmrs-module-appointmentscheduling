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

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

/**
 * Tests Appointment Type methods in the {@link ${AppointmentService}}.
 */
public class  AppointmentTypeServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointment types", method = "getAllAppointmentTypes()")
	public void getAllAppointmentTypes_shouldGetAllAppointmentTypes() throws Exception {
		Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
		assertEquals(4, appointmentTypes.size());
	}
	
	@Test
	@Verifies(value = "should get correct appointment type", method = "getAppointmentType(Integer)")
	public void getAppointmentType_shouldGetCorrectAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		assertEquals("Initial HIV Clinic Appointment", appointmentType.getName());
		
		appointmentType = service.getAppointmentType(2);
		assertNotNull(appointmentType);
		assertEquals("Return TB Clinic Appointment", appointmentType.getName());
		
		appointmentType = service.getAppointmentType(3);
		assertNotNull(appointmentType);
		assertEquals("Hospitalization", appointmentType.getName());
		
		appointmentType = service.getAppointmentType(5);
		Assert.assertNull(appointmentType);
	}
	
	@Test
	@Verifies(value = "should get correct appointment type", method = "getAppointmentTypeByUuid(String)")
	public void getAppointmentTypeByUuid_shouldGetCorrentAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		assertNotNull(appointmentType);
		assertEquals("Initial HIV Clinic Appointment", appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e4");
		assertNotNull(appointmentType);
		assertEquals("Return TB Clinic Appointment", appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e6");
		assertNotNull(appointmentType);
		assertEquals("Hospitalization", appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid("759799ab-c9a5-435e-b671-77773ada74e1");
		Assert.assertNull(appointmentType);
	}
	
	@Test
	@Verifies(value = "should get correct appointment types", method = "getAppointmentTypes(String)")
	public void getAppointmentTypes_shouldGetCorrentAppointmentTypes() throws Exception {
		List<AppointmentType> appointmentTypes = service.getAppointmentTypes("HIV Clinic");
		assertNotNull(appointmentTypes);
		assertEquals(1, appointmentTypes.size());
		assertEquals("Initial HIV Clinic Appointment", appointmentTypes.get(0).getName());
		
		appointmentTypes = service.getAppointmentTypes("Clinic Appointment");
		assertNotNull(appointmentTypes);
		assertEquals(2, appointmentTypes.size());
		assertEquals("Initial HIV Clinic Appointment", appointmentTypes.get(0).getName());
		assertEquals("Return TB Clinic Appointment", appointmentTypes.get(1).getName());
		
		appointmentTypes = service.getAppointmentTypes("ClinicAppointment");
		assertNotNull(appointmentTypes);
		assertEquals(0, appointmentTypes.size());
	}
	
	@Test
	@Verifies(value = "should save new appointment type", method = "saveAppointmentType(AppointmentType)")
	public void saveAppointmentType_shouldSaveNewAppointmentType() throws Exception {
		List<AppointmentType> appointmentTypes = service.getAppointmentTypes("Some Name");
		assertEquals(0, appointmentTypes.size());
		
		AppointmentType appointmentType = new AppointmentType("Some Name", "Description", 10);
		service.saveAppointmentType(appointmentType);
		
		appointmentTypes = service.getAppointmentTypes("Some Name");
		assertEquals(1, appointmentTypes.size());
		
		//Should create a new appointment type row.
		assertEquals(5, service.getAllAppointmentTypes().size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment type", method = "saveAppointmentType(AppointmentType)")
	public void saveAppointmentType_shouldSaveEditedAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		assertEquals("Initial HIV Clinic Appointment", appointmentType.getName());
		
		appointmentType.setName("Edited Name");
		appointmentType.setDescription("Edited Description");
		service.saveAppointmentType(appointmentType);
		
		appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		assertEquals("Edited Name", appointmentType.getName());
		assertEquals("Edited Description", appointmentType.getDescription());
		
		//Should not change the number of appointment types.
		assertEquals(4, service.getAllAppointmentTypes().size());
	}
	
	@Test
	@Verifies(value = "should retire given appointment type", method = "retireAppointmentType(AppointmentType, String)")
	public void retireAppointmentType_shouldRetireGivenAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		Assert.assertFalse(appointmentType.isRetired());
		Assert.assertNull(appointmentType.getRetireReason());
		
		service.retireAppointmentType(appointmentType, "retire reason");
		
		appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		assertTrue(appointmentType.isRetired());
		assertEquals("retire reason", appointmentType.getRetireReason());
		
		//Should not change the number of appointment types.
		assertEquals(4, service.getAllAppointmentTypes().size());
	}
	
	@Test
	@Verifies(value = "should unretire given appointment type", method = "unretireAppointmentType(AppointmentType)")
	public void unretireAppointmentType_shouldUnretireGivenAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentType(3);
		assertNotNull(appointmentType);
		assertTrue(appointmentType.isRetired());
		assertEquals("Some Retire Reason", appointmentType.getRetireReason());
		
		service.unretireAppointmentType(appointmentType);
		
		appointmentType = service.getAppointmentType(3);
		assertNotNull(appointmentType);
		Assert.assertFalse(appointmentType.isRetired());
		Assert.assertNull(appointmentType.getRetireReason());
		
		//Should not change the number of appointment types.
		assertEquals(4, service.getAllAppointmentTypes().size());
	}
	
	@Test
	@Verifies(value = "should delete given appointment type", method = "purgeAppointmentType(AppointmentType)")
	public void purgeAppointmentType_shouldDeleteGivenAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentType(4);
		assertNotNull(appointmentType);
		
		service.purgeAppointmentType(appointmentType);
		
		appointmentType = service.getAppointmentType(4);
		Assert.assertNull(appointmentType);
		
		//Should reduce the existing number of appointment types.
		assertEquals(3, service.getAllAppointmentTypes().size());
	}
}
