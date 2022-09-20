package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

public class AppointmentStatusResource1_9Test extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void testName() throws Exception {
		
		AppointmentStatusResource1_9 appointmentStatusResource1_9 = new AppointmentStatusResource1_9();
		RequestContext requestContext = new RequestContext();
		SimpleObject simpleObject = appointmentStatusResource1_9.getAll(requestContext);
		
		String appointmentStatusJson = toJson(simpleObject);
		
		for (AppointmentStatus appointmentStatus : AppointmentStatus.values()) {
			assertTrue(appointmentStatusJson.contains(appointmentStatus.getName()));
		}
	}
	
	private String toJson(Object object) throws IOException {
		return new ObjectMapper().writeValueAsString(object);
	}
	
}
