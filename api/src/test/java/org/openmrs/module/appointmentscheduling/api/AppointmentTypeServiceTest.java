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
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Tests Appointment Type methods in the {@link $ AppointmentService} .
 */
public class AppointmentTypeServiceTest extends BaseModuleContextSensitiveTest {
	
	public static final String INITIAL_HIV_CLINIC_APPOINTMENT = "Initial HIV Clinic Appointment";
	
	public static final String HOSPITALIZATION = "Hospitalization";
	
	public static final String NEW_APPOINTMENT_TYPE = "New Appointment Type";
	
	public static final String RETURN_TB_CLINIC_APPOINTMENT = "Return TB Clinic Appointment";
	
	public static final String HOSPITALIZATION_2 = "Hospitalization2";
	
	public static final String TB_CLINIC_APPOINTMENT_UUID = "759799ab-c9a5-435e-b671-77773ada74e4";
	
	public static final String HOSPITALIZATION_2_UUID = "759799ab-c9a5-435e-b671-77773ada74e6";
	
	public static final String INVALID_UUID = "759799ab-c9a5-435e-b671-77773ada74e1";
	
	public static final String INITIAL_HIV_CLINIC_APPOINTMENT_UUID = "c0c579b0-8e59-401d-8a4a-976a0b183519";
	
	private AppointmentService service;
	
	private Integer amountOfAppointmentTypesNotRetired = 3;
	
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
		assertEquals(INITIAL_HIV_CLINIC_APPOINTMENT, appointmentType.getName());
		
		appointmentType = service.getAppointmentType(2);
		assertNotNull(appointmentType);
		assertEquals(RETURN_TB_CLINIC_APPOINTMENT, appointmentType.getName());
		
		appointmentType = service.getAppointmentType(3);
		assertNotNull(appointmentType);
		assertEquals(HOSPITALIZATION_2, appointmentType.getName());
		
