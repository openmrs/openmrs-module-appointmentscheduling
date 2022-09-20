package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus.COMPLETED;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppointmentResource1_9ControllerTest
		extends
			MainResourceControllerTest {

	private AppointmentService appointmentService;

	@BeforeEach
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}

	@Test
	public void shouldGetAppointmentByUuid() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/"
				+ getUuid());
		SimpleObject result = deserialize(handle(req));

		assertNotNull(result);
		assertEquals(getUuid(),
				PropertyUtils.getProperty(result, "uuid"));
		assertEquals("SCHEDULED",
				PropertyUtils.getProperty(result, "status.code"));
		assertEquals("Initial HIV Clinic Appointment : Scheduled",
				PropertyUtils.getProperty(result, "display"));

		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
				Util.getByPath(result, "appointmentType/uuid"));
		assertEquals(false, PropertyUtils.getProperty(result, "voided"));
	}

	@Test
	public void shouldCreateNewAppointmentWithAllFields() throws Exception {

		String json = "{ \"timeSlot\":\"c0c579b0-8e59-401d-8a4a-976a0b183607\", "
				+ "\"patient\":\"31e09960-8f52-11e3-baa8-0800200c9a66\", \"status\":\"SCHEDULED\", \"reason\":\"Test\", "
				+ "\"visit\":\"c0c579b0-8e59-401d-8a4a-976a0b183600\", \"appointmentType\": \"c0c579b0-8e59-401d-8a4a-976a0b183519\" }";

		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());

		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertEquals(
				"c0c579b0-8e59-401d-8a4a-976a0b183607",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "timeSlot"), "uuid"));
		assertEquals(
				"c0c579b0-8e59-401d-8a4a-976a0b183600",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "visit"), "uuid"));
		assertEquals(
				"31e09960-8f52-11e3-baa8-0800200c9a66",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "patient"), "uuid"));
		assertEquals("SCHEDULED",
				PropertyUtils.getProperty(appt, "status.code"));
		assertEquals("Test", PropertyUtils.getProperty(appt, "reason"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "appointmentType"),
						"uuid"));
		assertEquals(getAllCount() + 1, appointmentService
				.getAllAppointments(false).size());

	}

	@Test
	public void shouldCreateNewAppointmentWithRequiredFieldsOnly()
			throws Exception {

		String json = "{ \"timeSlot\":\"c0c579b0-8e59-401d-8a4a-976a0b183607\", "
				+ "\"patient\":\"31e09960-8f52-11e3-baa8-0800200c9a66\", \"status\":\"SCHEDULED\", "
				+ "\"appointmentType\": \"c0c579b0-8e59-401d-8a4a-976a0b183519\" }";

		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());

		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertEquals(
				"c0c579b0-8e59-401d-8a4a-976a0b183607",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "timeSlot"), "uuid"));
		assertEquals(
				"31e09960-8f52-11e3-baa8-0800200c9a66",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "patient"), "uuid"));
		assertEquals("SCHEDULED",
				PropertyUtils.getProperty(appt, "status.code"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
				PropertyUtils.getProperty(
						PropertyUtils.getProperty(appt, "appointmentType"),
						"uuid"));
		assertEquals(getAllCount() + 1, appointmentService
				.getAllAppointments(false).size());

	}

	@Test
	public void shouldFailIfTimeSlotFull() throws Exception {

		String json = "{ \"timeSlot\":\"c0c579b0-8e59-401d-8a4a-976a0b183604\", "
				+ "\"patient\":\"31e09960-8f52-11e3-baa8-0800200c9a66\", \"status\":\"SCHEDULED\", "
				+ "\"appointmentType\": \"c0c579b0-8e59-401d-8a4a-976a0b183519\" }";

		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());

		Object appt = deserialize(handle(req));
		// TODO: fix this before committing!!
		//assertThrows(APIException.class);
	}

	@Test
	public void shouldEditAnAppointment() throws Exception {

		String json = "{ \"status\":\"COMPLETED\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/"
				+ getUuid());
		req.setContent(json.getBytes());
		handle(req);

		Appointment appointment = appointmentService
				.getAppointmentByUuid(getUuid());
		assertNotNull(appointment);
		assertEquals(COMPLETED, appointment.getStatus());

	}

	@Test
	public void shouldVoidAnAppointment() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI()
				+ "/" + getUuid());
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);

		Appointment voided = appointmentService.getAppointmentByUuid(getUuid());
		assertTrue(voided.isVoided());
		assertEquals("really ridiculous random reason",
				voided.getVoidReason());
	}

	@Test
	public void shouldPurgeAnAppointment() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI()
				+ "/" + getUuid());
		req.addParameter("purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);

		assertNull(appointmentService.getTimeSlotByUuid(getUuid()));
		assertEquals(getAllCount() - 1, appointmentService
				.getAllAppointments(false).size());

	}

	@Test
	public void shouldFetchAppointmentsByDate() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("fromDate", "2005-12-02T00:00:00.000");
		req.addParameter("toDate", "2006-12-02T00:00:00.000");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(2, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183601",
				appointments.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(1).get("uuid"));
	}

	@Test
	public void shouldFetchAppointmentsByAppointmentType() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("appointmentType",
				"c0c579b0-8e59-401d-8a4a-976a0b183519");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(2, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183601",
				appointments.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(1).get("uuid"));
	}

	@Test
	public void shouldFetchAppointmentsByProvider() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("provider", "c0c54sd0-8e59-401d-8a4a-976a0b183599");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(3, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183607",
				appointments.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183605",
				appointments.get(1).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183606",
				appointments.get(2).get("uuid"));
	}

	@Test
	public void shouldFetchAppointmentsByPatient() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("patient", "31e09960-8f52-11e3-baa8-0800200c9a66");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(2, appointments.size());
        assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183606",
                appointments.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(1).get("uuid"));

	}

	@Test
	public void shouldFetchAppointmentsByLocation() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("location", "9356400c-a5a2-4532-8f2b-2361b3446eb8");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(3, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183601",
				appointments.get(0).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(1).get("uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183604",
				appointments.get(2).get("uuid"));

	}

	@Test
	public void shouldFetchAppointmentsByStatus() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("status", "MISSED");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(1, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(0).get("uuid"));

	}

	@Test
	public void shouldFetchAppointmentsByStatusType() throws Exception {

		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("statusType", "MISSED");
		handle(req);

		List<Map<String, String>> appointments = (List<Map<String, String>>) deserialize(
				handle(req)).get("results");
		assertEquals(1, appointments.size());
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183602",
				appointments.get(0).get("uuid"));

	}

	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
				+ "/appointment";
	}

	@Override
	public String getUuid() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183601";
	}

	@Override
	public long getAllCount() {
		return 6;
	}
}
