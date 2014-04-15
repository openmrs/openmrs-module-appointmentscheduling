package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.junit.Before;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class TimeSlotResource1_9Test extends BaseDelegatingResourceTest<TimeSlotResource1_9, TimeSlot> {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("standardWebAppointmentTestDataset.xml");
	}
	
	@Override
	public TimeSlot newObject() {
		return Context.getService(AppointmentService.class).getTimeSlotByUuid(getUuidProperty());
	}
	
	@Override
	public void validateRefRepresentation() throws Exception {
		super.validateRefRepresentation();
		assertPropEquals("voided", getObject().isVoided()); // note that the voided property is only present if the property is voided
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("startDate", getObject().getStartDate());
		assertPropEquals("endDate", getObject().getEndDate());
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("appointmentBlock");
		assertPropPresent("countOfAppointments");
		assertPropPresent("unallocatedMinutes");
		assertPropNotPresent("auditInfo");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("startDate", getObject().getStartDate());
		assertPropEquals("endDate", getObject().getEndDate());
		assertPropEquals("voided", getObject().isVoided());
		assertPropPresent("appointmentBlock");
		assertPropPresent("countOfAppointments");
		assertPropPresent("unallocatedMinutes");
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Hippocrates of Cos, Xanadu: 2007-01-01 00:00:00.2 - 2007-01-01 01:00:00.0";
	}
	
	@Override
	public String getUuidProperty() {
		return "c0c579b0-8e59-401d-8a4a-976a0b183606";
	}
	
}
