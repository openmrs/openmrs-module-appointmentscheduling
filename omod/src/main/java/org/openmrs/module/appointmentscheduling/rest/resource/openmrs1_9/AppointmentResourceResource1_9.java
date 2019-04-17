package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.BlockExcludedDays;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentresource", supportedClass = AppointmentResource.class,
        supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*"})
public class AppointmentResourceResource1_9 extends DataDelegatingCrudResource<AppointmentResource> {

    @Override
    public AppointmentResource getByUniqueId(String uuid) {
        return Context.getService(AppointmentService.class).getAppointmentResourceByUuid(uuid);
    }

    @Override
    protected void delete(AppointmentResource delegate, String reason, RequestContext context) throws ResponseException {
        if (delegate.isVoided()) {
            return;
        }
        Context.getService(AppointmentService.class).voidAppointmentResource(delegate, reason);
    }

    @Override
    public AppointmentResource newDelegate() {
        return new AppointmentResource();
    }

    @Override
    public AppointmentResource save(AppointmentResource aresource) {
        return Context.getService(AppointmentService.class).saveAppointmentResource(aresource);
    }

    @Override
    public void purge(AppointmentResource delegate, RequestContext context) throws ResponseException {
        if (delegate == null) {
            return;
        }
        Context.getService(AppointmentService.class).purgeAppointmentResource(delegate);

    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addProperty("startTime");
            description.addProperty("endTime");
            description.addProperty("provider", Representation.DEFAULT);
            description.addProperty("location", Representation.REF);
            description.addProperty("types", Representation.REF);
            description.addProperty("daysList", Representation.REF);
            description.addProperty("includeWeekends");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addProperty("startTime");
            description.addProperty("endTime");
            description.addProperty("provider", Representation.DEFAULT);
            description.addProperty("location", Representation.REF);
            description.addProperty("types", Representation.REF);
            description.addProperty("daysList", Representation.DEFAULT);
            description.addProperty("includeWeekends");
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
        description.addRequiredProperty("startTime");
        description.addRequiredProperty("endTime");
        description.addRequiredProperty("location");
        description.addRequiredProperty("types");
        description.addRequiredProperty("daysList");
        description.addProperty("includeWeekends");
        description.addProperty("provider");
        return description;
    }

    @PropertySetter("daysList")
    public static void setDaysList(AppointmentResource instance, List<BlockExcludedDays> blockExcludedDays) {
        instance.setDaysList(blockExcludedDays);
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        return getCreatableProperties();
    }

    @Override
    protected NeedsPaging<AppointmentResource> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<AppointmentResource>(Context.getService(AppointmentService.class).getAllAppointmentResources(
                context.getIncludeAll()), context);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {

        Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
                context.getParameter("location")) : null;

        return new NeedsPaging<AppointmentResource>(Context.getService(AppointmentService.class).getAppointmentResourcesByConstraints(
                location, null, null), context);

    }

    public String getDisplayString(AppointmentResource resource) {
        return resource.getLocation() + ": "
                + resource.getStartTime() + " - " + resource.getEndTime();
    }
}