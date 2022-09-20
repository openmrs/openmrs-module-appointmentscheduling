package org.openmrs.module.appointmentscheduling.rest.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.appointmentscheduling.rest.test.SameDatetimeMatcher.sameDatetime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TimeSlotResource1_9ControllerTest extends MainResourceControllerTest {
	
	private AppointmentService appointmentService;
	
	@BeforeEach
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldGetTimeSlotByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		TimeSlot timeSlot = appointmentService.getTimeSlotByUuid(getUuid());
		
		assertNotNull(result);
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertThat((String) PropertyUtils.getProperty(result, "startDate"), sameDatetime(timeSlot.getStartDate()));
		assertThat((String) PropertyUtils.getProperty(result, "endDate"), sameDatetime(timeSlot.getEndDate()));
		assertEquals("Hippocrates of Cos, Xanadu: 2007-01-01 00:00:00.2 - 2007-01-01 01:00:00.0",
		    PropertyUtils.getProperty(result, "display"));
		
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "appointmentBlock/uuid"));
		assertEquals(true, PropertyUtils.getProperty(result, "voided"));
		assertEquals(1, PropertyUtils.getProperty(result, "countOfAppointments"));
	}
	
	@Test
	public void shouldGetFullTimeSlotByUuid() throws Exception {
		
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + getUuid(), new org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest.Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		SimpleObject result = deserialize(handle(req));
		
		TimeSlot timeSlot = appointmentService.getTimeSlotByUuid(getUuid());
		
		assertNotNull(result);
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertThat((String) PropertyUtils.getProperty(result, "startDate"), sameDatetime(timeSlot.getStartDate()));
		assertThat((String) PropertyUtils.getProperty(result, "endDate"), sameDatetime(timeSlot.getEndDate()));
		assertEquals("Hippocrates of Cos, Xanadu: 2007-01-01 00:00:00.2 - 2007-01-01 01:00:00.0",
		    PropertyUtils.getProperty(result, "display"));
		
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "appointmentBlock/uuid"));
		assertEquals(true, PropertyUtils.getProperty(result, "voided"));
		assertEquals(1, PropertyUtils.getProperty(result, "countOfAppointments"));
		assertEquals(49, PropertyUtils.getProperty(result, "unallocatedMinutes")); // 59 min slot minus one 10 minute appt
	}
	
	@Test
	public void shouldCreateNewTimeSlot() throws Exception {
		
		int originalCount = 8;
		String json = "{ \"startDate\":\"2005-01-03T09:00:00.000-0500\", \"endDate\":\"2005-01-03T10:00:00.000-0500\", "
		        + "\"appointmentBlock\": \"759799ab-c9a5-435e-b671-77773ada7499\" }";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertThat((String) PropertyUtils.getProperty(appt, "startDate"), sameDatetime("2005-01-03T09:00:00.000-0500"));
		assertThat((String) PropertyUtils.getProperty(appt, "endDate"), sameDatetime("2005-01-03T10:00:00.000-0500"));
		assertEquals("759799ab-c9a5-435e-b671-77773ada7499",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "appointmentBlock"), "uuid"));
		assertEquals(originalCount + 1, appointmentService.getAllTimeSlots().size());
		
	}
	
	@Test
	public void shouldEditATimeSlot() throws Exception {
		
		String json = "{ \"endDate\":\"2005-01-03T11:00:00.000-0500\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183606");
		req.setContent(json.getBytes());
		handle(req);
		
		TimeSlot updated = appointmentService.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183606");
		assertNotNull(updated);
		assertThat(updated.getEndDate(), is(date("2005-01-03T11:00:00.000-0500")));
	}
	
	private Date date(String date) {
		return (Date) ConversionUtil.convert(date, Date.class);
	}
	
	@Test
	public void shouldVoidATimeSlot() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183607");
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		TimeSlot voided = appointmentService.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183607");
		assertTrue(voided.isVoided());
		assertEquals("really ridiculous random reason", voided.getVoidReason());
	}
	
	@Test
	public void shouldPurgeATimeSlot() throws Exception {
		
		int originalCount = 8;
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183607");
		req.addParameter("purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		assertNull(appointmentService.getTimeSlotByUuid("c0c579b0-8e59-401d-8a4a-976a0b183607"));
		assertEquals(originalCount - 1, appointmentService.getAllTimeSlots().size());
		
	}
	
	@Test
	public void shouldFindTimeSlotBasedOnAppointmentTypeAndFromDateExcludingVoided() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2006-06-01T00:00:00.000");
		req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(1, timeSlots.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(0).get("uuid"));
	}
	
	@Test
	public void shouldFindTimeSlotBasedOnAppointmentTypeAndDateExcludingVoided() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2006-06-01T00:00:00.000");
		req.addParameter("toDate", "2008-01-25T00:00:00.000");
		req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(1, timeSlots.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(0).get("uuid"));
		
	}
	
	@Test
	public void shouldFindTimeSlotBasedOnAppointmentTypeAndFromDateAndLocationExcludingVoided() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2001-06-01T00:00:00.000");
		req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
		req.addParameter("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8");
		
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(1, timeSlots.size());
		
		// note that the first two time slots are full, so it is not returned here
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(0).get("uuid"));
		
	}
	
	@Test
	public void shouldFindTimeSlotBasedOnAppointmentTypeAndFromDateAndProviderExcludingVoided() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2001-06-01T00:00:00.000");
		req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
		req.addParameter("provider", "c0c54sd0-8e59-401d-8a4a-976a0b183599");
		
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(1, timeSlots.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183610", timeSlots.get(0).get("uuid"));
	}
	
	@Test
	public void shouldFindTimeSlotUsingAllParametersExcludingVoided() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2006-06-01T00:00:00.000");
		req.addParameter("toDate", "2009-01-01T00:00:00.000");
		req.addParameter("appointmentType", "c0c579b0-8e59-401d-8a4a-976a0b183519"); // appt type #1   (all blocks)
		req.addParameter("provider", "c0c549b0-8e59-401d-8a4a-976a0b183599"); // provider #1    (blocks #1, #2)
		req.addParameter("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8"); // location #3 (block #1)
		
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(1, timeSlots.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(0).get("uuid"));
	}
	
	@Test
	public void shouldFindTimeSlotIncludingFullSlots() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2001-06-01T00:00:00.000");
		req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
		req.addParameter("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8");
		req.addParameter("includeFull", "true");
		
		handle(req);
		
		List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
		assertEquals(3, timeSlots.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183604", timeSlots.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183605", timeSlots.get(1).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(2).get("uuid"));
		
	}

    @Test
    public void shouldExcludeTimeSlotThatPatientAlreadyHasAnAppointmentForOfSameType() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("fromDate", "2001-06-01T00:00:00.000");
        req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
        req.addParameter("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8");
        req.addParameter("excludeTimeSlotsPatientAlreadyBookedFor", "31e09960-8f52-11e3-baa8-0800200c9a66");
        req.addParameter("includeFull", "true");

        handle(req);

        List<Map<String, String>> timeSlots = (List<Map<String, String>>) deserialize(handle(req)).get("results");
        assertEquals(2, timeSlots.size());
        assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183605", timeSlots.get(0).get("uuid"));
        assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607", timeSlots.get(1).get("uuid"));

    }

	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/timeslot";
	}
	
	@Override
	public String getUuid() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183606";
	}
	
	@Override
	public long getAllCount() {
		return 7;
	}
	
}
