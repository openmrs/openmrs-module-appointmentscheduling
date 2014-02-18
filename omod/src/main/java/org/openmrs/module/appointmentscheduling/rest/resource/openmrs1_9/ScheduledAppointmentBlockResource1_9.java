package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.ScheduledAppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
        + "/scheduleAppointmentBlock", supportedClass = ScheduledAppointmentBlock.class, supportedOpenmrsVersions = "1.9.*")
public class ScheduledAppointmentBlockResource1_9 implements Searchable {
	
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {

        Date startDate = getDate(context);
        Location location = getLocation(context);

        List<ScheduledAppointmentBlock> dailyAppointmentBlocks = getScheduledAppointmentBlocks(startDate, location);

        SimpleObject result = new SimpleObject();
		result.add("result", convertToSimpleObjectList(dailyAppointmentBlocks));
		
		return result;
	}

    private List<ScheduledAppointmentBlock> getScheduledAppointmentBlocks(Date startDate, Location location) {
        return Context.getService(AppointmentService.class)
                    .getDailyAppointmentBlocks(location, startDate);
    }

    private Location getLocation(RequestContext context) {
        return context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
                context.getParameter("location")) : null;
    }

    private Date getDate(RequestContext context) {
        return context.getParameter("fromDate") != null ? (Date) ConversionUtil.convert(
                context.getParameter("fromDate"), Date.class) : null;
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
        simpleObject.add("appointmentBlock", convertProperty(dailyAppointmentBlock, "appointmentBlock"));
        simpleObject.add("appointments", convertProperty(dailyAppointmentBlock, "appointments"));
        return simpleObject;
    }

    private Object convertProperty(ScheduledAppointmentBlock dailyAppointmentBlock, String appointmentBlock) {
        return ConversionUtil.getPropertyWithRepresentation(dailyAppointmentBlock,
                appointmentBlock, Representation.DEFAULT);
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
}
