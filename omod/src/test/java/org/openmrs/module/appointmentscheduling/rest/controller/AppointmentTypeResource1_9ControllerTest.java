package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.jupiter.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppointmentTypeResource1_9ControllerTest extends MainResourceControllerTest {
	
	private AppointmentService appointmentService;
	
	@BeforeEach
	public void setup() throws Exception {
		appointmentService = Context.getService(AppointmentService.class);
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldGetAppointmentTypeByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(result);
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "name"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "display"));
		assertEquals("Initial HIV Clinic Appointment Description", PropertyUtils.getProperty(result, "description"));
		assertEquals(45, PropertyUtils.getProperty(result, "duration"));
		assertEquals(true, PropertyUtils.getProperty(result, "confidential"));
		assertEquals(false, PropertyUtils.getProperty(result, "retired"));
	}
	
	@Test
	public void shouldGetFullAppointmentTypeByUuid() throws Exception {
		
		MockHttpServletRequest req = newGetRequest(getURI() + "/" + getUuid(), new org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest.Parameter(
		        RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL));
		
		SimpleObject result = deserialize(handle(req));
		
		assertNotNull(result);
		assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "name"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(result, "display"));
		assertEquals("Initial HIV Clinic Appointment Description", PropertyUtils.getProperty(result, "description"));
		assertEquals(45, PropertyUtils.getProperty(result, "duration"));
        assertEquals(true, PropertyUtils.getProperty(result, "confidential"));
		assertEquals(false, PropertyUtils.getProperty(result, "retired"));
		assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
	}
	
	@Test
	public void shouldCreateNewAppointmentType() throws Exception {
		
		int originalCount = 4;
		String json = "{ \"name\":\"new type\", \"description\":\"new description\", \"duration\":\"24\" }";
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		Object appt = deserialize(handle(req));
		assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
		assertEquals(PropertyUtils.getProperty(appt, "name"), "new type");
		assertEquals(PropertyUtils.getProperty(appt, "description"), "new description");
		assertEquals(PropertyUtils.getProperty(appt, "duration"), 24);
		assertEquals(originalCount + 1, appointmentService.getAllAppointmentTypes().size());
	}
	
	@Test
	public void shouldEditAnAppointmentType() throws Exception {
		String json = "{ \"name\":\"new name\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183519");
		req.setContent(json.getBytes());
		handle(req);
		
		AppointmentType updated = appointmentService.getAppointmentTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		assertNotNull(updated);
		assertEquals("new name", updated.getName());
	}
	
	@Test
	public void shouldRetireAnAppointmentType() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/c0c579b0-8e59-401d-8a4a-976a0b183519");
		req.addParameter("!purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		AppointmentType retired = appointmentService.getAppointmentTypeByUuid("c0c579b0-8e59-401d-8a4a-976a0b183519");
		assertTrue(retired.isRetired());
		assertEquals("really ridiculous random reason", retired.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAnAppointmentType() throws Exception {
		
		int originalCount = 4;
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/759799ab-c9a5-435e-b671-77373ada74e6");
		req.addParameter("purge", "");
		req.addParameter("reason", "really ridiculous random reason");
		handle(req);
		
		assertNull(appointmentService.getAppointmentTypeByUuid("759799ab-c9a5-435e-b671-77373ada74e6"));
		assertEquals(originalCount - 1, appointmentService.getAllAppointmentTypes().size());
		
	}
	
	@Test
	public void shouldReturnAppointmentTypesSorted() throws Exception {
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.setParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_DEFAULT);
		SimpleObject result = deserialize(handle(req));
		assertNotNull(result);
		
		List<Object> types = (List<Object>) result.get("results");
		assertEquals(3, types.size());
		assertEquals("Hospitalization2", PropertyUtils.getProperty(types.get(0), "name"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(types.get(1), "name"));
		assertEquals("Return TB Clinic Appointment", PropertyUtils.getProperty(types.get(2), "name"));
	}
	
	@Test
	public void shouldSearchByStringAndReturnMatchingAppointmentTypesIncludingRetired() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_INCLUDE_ALL, String.valueOf(true));
		req.addParameter("q", "al");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		assertEquals(3, hits.size());
		assertEquals("Hospitalization", PropertyUtils.getProperty(hits.get(0), "display"));
		assertEquals(true, PropertyUtils.getProperty(hits.get(0), "retired"));
		assertEquals("Hospitalization2", PropertyUtils.getProperty(hits.get(1), "display"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(hits.get(2), "display"));
	}
	
	@Test
	public void shouldSearchByStringAndReturnMatchingAppointmentTypesIgnoringRetired() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "al");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		assertEquals(2, hits.size());
		assertEquals("Hospitalization2", PropertyUtils.getProperty(hits.get(0), "display"));
		assertEquals("Initial HIV Clinic Appointment", PropertyUtils.getProperty(hits.get(1), "display"));
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
