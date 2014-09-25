package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AppointmentTypeResource1_9Test extends BaseDelegatingResourceTest<AppointmentTypeResource1_9, AppointmentType> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Override
	public AppointmentType newObject() {
		return Context.getService(AppointmentService.class).getAppointmentTypeByUuid(getUuidProperty());
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropEquals("retired", getObject().isRetired());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("duration", getObject().getDuration());
		assertPropEquals("confidential", getObject().isConfidential());
		assertPropEquals("retired", getObject().isRetired());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("name", getObject().getName());
		assertPropEquals("description", getObject().getDescription());
		assertPropEquals("duration", getObject().getDuration());
        assertPropEquals("confidential", getObject().isConfidential());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Hospitalization";
	}
	
	@Override
	public String getUuidProperty() {
		return "759799ab-c9a5-435e-b671-77373ada74e6";
	}
	
}
