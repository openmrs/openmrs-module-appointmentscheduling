package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class AppointmentStatusResource1_9Test extends BaseModuleWebContextSensitiveTest {
	
	@Test
	public void testGetAll() throws Exception {
		
		AppointmentStatusResource1_9 appointmentStatusResource1_9 = new AppointmentStatusResource1_9();
		RequestContext requestContext = new RequestContext();
		SimpleObject simpleObject = appointmentStatusResource1_9.getAll(requestContext);

		List<SimpleObject> results = (List<SimpleObject>) simpleObject.get("results");
		assertThat(results.size(), is(9));

		List<String> names = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		List<String> codes = new ArrayList<String>();

		for (SimpleObject result : results) {
			names.add((String) result.get("name"));
			types.add((String) result.get("type"));
			codes.add((String) result.get("code"));
		}

		Assert.assertTrue(names.contains("Scheduled"));
		Assert.assertTrue(names.contains("Rescheduled"));
		Assert.assertTrue(names.contains("Walk-In"));
		Assert.assertTrue(names.contains("Waiting"));
		Assert.assertTrue(names.contains("In-Consultation"));
		Assert.assertTrue(names.contains("Cancelled"));
		Assert.assertTrue(names.contains("Cancelled and Needs Reschedule"));
		Assert.assertTrue(names.contains("Completed"));

		Assert.assertTrue(codes.contains("SCHEDULED"));
		Assert.assertTrue(codes.contains("RESCHEDULED"));
		Assert.assertTrue(codes.contains("WALKIN"));
		Assert.assertTrue(codes.contains("WAITING"));
		Assert.assertTrue(codes.contains("INCONSULTATION"));
		Assert.assertTrue(codes.contains("CANCELLED"));
		Assert.assertTrue(codes.contains("CANCELLED_AND_NEEDS_RESCHEDULE"));
		Assert.assertTrue(codes.contains("COMPLETED"));

		Assert.assertTrue(types.contains("SCHEDULED"));
		Assert.assertTrue(types.contains("ACTIVE"));
		Assert.assertTrue(types.contains("MISSED"));
		Assert.assertTrue(types.contains("CANCELLED"));
		Assert.assertTrue(types.contains("COMPLETED"));


	}

}
