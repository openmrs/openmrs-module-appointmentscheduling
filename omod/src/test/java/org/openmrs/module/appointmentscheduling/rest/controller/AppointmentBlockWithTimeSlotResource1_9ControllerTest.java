package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.appointmentscheduling.rest.test.SameDatetimeMatcher.sameDatetime;

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
		Assert.assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertThat((String) PropertyUtils.getProperty(appt, "startDate"), sameDatetime("2005-03-01T00:00:00.000-0500"));
		assertThat((String) PropertyUtils.getProperty(appt, "endDate"), sameDatetime("2005-03-01T11:00:00.000-0500"));
		Assert.assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "provider"), "uuid"));
		Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "location"), "uuid"));
		
		// make sure a time slot has been created for the appointment block
		AppointmentBlock appointmentBlock = appointmentService.getAppointmentBlockByUuid(PropertyUtils.getProperty(appt,
		    "uuid").toString());
		Assert.assertNotNull(appointmentBlock);
		TimeSlot timeSlot = appointmentService.getTimeSlotsInAppointmentBlock(appointmentBlock).get(0);
		Assert.assertNotNull(timeSlot);
		Assert.assertEquals(appointmentBlock.getStartDate(), timeSlot.getStartDate());
		Assert.assertEquals(appointmentBlock.getEndDate(), timeSlot.getEndDate());
		
	}
	
	@Test
	public void shouldEditAnAppointmentBlockAndUpdateTimeSlots() throws Exception {
		
		String json = "{ \"provider\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183599");
		req.setContent(json.getBytes());
		handle(req);
		
		AppointmentBlock updated = appointmentService.getAppointmentBlockByUuid("c0c579b0-8e59-401d-8a4a-976a0b183599");
		Assert.assertNotNull(updated);
		Assert.assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599", updated.getProvider().getUuid());
		
		// make sure a time slot has been updated for the appointment block
		List<TimeSlot> timeSlots = appointmentService.getTimeSlotsInAppointmentBlock(updated);
		Assert.assertEquals(1, timeSlots.size());
		TimeSlot timeSlot = timeSlots.get(0);
		Assert.assertNotNull(timeSlot);
		Assert.assertEquals(updated.getStartDate(), timeSlot.getStartDate());
		Assert.assertEquals(updated.getEndDate(), timeSlot.getEndDate());
	}
	
	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentblockwithtimeslot";
	}
}
