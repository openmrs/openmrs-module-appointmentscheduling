package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.ProviderSchedule;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ProviderScheduleResource1_9Test extends BaseDelegatingResourceTest<ProviderScheduleResource1_9, ProviderSchedule> {

    @Before
    public void setup() throws Exception {
        executeDataSet("standardWebAppointmentTestDataset.xml");
    }

    @Override
    public ProviderSchedule newObject() {
        return Context.getService(AppointmentService.class).getProviderScheduleByUuid(getUuidProperty());
    }

    @Override
    public void validateRefRepresentation() throws Exception {
        super.validateRefRepresentation();
        assertPropEquals("voided", null); // voided parameter only included if voided
    }


    @Override
    public void validateDefaultRepresentation() throws Exception {
        super.validateDefaultRepresentation();
        assertPropEquals("startDate", getObject().getStartDate());
        assertPropEquals("endDate", getObject().getEndDate());
        assertPropPresent("provider");
        assertPropPresent("location");
        assertPropPresent("types");
        assertPropNotPresent("auditInfo");
    }

    @Override
    public void validateFullRepresentation() throws Exception {
        super.validateFullRepresentation();
        assertPropEquals("startDate", getObject().getStartDate());
        assertPropEquals("endDate", getObject().getEndDate());
        assertPropPresent("provider");
        assertPropPresent("location");
        assertPropPresent("types");
        assertPropPresent("auditInfo");
    }

    @Override
    public String getDisplayProperty() {
        return "Xanadu: 07:00:00 - 18:00:00";
    }

    @Override
    public String getUuidProperty() {
        return "c0c579b0-ffff-401d-8a4a-976a0b183599";
    }

}