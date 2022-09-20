package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.openmrs.module.appointmentscheduling.rest.test.SameDatetimeMatcher.sameDatetime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppointmentBlockWithTimeSlotResource1_9ControllerTest extends AppointmentBlockResource1_9ControllerTest {
	
	// runs all the basic AppointmentBlockResource1_9 tests plus these additional tests
	
	@Test
	public void shouldCreateNewAppointmentBlockWithTimeSlot() throws Exception {
		
		String json = "{ \"startDate\":\"2005-03-01T00:00:00.000-0500\", \"endDate\":\"2005-03-01T11:00:00.000-0500\", "
		        + "\"provider\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", "
		        + "\"types\": [ \"c0c579b0-8e59-401d-8a4a-976a0b183519\" ]" + "}";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertThat((String) PropertyUtils.getProperty(appt, "startDate"), sameDatetime("2005-03-01T00:00:00.000-0500"));
		assertThat((String) PropertyUtils.getProperty(appt, "endDate"), sameDatetime("2005-03-01T11:00:00.000-0500"));
		assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "provider"), "uuid"));
		assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "location"), "uuid"));
		
		// make sure a time slot has been created for the appointment block
		AppointmentBlock appointmentBlock = appointmentService.getAppointmentBlockByUuid(PropertyUtils.getProperty(appt,
		    "uuid").toString());
		assertNotNull(appointmentBlock);
		TimeSlot timeSlot = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock).get(0);
		assertNotNull(timeSlot);
		assertEquals(appointmentBlock.getStartDate(), timeSlot.getStartDate());
		assertEquals(appointmentBlock.getEndDate(), timeSlot.getEndDate());
		
	}
	
	@Test
	public void shouldEditAnAppointmentBlockAndUpdateTimeSlots() throws Exception {
		
		String json = "{ \"provider\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183599");
		req.setContent(json.getBytes());
		handle(req);
		
		AppointmentBlock updated = appointmentService.getAppointmentBlockByUuid("c0c579b0-8e59-401d-8a4a-976a0b183599");
		assertNotNull(updated);
		assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599", updated.getProvider().getUuid());
		
		// make sure a time slot has been updated for the appointment block
		List<TimeSlot> timeSlots = appointmentService.getTimeSlotsInAppointmentBlock(updated);
		assertEquals(1, timeSlots.size());
		TimeSlot timeSlot = timeSlots.get(0);
		assertNotNull(timeSlot);
		assertEquals(updated.getStartDate(), timeSlot.getStartDate());
		assertEquals(updated.getEndDate(), timeSlot.getEndDate());
	}
	
	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentblockwithtimeslot";
	}
}
