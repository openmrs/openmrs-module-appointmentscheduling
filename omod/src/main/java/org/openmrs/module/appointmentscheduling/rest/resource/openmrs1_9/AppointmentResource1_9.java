package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;
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
import org.openmrs.module.webservices.validation.ValidationException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointment", supportedClass = Appointment.class, supportedOpenmrsVersions = "1.9.*")
public class AppointmentResource1_9 extends DataDelegatingCrudResource<Appointment> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		if (representation instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("timeSlot", Representation.DEFAULT);
			description.addProperty("visit", Representation.REF);
			description.addProperty("patient", Representation.DEFAULT);
			description.addProperty("status");
			description.addProperty("reason");
			description.addProperty("appointmentType", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (representation instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("timeSlot", Representation.FULL);
			description.addProperty("visit", Representation.FULL);
			description.addProperty("patient", Representation.FULL);
			description.addProperty("status");
			description.addProperty("reason");
			description.addProperty("appointmentType", Representation.FULL);
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
		description.addRequiredProperty("timeSlot");
		description.addProperty("visit");
		description.addRequiredProperty("patient");
		description.addRequiredProperty("status");
		description.addProperty("reason");
		description.addRequiredProperty("appointmentType");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		// note that time slot and appointment type are not updateable
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("visit");
		description.addRequiredProperty("status");
		description.addProperty("reason");
		return description;
	}
	
	@Override
	public Appointment getByUniqueId(String uuid) {
		return Context.getService(AppointmentService.class).getAppointmentByUuid(uuid);
	}
	
	@Override
	protected void delete(Appointment appointment, String reason, RequestContext requestContext) throws ResponseException {
		if (appointment.isVoided()) {
			return;
		}
		Context.getService(AppointmentService.class).voidAppointment(appointment, reason);
	}
	
	@Override
	public Appointment newDelegate() {
		return new Appointment();
	}
	
	@Override
	public Appointment save(Appointment appointment) {
		return save(appointment, false);
	}
	
	protected Appointment save(Appointment appointment, Boolean allowOverbook) {
		if (appointment.getId() != null) {
			// existing appointments get updated
			return Context.getService(AppointmentService.class).saveAppointment(appointment);
		} else {
			// new appointments get booked
			try {
				return Context.getService(AppointmentService.class).bookAppointment(appointment, allowOverbook);
			}
			catch (TimeSlotFullException e) {
				Errors errors = new BindException(appointment, "");
				errors.reject("appointmentscheduling.Appointment.error.timeSlotFull");
				throw new ValidationException("appointmentscheduling.Appointment.error.timeSlotFull", errors);
			}
		}
	}
	
	@Override
	public void purge(Appointment appointment, RequestContext requestContext) throws ResponseException {
		if (appointment == null) {
			return;
		}
		Context.getService(AppointmentService.class).purgeAppointment(appointment);
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		AppointmentService service = Context.getService(AppointmentService.class);
		return new NeedsPaging<Appointment>(service.getAllAppointments(), context);
	}
	
	/**
	 * Retrieves Appointments that satisfy the given constraints
	 * 
	 * @param fromDate (optional) - The appointment start date
	 * @param toDate (optional) - The appointment end date
	 * @param location (optional) - The appointment location
	 * @param provider (optional) - The appointment provider
	 * @param appointmentType (optional) - The appointment type
	 * @param status (optional) - The appointment status
	 * @param patient (optional) - The patient
	 * @return a list of appointments that satisfy the given constraints
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		
		Date fromDate = context.getParameter("fromDate") != null ? (Date) ConversionUtil.convert(
		    context.getParameter("fromDate"), Date.class) : null;
		
		Date toDate = context.getParameter("toDate") != null ? (Date) ConversionUtil.convert(context.getParameter("toDate"),
		    Date.class) : null;
		
		AppointmentType appointmentType = context.getParameter("appointmentType") != null ? Context.getService(
		    AppointmentService.class).getAppointmentTypeByUuid(context.getParameter("appointmentType")) : null;
		
		Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
		    context.getParameter("provider")) : null;
		
		Patient patient = context.getParameter("patient") != null ? Context.getPatientService().getPatientByUuid(
		    context.getParameter("patient")) : null;
		
		Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
		    context.getParameter("location")) : null;
		
		Appointment.AppointmentStatus status = context.getParameter("status") != null ? Appointment.AppointmentStatus
		        .valueOf(context.getParameter("status")) : null;
		
		return new NeedsPaging<Appointment>(Context.getService(AppointmentService.class).getAppointmentsByConstraints(
		    fromDate, toDate, location, provider, appointmentType, status, patient), context);
		
	}
	
	public String getDisplayString(Appointment appointment) {
		return appointment.getAppointmentType().getName() + " : " + appointment.getStatus();
	}
}
