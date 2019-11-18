package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmenttype", supportedClass = AppointmentType.class,
    supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentTypeResource1_9 extends MetadataDelegatingCrudResource<AppointmentType> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("duration");
			description.addProperty("confidential");
			description.addProperty("visitType", Representation.DEFAULT);
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("duration");
            description.addProperty("confidential");
            description.addProperty("visitType", Representation.DEFAULT);
			description.addProperty("retired");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		description.addRequiredProperty("duration");
        description.addProperty("confidential");
        description.addProperty("visitType");
		return description;
	}

	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	@Override
	public AppointmentType newDelegate() {
		return new AppointmentType();
	}

	@Override
	public AppointmentType save(AppointmentType appointmentType) {
		return Context.getService(AppointmentService.class).saveAppointmentType(appointmentType);
	}

	@Override
	public AppointmentType getByUniqueId(String uuid) {
		return Context.getService(AppointmentService.class).getAppointmentTypeByUuid(uuid);
	}

	@Override
	public void purge(AppointmentType appointmentType, RequestContext requestContext) throws ResponseException {
		if (appointmentType == null) {
			return;
		}
		Context.getService(AppointmentService.class).purgeAppointmentType(appointmentType);
	}

	@Override
	protected NeedsPaging<AppointmentType> doGetAll(RequestContext context) {
		return new NeedsPaging<AppointmentType>(Context.getService(AppointmentService.class).getAllAppointmentTypesSorted(
		    context.getIncludeAll()), context);
	}

	@Override
	protected PageableResult doSearch(RequestContext context) {
		return new NeedsPaging<AppointmentType>(Context.getService(AppointmentService.class).getAppointmentTypes(
		    context.getParameter("q"), context.getIncludeAll()), context);
	}

}
