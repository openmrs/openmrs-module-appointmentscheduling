package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class AppointmentAllowingOverbookResource1_9ControllerTest extends MainResourceControllerTest {
	
	private AppointmentService appointmentService;
	
	@Before
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
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
		Assert.assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183604",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "timeSlot"), "uuid"));
		Assert.assertEquals("31e09960-8f52-11e3-baa8-0800200c9a66",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "patient"), "uuid"));
		Assert.assertEquals("SCHEDULED", PropertyUtils.getProperty(appt, "status.code"));
		Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "appointmentType"), "uuid"));
		Assert.assertEquals(getAllCount() + 1, appointmentService.getAllAppointments(false).size());
		
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
