package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class AppointmentBlockWithTimeSlotResource1_9Test extends BaseDelegatingResourceTest<AppointmentBlockWithTimeSlotResource1_9, AppointmentBlock> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Override
	public AppointmentBlock newObject() {
		return Context.getService(AppointmentService.class).getAppointmentBlockByUuid(getUuidProperty());
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
		assertPropEquals("voided", getObject().isVoided());
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
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("provider");
		assertPropPresent("location");
		assertPropPresent("types");
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Mr. Horatio Test Hornblower Esq., Xanadu: 2005-01-03 00:00:00.0 - 2005-01-03 11:00:00.0";
	}
	
	@Override
	public String getUuidProperty() {
		return "759799ab-c9a5-435e-b671-77773ada7499";
	}
	
}
