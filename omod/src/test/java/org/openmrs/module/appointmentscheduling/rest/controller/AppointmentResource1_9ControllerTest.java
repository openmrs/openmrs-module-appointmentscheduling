package org.openmrs.module.appointmentscheduling.rest.controller;


import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class AppointmentResource1_9ControllerTest extends MainResourceControllerTest {

    private AppointmentService appointmentService;

    @Before
    public void setup() throws Exception {
        appointmentService = Context.getService(AppointmentService.class);
        executeDataSet("standardWebAppointmentTestDataset.xml");
    }

    @Test
    public void shouldGetAppointmentByUuid() throws Exception {

        MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
        SimpleObject result = deserialize(handle(req));

        Assert.assertNotNull(result);
        Assert.assertEquals(getUuid(), PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("SCHEDULED", PropertyUtils.getProperty(result, "status"));
        Assert.assertEquals("Initial HIV Clinic Appointment : Scheduled",
                PropertyUtils.getProperty(result, "display"));

        Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519", Util.getByPath(result, "appointmentType/uuid"));
        Assert.assertEquals(false, PropertyUtils.getProperty(result, "voided"));
    }

    @Test
    public void shouldCreateNewAppointment() throws Exception {

        String json = "{ \"timeSlot\":\"c0c579b0-8e59-401d-8a4a-976a0b183604\", " +
                "\"patient\":\"31e09960-8f52-11e3-baa8-0800200c9a66\", \"status\":\"SCHEDULED\", \"reason\":\"Test\", "
                + "\"visit\":\"c0c579b0-8e59-401d-8a4a-976a0b183600\", \"appointmentType\": \"c0c579b0-8e59-401d-8a4a-976a0b183519\" }";

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        Object appt = deserialize(handle(req));
        Assert.assertNotNull(PropertyUtils.getProperty(appt, "uuid"));
        Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183604",
                PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "timeSlot"), "uuid"));
        Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183600",
                PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "visit"), "uuid"));
        Assert.assertEquals("31e09960-8f52-11e3-baa8-0800200c9a66",
                PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "patient"), "uuid"));
        Assert.assertEquals("SCHEDULED", PropertyUtils.getProperty(appt, "status"));
        Assert.assertEquals("Test", PropertyUtils.getProperty(appt, "reason"));
        Assert.assertEquals("c0c579b0-8e59-401d-8a4a-976a0b183519",
                PropertyUtils.getProperty(PropertyUtils.getProperty(appt, "appointmentType"), "uuid"));
        Assert.assertEquals(getAllCount() + 1, appointmentService.getAllAppointments().size());

    }


    @Override
    public String getURI() {
        return AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointment";
    }

    @Override
    public String getUuid() {
        return "c0c579b0-8e59-401d-8a4a-976a0b183601";
    }

    @Override
    public long getAllCount() {
        return 4;
    }
}
