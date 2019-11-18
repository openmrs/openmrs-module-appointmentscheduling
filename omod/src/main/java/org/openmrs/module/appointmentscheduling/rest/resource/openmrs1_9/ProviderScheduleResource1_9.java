package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.ProviderSchedule;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9.util.AppointmentRestUtils;
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
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/providerschedule", supportedClass = ProviderSchedule.class,
        supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class ProviderScheduleResource1_9 extends DataDelegatingCrudResource<ProviderSchedule> {

    @Override
    public ProviderSchedule getByUniqueId(String uuid) {
        return Context.getService(AppointmentService.class).getProviderScheduleByUuid(uuid);
    }

    @Override
    protected void delete(ProviderSchedule delegate, String reason, RequestContext context) throws ResponseException {
        if (delegate.isVoided()) {
            return;
        }
        Context.getService(AppointmentService.class).voidProviderSchedule(delegate, reason);
    }

    @Override
    public ProviderSchedule newDelegate() {
        return new ProviderSchedule();
    }

    @Override
    public ProviderSchedule save(ProviderSchedule schedule) {
        return Context.getService(AppointmentService.class).saveProviderSchedule(schedule);
    }

    @Override
    public void purge(ProviderSchedule delegate, RequestContext context) throws ResponseException {
        if (delegate == null) {
            return;
        }
        Context.getService(AppointmentService.class).purgeProviderSchedule(delegate);

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
        description.addProperty("provider");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        return getCreatableProperties();
    }

    @Override
    protected NeedsPaging<ProviderSchedule> doGetAll(RequestContext context) throws ResponseException {
        return new NeedsPaging<ProviderSchedule>(Context.getService(AppointmentService.class).getAllProviderSchedules(
                context.getIncludeAll()), context);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {

        Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
                context.getParameter("location")) : null;
        Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
                context.getParameter("provider")) : null;
        List<AppointmentType> types = AppointmentRestUtils.getAppointmentTypes(context);

        return new NeedsPaging<ProviderSchedule>(Context.getService(AppointmentService.class).getProviderSchedulesByConstraints(
                location, provider, types), context);

    }

    public String getDisplayString(ProviderSchedule schedule) {
        return schedule.getLocation() + ": "
                + schedule.getStartTime() + " - " + schedule.getEndTime();
    }
}
