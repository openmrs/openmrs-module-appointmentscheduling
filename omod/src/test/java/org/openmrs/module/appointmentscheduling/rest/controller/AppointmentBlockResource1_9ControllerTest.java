package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class AppointmentBlockResource1_9ControllerTest extends MainResourceControllerTest {
	
	private AppointmentService appointmentService;
	
	@Before
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldGetAppointmentBlockByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertTrue(PropertyUtils.getProperty(result, "startDate").toString().contains("2005-01-01T00:00:00.00"));
        Assert.assertTrue(PropertyUtils.getProperty(result, "endDate").toString().contains("2005-01-01T11:00:00.00"));
		Assert.assertEquals("2005-01-01T11:00:00.000-0500", PropertyUtils.getProperty(result, "endDate"));
		Assert.assertEquals("Super User, Xanadu: 2005-01-01 00:00:00.0 - 2005-01-01 11:00:00.0",
		    PropertyUtils.getProperty(result, "display"));
		
		Assert.assertEquals("c0c549b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "provider/uuid"));
		
		Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8", Util.getByPath(result, "location/uuid"));
		Assert.assertEquals("Xanadu", Util.getByPath(result, "location/name"));
		
		Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519", Util.getByPath(result, "types[0]/uuid"));
		Assert.assertEquals("Initial HIV Clinic Appointment", Util.getByPath(result, "types[0]/name"));
		Assert.assertEquals("759799ab-c9a5-435e-b671-77773ada74e4", Util.getByPath(result, "types[1]/uuid"));
		Assert.assertEquals("Return TB Clinic Appointment", Util.getByPath(result, "types[1]/name"));
		Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));
		
	}
	
	@Test
	public void shouldGetFullAppointmentBlockByUuid() throws Exception {
		
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + getUuid(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assert.assertTrue(PropertyUtils.getProperty(result, "startDate").toString().contains("2005-01-01T00:00:00.00"));
        Assert.assertTrue(PropertyUtils.getProperty(result, "endDate").toString().contains("2005-01-01T11:00:00.00"));
		Assert.assertEquals("Super User, Xanadu: 2005-01-01 00:00:00.0 - 2005-01-01 11:00:00.0",
		    PropertyUtils.getProperty(result, "display"));
		
		Assert.assertEquals("c0c549b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "provider/uuid"));
		Assert.assertEquals("Super User", Util.getByPath(result, "provider/person/preferredName/display"));
		
		Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8", Util.getByPath(result, "location/uuid"));
		Assert.assertEquals("Xanadu", Util.getByPath(result, "location/name"));
		
		Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519", Util.getByPath(result, "types[0]/uuid"));
		Assert.assertEquals("Initial HIV Clinic Appointment", Util.getByPath(result, "types[0]/name"));
		Assert.assertEquals("759799ab-c9a5-435e-b671-77773ada74e4", Util.getByPath(result, "types[1]/uuid"));
		Assert.assertEquals("Return TB Clinic Appointment", Util.getByPath(result, "types[1]/name"));
		Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));
		
	}
	
	@Test
	public void shouldCreateNewAppointmentBlock() throws Exception {
		
		int originalCount = 3;
		String json = "{ \"startDate\":\"2005-03-01T00:00:00.000-0500\", \"endDate\":\"2005-03-01T11:00:00.000-0500\", "
		        + "\"provider\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\", \"location\":\"9356400c-a5a2-4532-8f2b-2361b3446eb8\", "
		        + "\"types\": [ \"c0c579b0-8e59-401d-8a4a-976a0b183519\" ]" + "}";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		Assert.assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
        Assert.assertTrue(PropertyUtils.getProperty(appt, "startDate").toString().contains("2005-03-01T00:00:00.00"));
        Assert.assertTrue(PropertyUtils.getProperty(appt, "endDate").toString().contains("2005-03-01T11:00:00.00"));
		Assert.assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "provider"), "uuid"));
		Assert.assertEquals("9356400c-a5a2-4532-8f2b-2361b3446eb8",
		    PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "location"), "uuid"));
		Assert.assertEquals(originalCount + 1, appointmentService.getAllAppointmentTypes().size());
		
	}
	
	@Test
	public void shouldEditAnAppointmentBlock() throws Exception {
		
		String json = "{ \"provider\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183599");
		req.setContent(json.getBytes());
		handle(req);
		
		AppointmentBlock updated = appointmentService.getAppointmentBlockByUuid("c0c579b0-8e59-401d-8a4a-976a0b183599");
		Assert.assertNotNull(updated);
		Assert.assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599", updated.getProvider().getUuid());
		
	}
	
	@Test
	public void shouldVoidAnAppointmentBlock() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183599");
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		AppointmentBlock voided = appointmentService.getAppointmentBlockByUuid("c0c579b0-8e59-401d-8a4a-976a0b183599");
		Assert.assertTrue(voided.isVoided());
		Assert.assertEquals("really ridiculous random reason", voided.getVoidReason());
	}
	
	@Test
	public void shouldPurgeAnAppointmentBlock() throws Exception {
		
		int originalCount = 3;
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/759799ab-c9a5-435e-b671-77773ada7499");
		req.addParameter("purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		Assert.assertNull(appointmentService.getAppointmentBlockByUuid("759799ab-c9a5-435e-b671-77773ada7499"));
		Assert.assertEquals(originalCount - 1, appointmentService.getAllAppointmentBlocks().size());
		
	}
	
	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_REST_NAMESPACE + "/appointmentblock";
	}
	
	@Override
	public String getUuid() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183599";
	}
	
	@Override
	public long getAllCount() {
		return 2;
	}
}
