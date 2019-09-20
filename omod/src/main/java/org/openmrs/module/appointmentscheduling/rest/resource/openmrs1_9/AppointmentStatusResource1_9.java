package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

@Resource(name = RestConstants.VERSION_1
		+ AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
		+ "/appointmentstatus", supportedClass = AppointmentStatusResource1_9.class,
		supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentStatusResource1_9 implements Listable, Searchable {

	@Override
	public SimpleObject getAll(RequestContext requestContext)
			throws ResponseException {
		AppointmentStatus[] appointmentStatus = AppointmentStatus
				.values();
		SimpleObject simpleObject = new SimpleObject().add("results",
				appointmentStatus);
		return simpleObject;
	}

	@Override
	public String getUri(Object o) {
		return RestConstants.URI_PREFIX
				+ "/appointmentscheduling/appointmentstatus/"
				+ ((AppointmentStatus) o).getName();
	}

	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}
}
