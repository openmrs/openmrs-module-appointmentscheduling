package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9.util;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.webservices.rest.web.RequestContext;

public class AppointmentRestUtils {
	
	public static List<AppointmentType> getAppointmentTypes(RequestContext context) {
		
		List<AppointmentType> types = null;
		String[] appointmentTypes = context.getRequest().getParameterValues("appointmentType");
		if (appointmentTypes != null && appointmentTypes.length > 0) {
			types = new ArrayList<AppointmentType>();
			for (String appointmentType : appointmentTypes) {
				types.add(Context.getService(AppointmentService.class).getAppointmentTypeByUuid(appointmentType));
			}
		}
		
		return types;
	}
	
}
