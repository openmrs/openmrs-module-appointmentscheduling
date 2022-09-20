package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppointmentAllowingOverbookResource1_9ControllerTest extends MainResourceControllerTest {

	@Autowired
	private AppointmentService appointmentService;
	
	@BeforeEach
	public void setup() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldCreateNewAppointmentEvenIfTimeBlockFull() throws Exception {
		
		String json = "{ \"timeSlot\":\"c0c579b0-8e59-401d-8a4a-976a0b183604\", "
		        + "\"patient\":\"31e09960-8f52-11e3-baa8-0800200c9a66\", \"status\":\"SCHEDULED\", "
		        + "\"appointmentType\": \"c0c579b0-8e59-401d-8a4a-976a0b183519\" }";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183604",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "timeSlot"), "uuid"));
		assertEquals("31e09960-8f52-11e3-baa8-0800200c9a66",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "patient"), "uuid"));
		assertEquals("SCHEDULED", PropertyUtils.getProperty(appt, "status.code"));
		assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "appointmentType"), "uuid"));
		assertEquals(getAllCount() + 1, appointmentService.getAllAppointments(false).size());
		
	}
	
	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentallowingoverbook";
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
