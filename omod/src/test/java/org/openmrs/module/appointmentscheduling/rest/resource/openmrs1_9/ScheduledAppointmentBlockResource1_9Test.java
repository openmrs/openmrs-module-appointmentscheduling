package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import static org.junit.Assert.assertNotNull;
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
		
		SimpleObject search = scheduledAppointmentBlockResource1_9.search(context);
		
		assertNotNull(search);
		
	}
}
