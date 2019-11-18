package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentStatusHistory;
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

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentstatushistory",
        supportedClass = AppointmentStatusHistory.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentStatusHistoryResource1_9 extends DataDelegatingCrudResource<AppointmentStatusHistory> {
    @Override
    public AppointmentStatusHistory getByUniqueId(String s) {
        return null;
    }

    @Override
    protected void delete(AppointmentStatusHistory appointmentStatusHistory, String s, RequestContext requestContext) throws ResponseException {

    }

    @Override
    public AppointmentStatusHistory newDelegate() {
        return null;
    }

    @Override
    public AppointmentStatusHistory save(AppointmentStatusHistory appointmentStatusHistory) {
        return null;
    }

    @Override
    public void purge(AppointmentStatusHistory appointmentStatusHistory, RequestContext requestContext) throws ResponseException {

    }
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("appointment", Representation.DEFAULT);
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("status");
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("appointment", Representation.DEFAULT);
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("status");
            description.addProperty("startDate");
            description.addProperty("endDate");
            description.addSelfLink();
            return description;
        }
        return null;
    }

    @Override
    protected NeedsPaging<AppointmentStatusHistory> doGetAll(RequestContext context) {
        return new NeedsPaging<AppointmentStatusHistory>(Context.getService(AppointmentService.class).getAllAppointmentStatusHistories(), context);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {

        Appointment appointment = context.getParameter("appointment") != null ? Context.getService(AppointmentService.class)
                .getAppointmentByUuid(context.getParameter("appointment")) : null;
        return new NeedsPaging<AppointmentStatusHistory>(Context.getService(AppointmentService.class).getAppointmentStatusHistories(appointment), context);
    }

    public String getDisplayString(AppointmentStatusHistory statusHistory) {
        return statusHistory.getStartDate() + "  "+statusHistory.getEndDate();
    }

}
