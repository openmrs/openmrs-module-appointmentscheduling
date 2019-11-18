package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentRequest;
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
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentrequest", supportedClass = AppointmentRequest.class,
    supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentRequestResource1_9 extends DataDelegatingCrudResource<AppointmentRequest> {

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        if (representation instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("patient", Representation.DEFAULT);
            description.addProperty("appointmentType", Representation.REF);
            description.addProperty("provider", Representation.DEFAULT);
            description.addProperty("requestedBy", Representation.DEFAULT);
            description.addProperty("requestedOn");
            description.addProperty("status");
            description.addProperty("minTimeFrameValue");
            description.addProperty("minTimeFrameUnits");
            description.addProperty("maxTimeFrameValue");
            description.addProperty("maxTimeFrameUnits");
            description.addProperty("notes");
            description.addProperty("voided");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (representation instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display", findMethod("getDisplayString"));
            description.addProperty("patient", Representation.FULL);
            description.addProperty("appointmentType", Representation.FULL);
            description.addProperty("provider", Representation.FULL);
            description.addProperty("requestedBy", Representation.FULL);
            description.addProperty("requestedOn");
            description.addProperty("status");
            description.addProperty("minTimeFrameValue");
            description.addProperty("minTimeFrameUnits");
            description.addProperty("maxTimeFrameValue");
            description.addProperty("maxTimeFrameUnits");
            description.addProperty("notes");
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
        description.addRequiredProperty("patient");
        description.addRequiredProperty("appointmentType");
        description.addProperty("provider");
        description.addProperty("requestedBy");
        description.addRequiredProperty("requestedOn");
        description.addRequiredProperty("status");
        description.addProperty("minTimeFrameValue");
        description.addProperty("minTimeFrameUnits");
        description.addProperty("maxTimeFrameValue");
        description.addProperty("maxTimeFrameUnits");
        description.addProperty("notes");
        return description;
    }

    @Override
    public DelegatingResourceDescription getUpdatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("patient");
        description.addRequiredProperty("appointmentType");
        description.addProperty("provider");
        description.addProperty("requestedBy");
        description.addRequiredProperty("requestedOn");
        description.addRequiredProperty("status");
        description.addProperty("minTimeFrameValue");
        description.addProperty("minTimeFrameUnits");
        description.addProperty("maxTimeFrameValue");
        description.addProperty("maxTimeFrameUnits");
        description.addProperty("notes");
        return description;
    }

    @Override
    public AppointmentRequest getByUniqueId(String uuid) {
        return Context.getService(AppointmentService.class).getAppointmentRequestByUuid(uuid);
    }

    @Override
    protected void delete(AppointmentRequest appointmentRequest, String reason, RequestContext requestContext) throws ResponseException {
        if (appointmentRequest.isVoided()) {
            return;
        }
        Context.getService(AppointmentService.class).voidAppointmentRequest(appointmentRequest, reason);
    }

    @Override
    public AppointmentRequest newDelegate() {
        return new AppointmentRequest();
    }

    @Override
    public AppointmentRequest save(AppointmentRequest appointmentRequest) {
        return Context.getService(AppointmentService.class).saveAppointmentRequest(appointmentRequest);
    }

    @Override
    public void purge(AppointmentRequest appointmentRequest, RequestContext requestContext) throws ResponseException {
        if (appointmentRequest == null) {
            return;
        }
        Context.getService(AppointmentService.class).purgeAppointmentRequest(appointmentRequest);
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {

        AppointmentType appointmentType = context.getParameter("appointmentType") != null ? Context.getService(
                AppointmentService.class).getAppointmentTypeByUuid(context.getParameter("appointmentType")) : null;

        Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
                context.getParameter("provider")) : null;

        Patient patient = context.getParameter("patient") != null ? Context.getPatientService().getPatientByUuid(
                context.getParameter("patient")) : null;

        AppointmentRequest.AppointmentRequestStatus status =  context.getParameter("status") != null  ?
                AppointmentRequest.AppointmentRequestStatus.valueOf(context.getParameter("status").toUpperCase())
                : null;

        return new NeedsPaging<AppointmentRequest>(Context.getService(AppointmentService.class).getAppointmentRequestsByConstraints(
                patient, appointmentType, provider, status), context);

    }

    @Override
    protected NeedsPaging<AppointmentRequest> doGetAll(RequestContext context) {
        return new NeedsPaging<AppointmentRequest>(Context.getService(AppointmentService.class).getAllAppointmentRequests(
                context.getIncludeAll()), context);
    }

    public String getDisplayString(AppointmentRequest appointmentRequest) {
        return appointmentRequest.getAppointmentType().getName() + " : " + appointmentRequest.getStatus();
    }
}
