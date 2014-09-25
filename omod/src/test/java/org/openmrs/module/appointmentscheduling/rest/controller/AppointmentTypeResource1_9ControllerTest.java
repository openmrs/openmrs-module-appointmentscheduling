package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class AppointmentTypeResource1_9ControllerTest extends MainResourceControllerTest {
	
	private AppointmentService appointmentService;
	
	@Before
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldGetAppointmentTypeByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "name"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "display"));
		Assert.assertEquals("Initial HIV Clinic Appointment Description", PropertyUtils.getProperty(result, "description"));
		Assert.assertEquals(45, PropertyUtils.getProperty(result, "duration"));
		Assert.assertEquals(true, PropertyUtils.getProperty(result, "confidential"));
		Assert.assertEquals(false, PropertyUtils.getProperty(result, "retired"));
	}
	
	@Test
	public void shouldGetFullAppointmentTypeByUuid() throws Exception {
		
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + getUuid(), new Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "name"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "display"));
		Assert.assertEquals("Initial HIV Clinic Appointment Description", PropertyUtils.getProperty(result, "description"));
		Assert.assertEquals(45, PropertyUtils.getProperty(result, "duration"));
        Assert.assertEquals(true, PropertyUtils.getProperty(result, "confidential"));
		Assert.assertEquals(false, PropertyUtils.getProperty(result, "retired"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldCreateNewAppointmentType() throws Exception {
		
		int originalCount = 4;
		String json = "{ \"name\":\"new type\", \"description\":\"new description\", \"duration\":\"24\" }";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		Assert.assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		Assert.assertEquals(PropertyUtils.getProperty(appt, "name"), "new type");
		Assert.assertEquals(PropertyUtils.getProperty(appt, "description"), "new description");
		Assert.assertEquals(PropertyUtils.getProperty(appt, "duration"), 24);
		Assert.assertEquals(originalCount + 1, appointmentService.getAllAppointmentTypes().size());
	}
	
	@Test
	public void shouldEditAnAppointmentType() throws Exception {
		String json = "{ \"name\":\"new name\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183519");
		req.setContent(json.getBytes());
		handle(req);
		
		AppointmentType updated = appointmentService.getAppointmentTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		Assert.assertNotNull(updated);
		Assert.assertEquals("new name", updated.getName());
	}
	
	@Test
	public void shouldRetireAnAppointmentType() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183519");
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		AppointmentType retired = appointmentService.getAppointmentTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		Assert.assertTrue(retired.isRetired());
		Assert.assertEquals("really ridiculous random reason", retired.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAnAppointmentType() throws Exception {
		
		int originalCount = 4;
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/759799ab-c9a5-435e-b671-77373ada74e6");
		req.addParameter("purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		Assert.assertNull(appointmentService.getAppointmentTypeByUuid("759799ab-c9a5-435e-b671-77373ada74e6"));
		Assert.assertEquals(originalCount - 1, appointmentService.getAllAppointmentTypes().size());
		
	}
	
	@Test
	public void shouldReturnAppointmentTypesSorted() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		SimpleObject result = deserialize(handle(req));
		Assert.assertNotNull(result);
		
		List<Object> types = (List<Object>) result.get("results");
		Assert.assertEquals(3, types.size());
		Assert.assertEquals("Hospitalization2", PropertyUtils.getProperty(types.get(0), "name"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(types.get(1), "name"));
		Assert.assertEquals("Return TB Clinic Appointment", PropertyUtils.getProperty(types.get(2), "name"));
	}
	
	@Test
	public void shouldSearchByStringAndReturnMatchingAppointmentTypesIncludingRetired() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		req.addParameter("q", "al");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(3, hits.size());
		Assert.assertEquals("Hospitalization", PropertyUtils.getProperty(hits.get(0), "display"));
		Assert.assertEquals(true, PropertyUtils.getProperty(hits.get(0), "retired"));
		Assert.assertEquals("Hospitalization2", PropertyUtils.getProperty(hits.get(1), "display"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(hits.get(2), "display"));
	}
	
	@Test
	public void shouldSearchByStringAndReturnMatchingAppointmentTypesIgnoringRetired() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "al");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(2, hits.size());
		Assert.assertEquals("Hospitalization2", PropertyUtils.getProperty(hits.get(0), "display"));
		Assert.assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(hits.get(1), "display"));
	}
	
	@Override
	public String getURI() {
		return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmenttype";
	}
	
	@Override
	public String getUuid() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183519";
	}
	
	@Override
	public long getAllCount() {
		return 3; // excluding the retired type
	}
}
