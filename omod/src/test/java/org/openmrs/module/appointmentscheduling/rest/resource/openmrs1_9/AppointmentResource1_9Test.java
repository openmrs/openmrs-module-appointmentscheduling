package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentData;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AppointmentResource1_9Test extends BaseDelegatingResourceTest<AppointmentResource1_9, AppointmentData> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Override
	public AppointmentData newObject() {
		return Context.getService(AppointmentService.class).getAppointmentDataByUuid(getUuidProperty());
	}
	
	@Override
	public String getDisplayProperty() {
		return "Initial HIV Clinic Appointment : Scheduled";
	}
	
	@Override
	public String getUuidProperty() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183601";
	}
	
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropNotPresent("status");
		assertPropNotPresent("voided"); // note that the voided property is only present if the property is voided
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		Object obj = getObject();
		assertPropEquals("reason", getObject().getReason());
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("visit");
		assertPropPresent("patient");
		assertPropPresent("appointmentType");
		assertPropPresent("status");
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("reason", getObject().getReason());
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("visit");
		assertPropPresent("patient");
		assertPropPresent("appointmentType");
		assertPropPresent("status");
		assertPropPresent("auditInfo");
	}
}
