package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_REST_NAMESPACE + "/appointmentblock", supportedClass = AppointmentBlock.class, supportedOpenmrsVersions = "1.9.*")
public class AppointmentBlockResource1_9 extends DataDelegatingCrudResource<AppointmentBlock> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("provider", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("types", Representation.DEFAULT);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("provider", Representation.FULL);
			description.addProperty("location", Representation.FULL);
			description.addProperty("types", Representation.FULL);
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("startDate");
		description.addRequiredProperty("endDate");
		description.addRequiredProperty("provider");
		description.addRequiredProperty("location");
		description.addRequiredProperty("types");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}
	
	@Override
	public AppointmentBlock newDelegate() {
		return new AppointmentBlock();
	}
	
	@Override
	public AppointmentBlock save(AppointmentBlock appointmentBlock) {
		return Context.getService(AppointmentService.class).saveAppointmentBlock(appointmentBlock);
	}
	
	@Override
	public AppointmentBlock getByUniqueId(String uuid) {
		return Context.getService(AppointmentService.class).getAppointmentBlockByUuid(uuid);
	}
	
	@Override
	protected void delete(AppointmentBlock appointmentBlock, String reason, RequestContext context) throws ResponseException {
		if (appointmentBlock.isVoided()) {
			return;
		}
		Context.getService(AppointmentService.class).voidAppointmentBlock(appointmentBlock, reason);
	}
	
	@Override
	public void purge(AppointmentBlock appointmentBlock, RequestContext requestContext) throws ResponseException {
		if (appointmentBlock == null) {
			return;
		}
		Context.getService(AppointmentService.class).purgeAppointmentBlock(appointmentBlock);
	}
	
	@Override
	protected NeedsPaging<AppointmentBlock> doGetAll(RequestContext context) {
		return new NeedsPaging<AppointmentBlock>(Context.getService(AppointmentService.class).getAllAppointmentBlocks(
		    context.getIncludeAll()), context);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		//return new NeedsPaging<AppointmentType>(Context.getService(AppointmentService.class).getAppointmentTypes(
		// context.getParameter("q")), context);
		return null;
	}
	
	public String getDisplayString(AppointmentBlock appointmentBlock) {
		return appointmentBlock.getProvider() + ", " + appointmentBlock.getLocation() + ": "
		        + appointmentBlock.getStartDate() + " - " + appointmentBlock.getEndDate();
	}
	
}
