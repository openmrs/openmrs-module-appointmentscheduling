package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.validation.ValidationException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ScheduledAppointmentBlockResource1_9Test extends BaseModuleWebContextSensitiveTest {
	
	@Before
	public void setUp() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldSearchForScheduledAppointmentBlock() {
		ScheduledAppointmentBlockResource1_9 scheduledAppointmentBlockResource1_9 = new ScheduledAppointmentBlockResource1_9();
		
		RequestContext context = Mockito.mock(RequestContext.class);
		when(context.getParameter("date")).thenReturn("2005-01-03 00:00:00.0");
		when(context.getParameter("location")).thenReturn("9356400c-a5a2-4532-8f2b-2361b3446eb7");
		when(context.getParameter("appointmentType")).thenReturn("759799ab-c9a5-435e-b671-77773ada74e6");
		
		SimpleObject search = scheduledAppointmentBlockResource1_9.search(context);
		assertSearchResults(search);
		
		List resultList = (List) search.get("results");
		assertRepresentationOfEachResult(resultList);
		
		SimpleObject firstResult = (SimpleObject) resultList.get(0);
		
		SimpleObject appointmentBlock = (SimpleObject) firstResult.get("appointmentBlock");
		assertAppointmentBlockResult(appointmentBlock);
		
		List appointments = (List) firstResult.get("appointments");
		assertAppointmentsResult(appointments);
	}
	
	private void assertAppointmentsResult(List appointments) {
		assertEquals(2, appointments.size());
		
		SimpleObject firstAppointment = (SimpleObject) appointments.get(0);
		
		assertTrue(firstAppointment.containsKey("patient"));
		assertEquals("22b47970-8f52-11e3-baa8-0800200c9a66", ((SimpleObject) firstAppointment.get("patient")).get("uuid")
		        .toString());
		assertTrue(firstAppointment.containsKey("appointmentType"));
		assertEquals("759799ab-c9a5-435e-b671-77773ada74e6",
		    ((SimpleObject) firstAppointment.get("appointmentType")).get("uuid").toString());
		
		SimpleObject secondAppointment = (SimpleObject) appointments.get(1);
		
		assertTrue(secondAppointment.containsKey("patient"));
		assertEquals("31e09960-8f52-11e3-baa8-0800200c9a66", ((SimpleObject) secondAppointment.get("patient")).get("uuid")
		        .toString());
		assertTrue(secondAppointment.containsKey("appointmentType"));
		assertEquals("759799ab-c9a5-435e-b671-77773ada74e6",
		    ((SimpleObject) secondAppointment.get("appointmentType")).get("uuid").toString());
		
	}
	
	private void assertAppointmentBlockResult(SimpleObject appointmentBlock) {
		assertTrue(appointmentBlock.containsKey("provider"));
		assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599", ((SimpleObject) appointmentBlock.get("provider")).get("uuid")
		        .toString());
		
		assertTrue(appointmentBlock.containsKey("location"));
		assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb7", ((SimpleObject) appointmentBlock.get("location")).get("uuid")
		        .toString());
		
		assertTrue(appointmentBlock.containsKey("startDate"));
		assertEquals("2005-01-03T00:00:00.000-0200", appointmentBlock.get("startDate").toString());
		
		assertTrue(appointmentBlock.containsKey("endDate"));
		assertEquals("2005-01-03T11:00:00.000-0200", appointmentBlock.get("endDate").toString());
	}
	
	private void assertRepresentationOfEachResult(List results) {
		SimpleObject firstResult = (SimpleObject) results.get(0);
		
		assertTrue(firstResult.containsKey("appointmentBlock"));
		assertTrue(firstResult.containsKey("appointments"));
	}
	
	private void assertSearchResults(SimpleObject search) {
		assertNotNull(search);
		assertTrue(search.containsKey("results"));
		assertEquals(1, search.size());
	}
	
	@Test(expected = ValidationException.class)
	public void shouldReturnErrorWhenDateParameterIsMissing() {
		ScheduledAppointmentBlockResource1_9 scheduledAppointmentBlockResource1_9 = new ScheduledAppointmentBlockResource1_9();
		
		RequestContext context = Mockito.mock(RequestContext.class);
		when(context.getParameter("date")).thenReturn(null);
		when(context.getParameter("location")).thenReturn("9356400c-a5a2-4532-8f2b-2361b3446eb7");
		
		scheduledAppointmentBlockResource1_9.search(context);
	}
	
	@Test(expected = ValidationException.class)
	public void shouldReturnErrorWhenLocationParameterIsMissing() {
		ScheduledAppointmentBlockResource1_9 scheduledAppointmentBlockResource1_9 = new ScheduledAppointmentBlockResource1_9();
		
		RequestContext context = Mockito.mock(RequestContext.class);
		when(context.getParameter("date")).thenReturn("2005-01-03 00:00:00.0");
		when(context.getParameter("location")).thenReturn(null);
		
		scheduledAppointmentBlockResource1_9.search(context);
	}
}
