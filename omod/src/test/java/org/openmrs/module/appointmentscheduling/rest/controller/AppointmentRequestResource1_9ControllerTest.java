package org.openmrs.module.appointmentscheduling.rest.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;
import static org.openmrs.module.appointmentscheduling.rest.test.SameDatetimeMatcher.sameDatetime;

public class AppointmentRequestResource1_9ControllerTest extends MainResourceControllerTest {

    private AppointmentService appointmentService;

    @Before
    public void setup() throws Exception {
        appointmentService = Context.getService(AppointmentService.class);
        executeDataSet("standardWebAppointmentTestDataset.xml");
    }

    @Test
    public void shouldGetAppointmentRequestByUuid() throws Exception {

        AppointmentRequest appointmentRequest = appointmentService.getAppointmentRequestByUuid(getUuid());

        MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/"
                + getUuid());
        SimpleObject result = deserialize(handle(req));

        Assert.assertNotNull(result);
        Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("PENDING", PropertyUtils.getProperty(result, "status"));
        Assert.assertEquals("Initial HIV Clinic Appointment : PENDING", PropertyUtils.getProperty(result, "display"));
        Assert.assertEquals(0, PropertyUtils.getProperty(result, "minTimeFrameValue"));
        Assert.assertEquals("DAYS", PropertyUtils.getProperty(result, "minTimeFrameUnits"));
        Assert.assertEquals(7, PropertyUtils.getProperty(result, "maxTimeFrameValue"));
        Assert.assertEquals("DAYS", PropertyUtils.getProperty(result, "maxTimeFrameUnits"));
        Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519", Util.getByPath(result, "appointmentType/uuid"));
        Assert.assertEquals("31e09960-8f52-11e3-baa8-0800200c9a66", Util.getByPath(result, "patient/uuid"));
        Assert.assertEquals("c0c549b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "provider/uuid"));
        Assert.assertEquals("c0c549b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "requestedBy/uuid"));
        Assert.assertEquals("ASAP", PropertyUtils.getProperty(result, "notes"));
        assertThat((String) PropertyUtils.getProperty(result, "requestedOn"), sameDatetime(appointmentRequest.getRequestedOn()));
        Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));

    }

    @Test
    public void shouldCreateNewAppointmentRequestWithAllFields() throws Exception {

        String json = "{ \"provider\":\"c0c549b0-8e59-401d-8a4a-976a0b183599\", "
                + "\"requestedOn\":\"2014-01-03T10:00:00.000-0500\", \"requestedBy\":\"c0c54sd0-8e59-401d-8a4a-976a0b183599\", "
                + "\"patient\":\"22b47970-8f52-11e3-baa8-0800200c9a66\", \"status\":\"FULFILLED\", \"notes\":\"Test\", "
                + "\"minTimeFrameValue\":\"0\", \"minTimeFrameUnits\":\"DAYS\", "
                + "\"maxTimeFrameValue\":\"7\", \"maxTimeFrameUnits\":\"DAYS\", "
                + "\"appointmentType\": \"759799ab-c9a5-435e-b671-77773ada74e4\" }";

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        Object result = deserialize(handle(req));
        Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("FULFILLED", PropertyUtils.getProperty(result, "status"));
        Assert.assertEquals("Return TB Clinic Appointment : FULFILLED", PropertyUtils.getProperty(result, "display"));
        Assert.assertEquals(0, PropertyUtils.getProperty(result, "minTimeFrameValue"));
        Assert.assertEquals("DAYS", PropertyUtils.getProperty(result, "minTimeFrameUnits"));
        Assert.assertEquals(7, PropertyUtils.getProperty(result, "maxTimeFrameValue"));
        Assert.assertEquals("DAYS", PropertyUtils.getProperty(result, "maxTimeFrameUnits"));
        Assert.assertEquals("759799ab-c9a5-435e-b671-77773ada74e4", Util.getByPath(result, "appointmentType/uuid"));
        Assert.assertEquals("22b47970-8f52-11e3-baa8-0800200c9a66", Util.getByPath(result, "patient/uuid"));
        Assert.assertEquals("c0c549b0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "provider/uuid"));
        Assert.assertEquals("c0c54sd0-8e59-401d-8a4a-976a0b183599", Util.getByPath(result, "requestedBy/uuid"));
        Assert.assertEquals("Test", PropertyUtils.getProperty(result, "notes"));
        Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));

    }

    @Test
    public void shouldCreateNewAppointmentRequestWithRequiredFieldsOnly()
            throws Exception {

        String json = "{\"requestedOn\":\"2014-01-03T10:00:00.000-0500\", "
                + "\"patient\":\"22b47970-8f52-11e3-baa8-0800200c9a66\", \"status\":\"PENDING\", "
                + "\"appointmentType\": \"759799ab-c9a5-435e-b671-77773ada74e4\" }";

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        Object result = deserialize(handle(req));
        Assert.assertNotNull(PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("PENDING", PropertyUtils.getProperty(result, "status"));
        Assert.assertEquals("Return TB Clinic Appointment : PENDING", PropertyUtils.getProperty(result, "display"));
        Assert.assertEquals("759799ab-c9a5-435e-b671-77773ada74e4", Util.getByPath(result, "appointmentType/uuid"));
        Assert.assertEquals("22b47970-8f52-11e3-baa8-0800200c9a66", Util.getByPath(result, "patient/uuid"));
        Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));

    }

    @Test
    public void shouldEditAnAppointmentRequest() throws Exception {

        String json = "{ \"status\":\"FULFILLED\" }";
        MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
        req.setContent(json.getBytes());
        handle(req);

        AppointmentRequest appointmentRequest = appointmentService.getAppointmentRequestByUuid(getUuid());
        Assert.assertNotNull(appointmentRequest);
        Assert.assertEquals(AppointmentRequest.AppointmentRequestStatus.FULFILLED, appointmentRequest.getStatus());

    }

    @Test
    public void shouldVoidAnAppointment() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        req.addParameter("!purge", "");
        req.addParameter("reason", "really ridiculous random reason");
        handle(req);

        AppointmentRequest voided = appointmentService.getAppointmentRequestByUuid(getUuid()) ;
        Assert.assertTrue(voided.isVoided());
        Assert.assertEquals("really ridiculous random reason",voided.getVoidReason());
    }

    @Test
    public void shouldPurgeAnAppointment() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        req.addParameter("purge", "");
        req.addParameter("reason", "really ridiculous random reason");
        handle(req);

        Assert.assertNull(appointmentService.getAppointmentRequestByUuid(getUuid()));
        Assert.assertEquals(getAllCount() - 1, appointmentService.getAllAppointmentRequests(false).size());

    }

    @Test
    public void shouldFetchAppointmentRequestsByAppointmentType() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("appointmentType", "759799ab-c9a5-435e-b671-77773ada74e4");
        handle(req);

        List<Map<String, String>> appointmentRequests = (List<Map<String, String>>) deserialize(handle(req)).get("results");
        Assert.assertEquals(1, appointmentRequests.size());
        Assert.assertEquals("862c94f1-3dae-11e4-916c-0800200c9a66", appointmentRequests.get(0).get("uuid"));

    }

    @Test
    public void shouldFetchAppointmentRequestsByPatient() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("patient", "31e09960-8f52-11e3-baa8-0800200c9a66");
        handle(req);

        List<Map<String, String>> appointmentRequests = (List<Map<String, String>>) deserialize(handle(req)).get("results");
        Assert.assertEquals(1, appointmentRequests.size());
        Assert.assertEquals("862c94f0-3dae-11e4-916c-0800200c9a66", appointmentRequests.get(0).get("uuid"));

    }

    @Test
    public void shouldFetchAppointmentRequestsByProvider() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("provider", "c0c549b0-8e59-401d-8a4a-976a0b183599");
        handle(req);

        List<Map<String, String>> appointmentRequests = (List<Map<String, String>>) deserialize(handle(req)).get("results");
        Assert.assertEquals(2, appointmentRequests.size());
        Assert.assertTrue( (appointmentRequests.get(0).get("uuid").equals("862c94f0-3dae-11e4-916c-0800200c9a66")
                                && appointmentRequests.get(1).get("uuid").equals("862c94f1-3dae-11e4-916c-0800200c9a66")
                            || (appointmentRequests.get(1).get("uuid").equals("862c94f0-3dae-11e4-916c-0800200c9a66")
                                && appointmentRequests.get(0).get("uuid").equals("862c94f1-3dae-11e4-916c-0800200c9a66"))) );

    }

    @Test
    public void shouldFetchAppointmentRequestsByStatus() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("status", "PENDING");
        handle(req);

        List<Map<String, String>> appointmentRequests = (List<Map<String, String>>) deserialize(handle(req)).get("results");
        Assert.assertEquals(1, appointmentRequests.size());
        Assert.assertEquals("862c94f0-3dae-11e4-916c-0800200c9a66", appointmentRequests.get(0).get("uuid"));

    }


    @Override
    public String getURI() {
        return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
                + "/appointmentrequest";
    }

    @Override
    public String getUuid() {
        return "862c94f0-3dae-11e4-916c-0800200c9a66";
    }

    @Override
    public long getAllCount() {
        return 2;
    }

}