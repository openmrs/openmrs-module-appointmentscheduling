package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.VisitType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentreporting",
        supportedClass = Appointment.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*"})
public class DailyAppointmentCountsResource1_9 extends DataDelegatingCrudResource<Appointment> {

    @Override
    public Appointment getByUniqueId(String uniqueId) {
        return null;
    }

    @Override
    protected void delete(Appointment delegate, String reason, RequestContext context) throws ResponseException {

    }

    @Override
    public Appointment newDelegate() {
        return null;
    }

    @Override
    public Appointment save(Appointment delegate) {
        return null;
    }

    @Override
    public void purge(Appointment delegate, RequestContext context) throws ResponseException {

    }

    @Override
    protected PageableResult doGetAll(RequestContext context) throws ResponseException {
        AppointmentService service = Context.getService(AppointmentService.class);
        return new NeedsPaging<Appointment>(service.getAllAppointments(context.getIncludeAll()), context);
    }

    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
        if (representation instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("dailyCount", findMethod("getDairlyCount"));
            description.addProperty("appointmentDate", findMethod("getAppointmentDate"));
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (representation instanceof FullRepresentation || representation instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("dailyCount", findMethod("getDairlyCount"));
            description.addProperty("appointmentDate", findMethod("getAppointmentDate"));
            description.addSelfLink();
            return description;
        }

        return null;
    }

    @Override
    protected PageableResult doSearch(RequestContext context) {
        String status = context.getRequest().getParameter("status");

        AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status);

        Date fromDate = context.getParameter("fromDate") != null ? (Date) ConversionUtil.convert(
                context.getParameter("fromDate"), Date.class) : null;

        Date toDate = context.getParameter("toDate") != null ? (Date) ConversionUtil.convert(context.getParameter("toDate"),
                Date.class) : null;

        AppointmentType appointmentType = context.getParameter("appointmentType") != null ? Context.getService(
                AppointmentService.class).getAppointmentTypeByUuid(context.getParameter("appointmentType")) : null;

        Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
                context.getParameter("provider")) : null;

        Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
                context.getParameter("location")) : null;

        VisitType visitType = context.getParameter("visitType") != null ? Context.getVisitService().getVisitTypeByUuid(
                context.getParameter("visitType")) : null;

        return new NeedsPaging<Appointment>(Context.getService(AppointmentService.class).getDistinctDailyAppointment(
                fromDate, toDate, location, provider, appointmentType,  appointmentStatus, visitType), context);

    }

    public Integer getDairlyCount(Appointment appointment) {
        return Context.getService(AppointmentService.class).getDailyAppointmentsCount(appointment);
    }

    public String getAppointmentDate(Appointment appointment){
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(appointment.getTimeSlot().getStartDate());
    }

}