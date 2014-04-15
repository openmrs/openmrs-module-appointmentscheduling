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
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * Tests Appointment Block methods in the {@link $ AppointmentService} .
 */
public class AppointmentBlockServiceTest extends BaseModuleContextSensitiveTest {
	
	private static final int TOTAL_APPOINTMENT_BLOCKS = 5;
	
	@Autowired
	private AppointmentService service;
	
	@Autowired
	LocationService locationService;
	
	@Before
	public void before() throws Exception {
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all appointment blocks", method = "getAllAppointmentBlocks()")
	public void getAllAppointmentBlocks_shouldGetAllAppointmentBlocks() throws Exception {
		List<AppointmentBlock> appointmentBlocks = service.getAllAppointmentBlocks();
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, appointmentBlocks.size());
	}
	
	@Test
	@Verifies(value = "should get correct appointment block", method = "getAppointmentBlock(Integer)")
	public void getAppointmentBlock_shouldGetCorrectAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-01 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlock(2);
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-02 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlock(3);
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-03 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlock(6);
		assertNull(appointmentBlock);
	}
	
	@Test
	@Verifies(value = "should get correct appointment block", method = "getAppointmentBlockByUuid(String)")
	public void getAppointmentBlockByUuid_shouldGetCorrectAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlockByUuid("c0c579b0-8e59-401d-8a4a-976a0b183599");
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-01 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlockByUuid("759799ab-c9a5-435e-b671-77773ada99e9");
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-02 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlockByUuid("759799ab-c9a5-435e-b671-77773ada7499");
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-03 00:00:00.0", appointmentBlock.getStartDate().toString());
		
		appointmentBlock = service.getAppointmentBlockByUuid("759799ab-c9a5-435e-b671-77773ada74e1");
		assertNull(appointmentBlock);
	}
	
	@Test
	@Verifies(value = "should save new appointment block", method = "saveAppointmentBlock(AppointmentBlock)")
	public void saveAppointmentBlock_shouldSaveNewAppointmentBlock() throws Exception {
		List<AppointmentBlock> appointmentBlocks = service.getAllAppointmentBlocks(true);
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, appointmentBlocks.size());
		
		appointmentBlocks = service.getAllAppointmentBlocks();
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, appointmentBlocks.size());
		
		Date started = new Date();
		Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
		Provider provider = Context.getProviderService().getProvider(1);
		AppointmentBlock appointmentBlock = new AppointmentBlock(started, OpenmrsUtil.getLastMomentOfDay(started), provider,
		        new Location(1), appointmentTypes);
		service.saveAppointmentBlock(appointmentBlock);
		
		appointmentBlock = service.getAppointmentBlock(4);
		assertNotNull(appointmentBlock);
		
		//Should create a new appointment block row
		assertEquals(TOTAL_APPOINTMENT_BLOCKS + 1, service.getAllAppointmentBlocks().size());
	}
	
	@Test
	@Verifies(value = "save a providerless appointment block", method = "saveAppointmentBlock(AppointmentBlock)")
	public void saveAppointmentBlock_shouldSaveAProviderlessAppointmentBlock() throws Exception {
		List<AppointmentBlock> appointmentBlocks = service.getAllAppointmentBlocks(true);
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, appointmentBlocks.size());
		
		Date started = new Date();
		Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
		AppointmentBlock appointmentBlock = new AppointmentBlock(started, OpenmrsUtil.getLastMomentOfDay(started), null,
		        new Location(1), appointmentTypes);
		service.saveAppointmentBlock(appointmentBlock);
		
		assertThat(appointmentBlock.getProvider(), is(nullValue()));
		
		appointmentBlocks = service.getAllAppointmentBlocks(true);
		assertThat(appointmentBlocks, hasItem(appointmentBlock));
		assertEquals(TOTAL_APPOINTMENT_BLOCKS + 1, appointmentBlocks.size());
	}
	
	@Test
	@Verifies(value = "should save edited appointment block", method = "saveAppointmentBlock(AppointmentBlock)")
	public void saveAppointmentBlock_shouldSaveEditedAppointmentBlock() throws Exception {
		
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		assertEquals("2005-01-01 00:00:00.0", appointmentBlock.getStartDate().toString());
		Date startDate = new Date();
		appointmentBlock.setStartDate(startDate);
		appointmentBlock.setEndDate(OpenmrsUtil.getLastMomentOfDay(startDate));
		service.saveAppointmentBlock(appointmentBlock);
		
		appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		assertEquals(startDate, appointmentBlock.getStartDate());
		
		//Should not change the number of appointment types
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, service.getAllAppointmentBlocks().size());
		
	}
	
	@Test
	@Verifies(value = "should void given appointment block", method = "voidAppointmentBlock(AppointmentBlock, String)")
	public void voidAppointmentBlock_shouldVoidGivenAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		Assert.assertFalse(appointmentBlock.isVoided());
		assertNull(appointmentBlock.getVoidReason());
		
		service.voidAppointmentBlock(appointmentBlock, "void reason");
		
		appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		assertTrue(appointmentBlock.isVoided());
		assertEquals("void reason", appointmentBlock.getVoidReason());
		
		// make sure all the underlying appointment blocks have been voided
		assertEquals(0, service.getTimeSlotsInAppointmentBlock(appointmentBlock).size());
		
		//Should not change the number of appointment blocks.
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, service.getAllAppointmentBlocks().size());
	}
	
	@Test
	@Verifies(value = "should unvoid given appointment block", method = "unvoidAppointmentBlock(AppointmentBlock)")
	public void unvoidAppointmentBlock_shouldUnvoidGivenAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(3);
		assertNotNull(appointmentBlock);
		Assert.assertTrue(appointmentBlock.isVoided());
		assertEquals("Some void reason", appointmentBlock.getVoidReason());
		
		service.unvoidAppointmentBlock(appointmentBlock);
		
		appointmentBlock = service.getAppointmentBlock(3);
		assertNotNull(appointmentBlock);
		assertFalse(appointmentBlock.isVoided());
		assertNull("void reason", appointmentBlock.getVoidReason());
		
		//Should not change the number of appointment blocks.
		assertEquals(TOTAL_APPOINTMENT_BLOCKS, service.getAllAppointmentBlocks().size());
	}
	
	@Test
	@Verifies(value = "should delete given appointment block", method = "purgeAppointmentBlock(AppointmentBlock)")
	public void purgeAppointmentBlock_shouldDeleteGivenAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(3);
		assertNotNull(appointmentBlock);
		
		service.purgeAppointmentBlock(appointmentBlock);
		
		appointmentBlock = service.getAppointmentBlock(3);
		assertNull(appointmentBlock);
		
		//Should decrease the number of appointment blocks by one.
		assertEquals(TOTAL_APPOINTMENT_BLOCKS - 1, service.getAllAppointmentBlocks().size());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Verifies(value = "should get all the appointment blocks that correspounds to a given date interval, set of locations, a provider and an appointment type", method = "getAppointmentBlocks(Date,Date,String,Provider,AppointmentType)")
	public void getAppointmentBlocksByDateAndLocation_shouldGetAllCorrectAppointmentBlock() throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		Location location = appointmentBlock.getLocation();
		Date fromDate = appointmentBlock.getStartDate();
		Date toDate = appointmentBlock.getEndDate();
		Provider provider = appointmentBlock.getProvider();
		AppointmentType appointmentType = service.getAppointmentType(2);
		String locaitons = location.getId() + ",3";
		List<AppointmentBlock> appointmentBlocks = service.getAppointmentBlocks(fromDate, toDate, locaitons, provider, null);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		appointmentBlock = service.getAppointmentBlock(2);
		fromDate = appointmentBlock.getStartDate();
		toDate = appointmentBlock.getEndDate();
		appointmentBlocks = service.getAppointmentBlocks(fromDate, toDate, "", provider, null);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(2), appointmentBlocks.get(0).getAppointmentBlockId());
		
		// should ignore voided blocks
		appointmentBlocks = service.getAppointmentBlocks(toDate, null, "", provider, null);
		assertNotNull(appointmentBlocks);
		assertEquals(1, appointmentBlocks.size());
		assertEquals(new Integer(4), appointmentBlocks.get(0).getAppointmentBlockId());
		
		appointmentBlocks = service.getAppointmentBlocks(null, toDate, "", provider, null);
		assertNotNull(appointmentBlocks);
		assertEquals(2, appointmentBlocks.size());
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		assertEquals(new Integer(2), appointmentBlocks.get(1).getAppointmentBlockId());
		
		appointmentBlocks = service.getAppointmentBlocks(null, null, locaitons, provider, null);
		assertNotNull(appointmentBlocks);
		assertEquals(1, appointmentBlocks.size());
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		appointmentBlocks = service.getAppointmentBlocks(null, null, "", provider, null);
		assertEquals(3, appointmentBlocks.size());
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		assertEquals(new Integer(2), appointmentBlocks.get(1).getAppointmentBlockId());
		assertEquals(new Integer(4), appointmentBlocks.get(2).getAppointmentBlockId());
		
		//test filtering by appointment type only
		appointmentBlocks = service.getAppointmentBlocks(null, null, "", null, appointmentType);
		assertEquals(1, appointmentBlocks.size());
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//test filtering by appointment type and provider
		appointmentBlocks = service.getAppointmentBlocks(null, null, "", provider, appointmentType);
		assertEquals(1, appointmentBlocks.size());
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		provider = Context.getProviderService().getProvider(2);
		appointmentBlocks = service.getAppointmentBlocks(null, null, "", provider, null);
		assertEquals(0, appointmentBlocks.size());
		
	}
	
	@Test
	@Verifies(value = "allow overlapping providerless appointment blocks", method = "getOverlappingAppointmentBlocks(AppointmentBlock)")
	public void getOverlappingAppointmentBlocks_shouldAllowOverlappingProviderlessAppointmentBlocks() throws Exception {
		// the test dataset has a block from 2005-01-01 00:00:00.0 - 11:00:00.0 with provider 1 at location 3
		// we will also create a providerless block at the same time; neither of these should overlap with a third block
		// at the same time that is also providerless
		
		Date fromDate = DateUtils.parseDate("2005-01-01 00:00", "yyyy-MM-dd HH:mm");
		Date toDate = DateUtils.parseDate("2005-01-01 11:00", "yyyy-MM-dd HH:mm");
		Location atLocation = locationService.getLocation(3);
		Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
		
		AppointmentBlock block = new AppointmentBlock(fromDate, toDate, null, atLocation, appointmentTypes);
		service.saveAppointmentBlock(block);
		
		assertThat(service.getOverlappingAppointmentBlocks(block).size(), is(0));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Verifies(value = "should get all the appointment blocks that overlaps to a given appointment block", method = "getOverlappingAppointmentBlocks(AppointmentBlock)")
	public void getOverlappingAppointmentBlocksToTheGivenAppointmentBlock_shouldGetAllCorrectAppointmentBlock()
	        throws Exception {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		List<AppointmentBlock> appointmentBlocks = null;
		AppointmentBlock testedAppointmentBlock = new AppointmentBlock(appointmentBlock.getStartDate(),
		        appointmentBlock.getEndDate(), appointmentBlock.getProvider(), appointmentBlock.getLocation(),
		        appointmentBlock.getTypes());
		//Full overlap
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Partial overlaps
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Case 1
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Case 2
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Case 3
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Case 4
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, -1);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(1), appointmentBlocks.get(0).getAppointmentBlockId());
		
		//Empty Case
		cal.setTime(appointmentBlock.getStartDate());
		cal.add(Calendar.HOUR_OF_DAY, -11);
		testedAppointmentBlock.setStartDate(new Timestamp(cal.getTime().getTime()));
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, -11);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(0), new Integer(appointmentBlocks.size()));
		
		//Special Case
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.DAY_OF_MONTH, 4);
		testedAppointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(2), new Integer(appointmentBlocks.size()));
		
		//Different provider
		testedAppointmentBlock.setProvider(Context.getProviderService().getProvider(2));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(0), new Integer(appointmentBlocks.size()));
		
		//Update an appointment block
		cal.setTime(appointmentBlock.getEndDate());
		cal.add(Calendar.HOUR_OF_DAY, 1);
		appointmentBlock.setEndDate(new Timestamp(cal.getTime().getTime()));
		appointmentBlocks = service.getOverlappingAppointmentBlocks(testedAppointmentBlock);
		assertNotNull(appointmentBlocks);
		assertEquals(new Integer(0), new Integer(appointmentBlocks.size()));
	}
}
