package org.openmrs.module.appointmentscheduling.extension.html;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.web.extension.PatientDashboardTabExt;

public class PatientDashboardAppointmentTabExt extends PatientDashboardTabExt {
	
	@Override
	public String getPortletUrl() {
		return "appointments.portlet";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return AppointmentUtils.PRIV_VIEW_APPOINTMENT_HISTORY_TAB;
	}
	
	@Override
	public String getTabId() {
		return "appointmentscheduling.title";
	}
	
	@Override
	public String getTabName() {
		return Context.getMessageSourceService().getMessage("appointmentscheduling.appointmentsTab");
	}
	
}