		appointmentType = service.getAppointmentType(5);
		Assert.assertNull(appointmentType);
	}
	
	@Test
	@Verifies(value = "should get correct appointment type", method = "getAppointmentTypeByUuid(String)")
	public void getAppointmentTypeByUuid_shouldGetCorrentAppointmentType() throws Exception {
		AppointmentType appointmentType = service.getAppointmentTypeByUuid(INITIAL_HIV_CLINIC_APPOINTMENT_UUID);
		assertNotNull(appointmentType);
		assertEquals(INITIAL_HIV_CLINIC_APPOINTMENT, appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid(TB_CLINIC_APPOINTMENT_UUID);
		assertNotNull(appointmentType);
		assertEquals(RETURN_TB_CLINIC_APPOINTMENT, appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid(HOSPITALIZATION_2_UUID);
		assertNotNull(appointmentType);
		assertEquals(HOSPITALIZATION_2, appointmentType.getName());
		
		appointmentType = service.getAppointmentTypeByUuid(INVALID_UUID);
		Assert.assertNull(appointmentType);
	}
	
	@Test
	@Verifies(value = "should get correct appointment types", method = "getAppointmentTypes(String)")
	public void getAppointmentTypes_shouldGetCorrentAppointmentTypes() throws Exception {
		List<AppointmentType> appointmentTypes = service.getAppointmentTypes("HIV Clinic");
		assertNotNull(appointmentTypes);
		assertEquals(1, appointmentTypes.size());
		assertEquals(INITIAL_HIV_CLINIC_APPOINTMENT, appointmentTypes.get(0).getName());
		
		appointmentTypes = service.getAppointmentTypes("Clinic Appointment");
		assertNotNull(appointmentTypes);
		assertEquals(2, appointmentTypes.size());
		assertEquals(INITIAL_HIV_CLINIC_APPOINTMENT, appointmentTypes.get(0).getName());
		assertEquals(RETURN_TB_CLINIC_APPOINTMENT, appointmentTypes.get(1).getName());
		
		appointmentTypes = service.getAppointmentTypes("ClinicAppointment");
		assertNotNull(appointmentTypes);
		assertEquals(0, appointmentTypes.size());
	}
	
	@Test
	@Verifies(value = "should get appointment types with fuzzy search", method = "getAppointmentTypes(String)")
	public void getAppointmentTypes_shouldGetCorrectAppointmentTypesWithFuzzySearch() throws Exception {
		List<AppointmentType> appointmentTypes = service.getAppointmentTypes("HIV");
		assertNotNull(appointmentTypes);
		assertTrue(appointmentTypes.get(0).getName().equals(INITIAL_HIV_CLINIC_APPOINTMENT));
	}
	
	@Test
	@Verifies(value = "should get all appointment types not retired ", method = "getAppointmentTypes")
	public void getAppointmentTypes_shouldGetAppointmentTypesNotRetired() throws Exception {
		List<AppointmentType> appointmentTypes = service.getAppointmentTypes("", false);
		assertNotNull(appointmentTypes);
		assertEquals((Object) amountOfAppointmentTypesNotRetired, appointmentTypes.size());
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
		assertEquals(INITIAL_HIV_CLINIC_APPOINTMENT, appointmentType.getName());
		
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
    @Verifies(value = "should save confidential appointment type", method = "saveAppointmentType(AppointmentType)")
    public void saveAppointmentType_shouldSaveConfidentialAppointmentType() throws Exception {
        AppointmentType appointmentType = new AppointmentType("HIV Followup", "Scheduled followup", 30);
        appointmentType.setConfidential(true);

        appointmentType = service.saveAppointmentType(appointmentType);
        assertTrue(appointmentType.isConfidential());
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
		AppointmentType appointmentType = service.getAppointmentType(4);
		assertNotNull(appointmentType);
		assertTrue(appointmentType.isRetired());
		assertEquals("Some Retire Reason", appointmentType.getRetireReason());
		
		service.unretireAppointmentType(appointmentType);
		
		appointmentType = service.getAppointmentType(4);
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
	
	@Test
	@Verifies(value = "should get correct appointment type distribution", method = "getAppointmentTypeDistribution(Date, Date")
	public void getAppointmentTypeDistribution_shouldGetCorrectDistribution() throws ParseException {
		//Total of 3 appointments - one of type 3 , two of type 1 
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2005-01-01 00:00:00.0");
		Date toDate = format.parse("2008-01-01 01:00:00.1");
		
		AppointmentType type1 = Context.getService(AppointmentService.class).getAppointmentType(1);
		AppointmentType type2 = Context.getService(AppointmentService.class).getAppointmentType(2);
		AppointmentType type3 = Context.getService(AppointmentService.class).getAppointmentType(3);
		assertNotNull(type1);
		assertNotNull(type2);
		assertNotNull(type3);
		
		Map<AppointmentType, Integer> distribution = Context.getService(AppointmentService.class)
		        .getAppointmentTypeDistribution(fromDate, toDate);
		
		assertEquals((Integer) 5, (Integer) distribution.get(type3));
		Assert.assertFalse(distribution.containsKey(type2));
		assertEquals((Integer) 2, (Integer) distribution.get(type1));
		assertEquals(2, distribution.size());
	}
	
	@Test
	@Verifies(value = "should return true when appointment type name exists and uuid does not", method = "verifyAppointmentTypeNameExists(String appointmentTypeName)")
	public void shouldReturnTrueWhenAppointmentTypeNameExists() throws Exception {
		AppointmentType appointmentType = new AppointmentType(INITIAL_HIV_CLINIC_APPOINTMENT, "DESCRIPTION", 10);
		assertTrue(service.verifyDuplicatedAppointmentTypeName(appointmentType));
	}
	
	@Test
	@Verifies(value = "should return false when appointment type name does not exists", method = "verifyAppointmentTypeNameExists(String appointmentTypeName)")
	public void shouldReturnFalseWhenAppointmentTypeNameDoesNotExists() throws Exception {
		AppointmentType appointmentType = new AppointmentType(NEW_APPOINTMENT_TYPE, "DESCRIPTION", 10);
		assertFalse(service.verifyDuplicatedAppointmentTypeName(appointmentType));
	}
	
	@Test
	@Verifies(value = "should return false when appointment type name exists but it is retired", method = "verifyAppointmentTypeNameExists(String appointmentTypeName)")
	public void shouldReturnFalseWhenAppointmentTypeNameExistsButRetired() throws Exception {
		AppointmentType appointmentType = new AppointmentType(HOSPITALIZATION, "DESCRIPTION", 10);
		assertFalse(service.verifyDuplicatedAppointmentTypeName(appointmentType));
	}
	
	@Test
	@Verifies(value = "should return false when appointment type has the same name and uuid", method = "verifyAppointmentTypeNameExists(String appointmentTypeName)")
	public void shouldReturnFalseWhenAppointmentTypeHasTheSameNameAndUuid() throws Exception {
		AppointmentType appointmentType = service.getAppointmentTypeByUuid(INITIAL_HIV_CLINIC_APPOINTMENT_UUID);
		assertFalse(service.verifyDuplicatedAppointmentTypeName(appointmentType));
	}
}
