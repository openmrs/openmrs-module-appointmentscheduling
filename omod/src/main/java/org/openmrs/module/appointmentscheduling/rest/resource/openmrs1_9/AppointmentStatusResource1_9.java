package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

@Resource(name = RestConstants.VERSION_1
		+ AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
		+ "/appointmentstatus", supportedClass = Appointment.AppointmentStatus.class,
		supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class AppointmentStatusResource1_9 implements Listable, Searchable , Converter {

	@Override
	public SimpleObject getAll(RequestContext requestContext)
			throws ResponseException {


		List<SimpleObject> statusList = new ArrayList<SimpleObject>();

		for (AppointmentStatus appointmentStatus : AppointmentStatus.values()) {
			statusList.add(asRepresentation(appointmentStatus, null));
		}

		SimpleObject simpleObject = new SimpleObject().add("results",
				statusList);
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

	@Override
	public Object newInstance(String type) {
		return null;  // not relevant for enum?
	}

	@Override
	public Object getByUniqueId(String id) {
		return AppointmentStatus.valueOf(id);
	}

	@Override
	public SimpleObject asRepresentation(Object instance, Representation rep) throws ConversionException {
		AppointmentStatus status = (AppointmentStatus) instance;
		SimpleObject simpleObject = new SimpleObject();
		simpleObject.add("name", status.getName());
		simpleObject.add("type", status.getType().toString());
		simpleObject.add("code", status.name());
		return simpleObject;
	}

	@Override
	public Object getProperty(Object instance, String propertyName) throws ConversionException {
		return null; // not relevant for enum
	}

	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		// not relevant for enum
	}
}
