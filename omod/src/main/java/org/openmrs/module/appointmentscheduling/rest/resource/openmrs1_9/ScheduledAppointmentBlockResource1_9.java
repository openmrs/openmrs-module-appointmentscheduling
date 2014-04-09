package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.ScheduledAppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9.util.AppointmentRestUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.validation.ValidationException;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
        + "/scheduledappointmentblocks", supportedClass = ScheduledAppointmentBlock.class, supportedOpenmrsVersions = "1.9.*")
public class ScheduledAppointmentBlockResource1_9 implements Searchable, Retrievable {
	
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		
		Date date = getDate(context);
		Location location = getLocation(context);
		List<AppointmentType> appointmentTypes = AppointmentRestUtils.getAppointmentTypes(context);
		
		if (date == null || location == null) {
			throw new ValidationException("appointmentscheduling.AppointmentBlock.error.scheduledAppointmentBlocks");
		}
		
		List<ScheduledAppointmentBlock> dailyAppointmentBlocks = getScheduledAppointmentBlocks(date, location,
		    appointmentTypes);
		
		SimpleObject result = new SimpleObject();
		result.add("results", convertToSimpleObjectList(dailyAppointmentBlocks));
		
		return result;
	}
	
	private List<ScheduledAppointmentBlock> getScheduledAppointmentBlocks(Date startDate, Location location,
	        List<AppointmentType> appointmentTypes) {
		return Context.getService(AppointmentService.class).getDailyAppointmentBlocks(location, startDate, appointmentTypes);
	}
	
	private Location getLocation(RequestContext context) {
		return context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
		    context.getParameter("location")) : null;
	}
	
	private Date getDate(RequestContext context) {
		return context.getParameter("date") != null ? (Date) ConversionUtil
		        .convert(context.getParameter("date"), Date.class) : null;
	}
	
	private List<SimpleObject> convertToSimpleObjectList(List<ScheduledAppointmentBlock> dailyAppointmentBlocks) {
		List<SimpleObject> simpleObjects = new ArrayList<SimpleObject>();
		
		for (ScheduledAppointmentBlock dailyAppointmentBlock : dailyAppointmentBlocks) {
			simpleObjects.add(getSimpleObject(dailyAppointmentBlock));
		}
		
		return simpleObjects;
	}
	
	private SimpleObject getSimpleObject(ScheduledAppointmentBlock dailyAppointmentBlock) {
		SimpleObject simpleObject = new SimpleObject();
		simpleObject.add("appointmentBlock", getSimpleAppointmentBlock(dailyAppointmentBlock));
		simpleObject.add("appointments",getSimpleAppointments(dailyAppointmentBlock));
		return simpleObject;
	}

    private Object getSimpleAppointmentBlock(ScheduledAppointmentBlock scheduledAppointmentBlock) {
        return ConversionUtil.getPropertyWithRepresentation(scheduledAppointmentBlock, "appointmentBlock", Representation.DEFAULT);
    }

	private List<SimpleObject> getSimpleAppointments(ScheduledAppointmentBlock scheduledAppointmentBlock) {

        List<SimpleObject> simpleAppointments = new ArrayList<SimpleObject>();

        // we basically made are our custom representation here, because we want only the ref version of the time slot
        // (because we already have the overall appointment block, which would be the save for every appointment),
        // but we do want the default versions of the appointment type and patient
        for (Appointment appointment : scheduledAppointmentBlock.getAppointments()) {
            SimpleObject simpleAppointment = new SimpleObject();

            simpleAppointment.add("timeSlot", ConversionUtil.getPropertyWithRepresentation(appointment, "timeSlot", Representation.REF));
            simpleAppointment.add("appointmentType", ConversionUtil.getPropertyWithRepresentation(appointment, "appointmentType", Representation.DEFAULT));
            simpleAppointment.add("patient", ConversionUtil.getPropertyWithRepresentation(appointment, "patient", Representation.DEFAULT));
            simpleAppointment.add("status", ConversionUtil.getPropertyWithRepresentation(appointment, "status", Representation.DEFAULT));
            simpleAppointment.add("reason", ConversionUtil.getPropertyWithRepresentation(appointment, "reason", Representation.DEFAULT));
            simpleAppointment.add("cancelReason", ConversionUtil.getPropertyWithRepresentation(appointment, "cancelReason", Representation.DEFAULT));
            // note that we don't return the visit because we don't care about it at this point

            simpleAppointments.add(simpleAppointment);
        }

       return simpleAppointments;
    }

	
	@Override
	public String getUri(Object delegate) {
		
		return RestConstants.URI_PREFIX + "/appointmentscheduling/scheduledappointmentblock/startDate=?";
	}
	
	private String getUniqueId(ScheduledAppointmentBlock delegate) {
		try {
			return (String) PropertyUtils.getProperty(delegate, "uuid");
		}
		catch (Exception ex) {
			throw new RuntimeException("Cannot find String uuid property on " + delegate.getClass(), null);
		}
	}
	
	@Override
	public Object retrieve(String uuid, RequestContext context) throws ResponseException {
		return null; //To change body of implemented methods use File | Settings | File Templates.
	}
	
	@Override
	public List<Representation> getAvailableRepresentations() {
		return Arrays.asList(Representation.DEFAULT, Representation.FULL, Representation.REF);
	}
}
