package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AppointmentRequestResource1_9Test extends BaseDelegatingResourceTest<AppointmentRequestResource1_9, AppointmentRequest> {

    @Before
    public void setup() throws Exception {
        executeDataSet("standardWebAppointmentTestDataset.xml");
    }

    @Override
    public AppointmentRequest newObject() {
        return Context.getService(AppointmentService.class).getAppointmentRequestByUuid(getUuidProperty());
    }

    @Override
    public String getDisplayProperty() {
        return "Initial HIV Clinic Appointment : PENDING";
    }

    @Override
    public String getUuidProperty() {
        return "862c94f0-3dae-11e4-916c-0800200c9a66";
    }

    public void validateRefRepresentation() throws Exception {
        super.validateRefRepresentation();
        assertPropNotPresent("voided"); // note that the voided property is only present if the property is voided
    }

    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("status", getObject().getStatus());
        assertPropEquals("requestedOn", getObject().getRequestedOn());
        assertPropEquals("voided", getObject().isVoided());
        assertPropEquals("notes", getObject().getNotes());
        assertPropPresent("patient");
        assertPropPresent("appointmentType");
        assertPropPresent("provider");
        assertPropPresent("requestedBy");
        assertPropPresent("minTimeFrameValue");
        assertPropPresent("minTimeFrameUnits");
        assertPropPresent("maxTimeFrameValue");
        assertPropPresent("maxTimeFrameUnits");
        assertPropNotPresent("auditInfo");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("status", getObject().getStatus());
        assertPropEquals("requestedOn", getObject().getRequestedOn());
        assertPropEquals("voided", getObject().isVoided());
        assertPropEquals("notes", getObject().getNotes());
        assertPropPresent("patient");
        assertPropPresent("appointmentType");
        assertPropPresent("provider");
        assertPropPresent("requestedBy");
        assertPropPresent("minTimeFrameValue");
        assertPropPresent("minTimeFrameUnits");
        assertPropPresent("maxTimeFrameValue");
        assertPropPresent("maxTimeFrameUnits");
        assertPropPresent("auditInfo");
    }

}
