package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class AppointmentStatusResource1_9Test extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void testName() throws Exception {
		
		AppointmentStatusResource1_9 appointmentStatusResource1_9 = new AppointmentStatusResource1_9();
		RequestContext requestContext = new RequestContext();
		SimpleObject simpleObject = appointmentStatusResource1_9.getAll(requestContext);
		
		String appointmentStatusJson = toJson(simpleObject);
		
		for (Appointment.AppointmentStatus appointmentStatus : Appointment.AppointmentStatus.values()) {
			assertTrue(appointmentStatusJson.contains(appointmentStatus.getName()));
		}
	}
	
	private String toJson(Object object) throws IOException {
		return new ObjectMapper().writeValueAsString(object);
	}
	
}
