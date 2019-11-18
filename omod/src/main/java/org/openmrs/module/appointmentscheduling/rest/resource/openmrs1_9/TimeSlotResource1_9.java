package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
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

import java.util.Date;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/timeslot", supportedClass = TimeSlot.class, supportedOpenmrsVersions = {
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*" })
public class TimeSlotResource1_9 extends DataDelegatingCrudResource<TimeSlot> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("appointmentBlock", Representation.DEFAULT);
			description.addProperty("countOfAppointments", findMethod("getCountOfAppointments"));
			description.addProperty("unallocatedMinutes", findMethod("getUnallocatedMinutes"));
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
			description.addProperty("appointmentBlock", Representation.FULL);
			description.addProperty("countOfAppointments", findMethod("getCountOfAppointments"));
			description.addProperty("unallocatedMinutes", findMethod("getUnallocatedMinutes"));
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
		description.addRequiredProperty("appointmentBlock");
		return description;
	}

	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	@Override
	public TimeSlot newDelegate() {
		return new TimeSlot();
	}

	@Override
	public TimeSlot save(TimeSlot timeSlot) {
		return Context.getService(AppointmentService.class).saveTimeSlot(timeSlot);
	}

	@Override
	public TimeSlot getByUniqueId(String uuid) {
		return Context.getService(AppointmentService.class).getTimeSlotByUuid(uuid);
	}

	@Override
	protected void delete(TimeSlot timeSlot, String reason, RequestContext context) throws ResponseException {
		if (timeSlot.isVoided()) {
			return;
		}
		Context.getService(AppointmentService.class).voidTimeSlot(timeSlot, reason);
	}

	@Override
	public void purge(TimeSlot timeSlot, RequestContext requestContext) throws ResponseException {
		if (timeSlot == null) {
			return;
		}
		Context.getService(AppointmentService.class).purgeTimeSlot(timeSlot);
	}

	@Override
	protected NeedsPaging<TimeSlot> doGetAll(RequestContext context) {
		return new NeedsPaging<TimeSlot>(Context.getService(AppointmentService.class).getAllTimeSlots(
		    context.getIncludeAll()), context);
	}

	/**
	 * Return a list of time slots that fall within the given constraints.
	 *
	 * @param appointmentType - Type of the appointment this slot must support
	 * @param fromDate - (optional) earliest start date.
	 * @param toDate - (optional) latest start date.
	 * @param provider - (optional) the time slots's provider.
	 * @param location - (optional) the time slots's location. (or predecessor location)
	 * @param includeFull - (optional, default false) include time slots that are already fully
	 *            booked
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {

		Date startDate = context.getParameter("fromDate") != null ? (Date) ConversionUtil.convert(
		    context.getParameter("fromDate"), Date.class) : null;

		Date endDate = context.getParameter("toDate") != null ? (Date) ConversionUtil.convert(
		    context.getParameter("toDate"), Date.class) : null;

		AppointmentType appointmentType = context.getParameter("appointmentType") != null ? Context.getService(
		    AppointmentService.class).getAppointmentTypeByUuid(context.getParameter("appointmentType")) : null;

		Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
		    context.getParameter("provider")) : null;

		Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
		    context.getParameter("location")) : null;

		Boolean includeFull = context.getParameter("includeFull") != null ? (Boolean) ConversionUtil.convert(
		    context.getParameter("includeFull"), Boolean.class) : false;

		Patient patient = context.getParameter("excludeTimeSlotsPatientAlreadyBookedFor") != null ? Context
		        .getPatientService().getPatientByUuid(context.getParameter("excludeTimeSlotsPatientAlreadyBookedFor"))
		        : null;

		if (includeFull) {
			return new NeedsPaging<TimeSlot>(
			        Context.getService(AppointmentService.class).getTimeSlotsByConstraintsIncludingFull(appointmentType,
                            startDate, endDate, provider, location, patient), context);
		} else {
			return new NeedsPaging<TimeSlot>(Context.getService(AppointmentService.class).getTimeSlotsByConstraints(
			    appointmentType, startDate, endDate, provider, location, patient), context);
		}

	}

	public String getDisplayString(TimeSlot timeSlot) {
		return timeSlot.getAppointmentBlock().getProvider() + ", " + timeSlot.getAppointmentBlock().getLocation() + ": "
		        + timeSlot.getStartDate() + " - " + timeSlot.getEndDate();
	}

	public Integer getCountOfAppointments(TimeSlot timeSlot) {
		return Context.getService(AppointmentService.class).getCountOfAppointmentsInTimeSlotThatAreNotCancelled(timeSlot);
	}

	public Integer getUnallocatedMinutes(TimeSlot timeSlot) {
		return Context.getService(AppointmentService.class).getTimeLeftInTimeSlot(timeSlot);
	}

}
