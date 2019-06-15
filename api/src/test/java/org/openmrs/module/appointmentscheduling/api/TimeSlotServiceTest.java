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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;
import net.sf.cglib.core.Local;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * <<<<<<< HEAD Tests Time Slot methods in the {@link AppointmentService} . ======= Tests Time Slot
 * methods in the {@link AppointmentService} . >>>>>>> f97b876... fixed which AppointmentStatuses
 * are considered "cancelled"
 */
public class TimeSlotServiceTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	private static int TOTAL_TIME_SLOTS = 9;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all time slots", method = "getAllTimeSlots()")
	public void getAllTimeSlots_shouldGetAllTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots();
		assertEquals(TOTAL_TIME_SLOTS, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should get all time slots by a given bool whether to include voided", method = "getAllTimeSlots(boolean)")
	public void getAllTimeSlots_shouldGetAllUnvoidedTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots(false);
		assertEquals(8, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should get all time slots including voided", method = "getAllTimeSlots(boolean)")
	public void getAllTimeSlots_shouldGetAllIncludingVoidedTimeSlots() {
		List<TimeSlot> timeSlots = service.getAllTimeSlots(true);
		assertEquals(TOTAL_TIME_SLOTS, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should save new time slot", method = "saveTimeSlot(TimeSlot)")
	public void saveTimeSlot_shouldSaveNewTimeSlot() {
		
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		TimeSlot timeSlot = new TimeSlot(appointmentBlock, new Date(), new Date());
		timeSlot = service.saveTimeSlot(timeSlot);
		List<TimeSlot> timeSlots = service.getAllTimeSlots();
		
		assertNotNull(timeSlot);
		assertEquals(TOTAL_TIME_SLOTS + 1, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should save edited time slot without adding a new row", method = "saveTimeSlot(TimeSlot)")
	public void saveTimeSlot_shouldSaveEditedTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		Date currentDate = new Date();
		timeSlot.setEndDate(currentDate);
		timeSlot = service.saveTimeSlot(timeSlot);
		assertNotNull(timeSlot);
		
		timeSlot = service.getTimeSlot(1);
		assertEquals(currentDate, timeSlot.getEndDate());
	}
	
	@Test
	@Verifies(value = "get correct time slot by a given id", method = "getTimeSlot(Integer timeSlotId)")
	public void getTimeSlot_shouldGetCorrectTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		Date startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.0", startDate.toString());
		
		timeSlot = service.getTimeSlot(2);
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.1", startDate.toString());
		
		timeSlot = service.getTimeSlot(3);
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2007-01-01 00:00:00.2", startDate.toString());
		
		timeSlot = service.getTimeSlot(10);
		assertNull(timeSlot);
	}
	
	@Test
	@Verifies(value = "get correct time slot by a given uuid", method = "getTimeSlotByUuid(String)")
	public void getTimeSlotByUuid_shouldgetCorrectTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183604");
		Date startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.0", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183605");
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2006-01-01 00:00:00.1", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183606");
		startDate = timeSlot.getStartDate();
		assertNotNull(timeSlot);
		assertEquals("2007-01-01 00:00:00.2", startDate.toString());
		
		timeSlot = service.getTimeSlotByUuid("NOT A UUID");
		Assert.assertNull(timeSlot);
	}
	
	@Test
	@Verifies(value = "should void a time slot", method = "voidTimeSlot(TimeSlot, String)")
	public void voidTimeSlot_shouldVoidTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);
		assertFalse(timeSlot.isVoided());
		
		service.voidTimeSlot(timeSlot, "void reason");
		
		timeSlot = service.getTimeSlot(1);
		assertNotNull(timeSlot);
		assertTrue(timeSlot.isVoided());
		
		assertEquals(TOTAL_TIME_SLOTS, service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "should unvoid a time slot", method = "unvoidTimeSlot(TimeSlot)")
	public void unvoidTimeSlot_shouldUnvoidTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		assertTrue(timeSlot.isVoided());
		assertEquals("void reason", timeSlot.getVoidReason());
		
		service.unvoidTimeSlot(timeSlot);
		
		timeSlot = service.getTimeSlot(3);
		assertNotNull(timeSlot);
		assertFalse(timeSlot.isVoided());
		assertNull(timeSlot.getVoidReason());
		
		assertEquals(TOTAL_TIME_SLOTS, service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "should delete a given time slot", method = "purgeTimeSlot(TimeSlot)")
	public void purgeTimeSlot_shouldDeleteTimeSlot() {
		TimeSlot timeSlot = service.getTimeSlot(4);
		assertNotNull(timeSlot);
		
		service.purgeTimeSlot(timeSlot);
		
		timeSlot = service.getTimeSlot(4);
		assertNull(timeSlot);
		
		assertEquals(TOTAL_TIME_SLOTS - 1, service.getAllTimeSlots().size());
	}
	
	@Test
	@Verifies(value = "should not include voided time slots", method = "getTimeSlotsByConstraintsIncludingFull(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraintsIncludingFull_shouldNotIncludeVoidedTimeSlots() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, null, null,
		    null, null);
		int countVoided = 0;
		for (TimeSlot slot : availableTimeSlots) {
			if (slot.isVoided())
				countVoided++;
		}
		assertEquals(countVoided, 0);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2000-01-01 00:00:00.0");
		Date toDate = format.parse("2012-01-01 00:00:00.0");
		availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate, toDate, null, null);
		countVoided = 0;
		for (TimeSlot slot : availableTimeSlots) {
			if (slot.isVoided())
				countVoided++;
		}
		assertEquals(countVoided, 0);
		
		Provider provider = Context.getProviderService().getProvider(1);
		Assert.assertNotNull(provider);
		availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate, toDate, provider,
		    null);
		countVoided = 0;
		for (TimeSlot slot : availableTimeSlots) {
			if (slot.isVoided())
				countVoided++;
		}
		
		assertEquals(countVoided, 0);
	}
	
	@Test
	@Verifies(value = "should get correct time slots", method = "getTimeSlotsByConstraintsIncludingFull(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraintsIncludingFull_shouldGetCorrectTimeSlots() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(2);
		assertNotNull(appointmentType);
		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2006-01-01 01:00:00.1");
		Date toDate = format.parse("2013-01-01 00:00:00.0");
		
		//Filter by dates
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate,
		    toDate, provider, null);
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(2)));
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(4)));
		assertTrue(availableTimeSlots.size() == 2);
		
		//Confirm Time Left
		appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		fromDate = format.parse("2006-01-01 00:00:00.0");
		availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate, toDate, provider,
		    null);
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(1)));
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(4)));
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(2)));
		assertTrue(availableTimeSlots.size() == 3);
		
		//Filter by provider
		provider = Context.getProviderService().getProvider(2);
		assertNotNull(provider);
		availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(appointmentType, fromDate, toDate, provider,
		    null);
		assertTrue(availableTimeSlots.size() == 0);
		
	}
	
	@Test
	@Verifies(value = "should get correct time slots", method = "getTimeSlotsByConstraints(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraints_shouldGetCorrectTimeSlots() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(2);
		assertNotNull(appointmentType);
		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2006-01-01 00:00:00.1");
		Date toDate = format.parse("2013-01-01 00:00:00.0");
		
		List<TimeSlot> availableTimeSlots = null;
		
		//Confirm Time Left
		appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		fromDate = format.parse("2006-01-01 00:00:00.0");
		availableTimeSlots = service.getTimeSlotsByConstraints(appointmentType, fromDate, toDate, provider, null);
		assertTrue(availableTimeSlots.contains(service.getTimeSlot(4)));
		assertTrue(availableTimeSlots.size() == 1);
		
	}
	
	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if appointment type is null", method = "getTimeSlotsByConstraints(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraints_shouldThrowExcetpionIfAppointmentTypeIsNull() throws ParseException {
		Provider provider = Context.getProviderService().getProvider(1);
		Assert.assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2000-01-01 00:00:00.0");
		Date toDate = format.parse("2013-01-01 00:00:00.0");
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraints(null, fromDate, toDate, provider, null);
	}
	
	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if an illegal date interval was given", method = "getTimeSlotsByConstraints(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraints_shouldThrowExcetpionIfIllegalDateInterval() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		Provider provider = Context.getProviderService().getProvider(1);
		Assert.assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2013-01-01 00:00:00.0");
		Date toDate = format.parse("2001-01-01 00:00:00.0");
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraints(null, fromDate, toDate, provider, null);
	}
	
	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if appointment type is null", method = "getTimeSlotsByConstraintsIncludingFull(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraintsIncludingFull_shouldThrowExcetpionIfAppointmentTypeIsNull() throws ParseException {
		Provider provider = Context.getProviderService().getProvider(1);
		Assert.assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2000-01-01 00:00:00.0");
		Date toDate = format.parse("2013-01-01 00:00:00.0");
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(null, fromDate, toDate, provider,
		    null);
	}
	
	@Test(expected = APIException.class)
	@Verifies(value = "should throw exception if an illegal date interval was given", method = "getTimeSlotsByConstraintsIncludingFull(AppointmentType, Date, Date, Provider)")
	public void getTimeSlotsByConstraintsIncludingFull_shouldThrowExcetpionIfIllegalDateInterval() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		Provider provider = Context.getProviderService().getProvider(1);
		Assert.assertNotNull(provider);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2013-01-01 00:00:00.0");
		Date toDate = format.parse("2001-01-01 00:00:00.0");
		List<TimeSlot> availableTimeSlots = service.getTimeSlotsByConstraintsIncludingFull(null, fromDate, toDate, provider,
		    null);
	}
	
	@Test
	@Verifies(value = "should return correct time slots", method = "getTimeSlotsInAppointmentBlock(AppointmentBlock)")
	public void getTimeSlotsInAppointmentBlock_shouldReturnCorrectTimeSlots() {
		AppointmentBlock appointmentBlock = service.getAppointmentBlock(1);
		assertNotNull(appointmentBlock);
		
		List<TimeSlot> timeSlots = service.getTimeSlotsInAppointmentBlock(appointmentBlock);
		assertEquals(6, timeSlots.size()); // there are 7 time slots, but this should exclude the voided time slot
		
		//Should be empty list because appointment block is not exists
		timeSlots = service.getTimeSlotsInAppointmentBlock(null);
		assertEquals(0, timeSlots.size());
		
		//Should get an empty list because there are no time slots associated with the given appointment block
		appointmentBlock = service.getAppointmentBlock(2);
		timeSlots = service.getTimeSlotsInAppointmentBlock(appointmentBlock);
		assertEquals(0, timeSlots.size());
	}
	
	@Test
	@Verifies(value = "should return correct time left in time slot", method = "getTimeLeftInTimeSlot(TimeSlot)")
	public void getTimeLeftInTimeSlot_shouldReturnCorrectTimeLeft() {
		Integer timeLeft = service.getTimeLeftInTimeSlot(null);
		assertNull(timeLeft);
		
		TimeSlot timeSlot = service.getTimeSlot(1);
		Assert.assertNotNull(timeSlot);
		timeLeft = service.getTimeLeftInTimeSlot(timeSlot);
		assertEquals((Integer) 6, timeLeft);
	}
	
	@Test
	@Verifies(value = "should return all time slots by constraints and non voided ordered by the earliest start date", method = "getTimeSlotsByConstraintsIncludingFull")
	public void shouldGetAllTimeSlotsByConstraintsSortedByStartDate() throws ParseException {
		AppointmentType type = service.getAppointmentType(1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2005-01-01 00:00:00.0");
		
		List<TimeSlot> result = service.getTimeSlotsByConstraintsIncludingFull(type, fromDate, null, null, null);
		assertNotNull(result);
		assertEquals(8, result.size());
		
		TimeSlot firstTimeSlot = result.get(0);
		assertEquals(5, firstTimeSlot.getTimeSlotId().intValue());
		
		TimeSlot lastTimeSlot = result.get(result.size() - 1);
		assertEquals(9, lastTimeSlot.getTimeSlotId().intValue());
	}
	
	@Test
	@Verifies(value = "should return  a list of time slots available by appointment type and constraints ordered by the earliest start date", method = "getTimeSlotsByConstraints")
	public void shouldGetOnlyAvailableTimeSlotsByConstraintsSortedByStartDate() throws ParseException {

        AppointmentType type = service.getAppointmentType(1);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date fromDate = format.parse("2005-01-01 00:00:00.0");

		List<TimeSlot> result = service.getTimeSlotsByConstraints(type, fromDate, null, null, null);
		assertNotNull(result);
		assertEquals(4, result.size());
		assertEquals(5, result.get(0).getTimeSlotId().intValue());
		assertEquals(4, result.get(1).getTimeSlotId().intValue());
		assertEquals(8, result.get(2).getTimeSlotId().intValue());
		assertEquals(9, result.get(3).getTimeSlotId().intValue());
	}

    @Test
    @Verifies(value = "should not return a time slot if patient already has appointment during that time slot of that service type", method = "getTimeSlotsByConstraints")
    public void shouldNotReturnTimeSlotIfPatientAlreadyHasAppointmentOfThatTypeDuringTimeSlot() throws ParseException {

        AppointmentType type = service.getAppointmentType(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date fromDate = format.parse("2004-01-01 00:00:00.0");

        Patient patient = Context.getPatientService().getPatient(1);

        List<TimeSlot> result = service.getTimeSlotsByConstraintsIncludingFull(type, fromDate, null, null, null, patient);
        assertNotNull(result);
        assertEquals(6, result.size());

        // make sure the result set does not include the two time slots that the patient is already booked for
        assertTrue(!result.contains(service.getTimeSlot(1)));
        assertTrue(!result.contains(service.getTimeSlot(8)));
    }

	@Test
	@Verifies(value = "should get correct time slot for appointment", method = "getTimeslotForAppointment(Location, Provider, AppointmentType, Date)")
	public void getTimeslotForAppointment_shouldGetTimeslotForAppointment() throws ParseException {
		AppointmentType appointmentType = service.getAppointmentType(1);
		assertNotNull(appointmentType);
		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);
		Location location = Context.getLocationService().getLocation(2);
		assertNotNull(location);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date appointmentDate = format.parse("2014-01-02 11:00:00.0");

		TimeSlot timeSlot = service.getTimeslotForAppointment(location, provider, appointmentType, appointmentDate);
		assertNotNull(timeSlot);
		assertNotEquals(appointmentDate, timeSlot.getStartDate());
	}


	@Test
	@Verifies(value = "should create a new timeslot using provider schedule", method = "createTimeSlotUsindProviderSchedule(Date, Provider, Location)")
	public void createTimeSlotUsindProviderSchedule_shouldcreateTimeSlotUsindProviderSchedule() throws ParseException {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date appointmentDate = format.parse("2020-01-02 08:00:00.0");

		Provider provider = Context.getProviderService().getProvider(1);
		assertNotNull(provider);

		Location location = Context.getLocationService().getLocation(2);
		assertNotNull(location);

		TimeSlot timeSlot = service.createTimeSlotUsingProviderSchedule(appointmentDate, provider, location);
		List<TimeSlot> timeSlots = service.getAllTimeSlots();

		assertNotNull(timeSlot);
		assertEquals(TOTAL_TIME_SLOTS + 1, timeSlots.size());
	}
}
