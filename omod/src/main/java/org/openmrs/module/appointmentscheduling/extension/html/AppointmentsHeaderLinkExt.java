package org.openmrs.module.appointmentscheduling.extension.html;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.web.extension.LinkExt;

public class AppointmentsHeaderLinkExt extends LinkExt {
	
	@Override
	public String getLabel() {
		return Context.getMessageSourceService().getMessage("appointmentscheduling.header.link");
	}
	
	@Override
	public String getRequiredPrivilege() {
		return AppointmentUtils.PRIV_VIEW_APPOINTMENTS;
	}
	
	@Override
	public String getUrl() {
		return "module/appointmentscheduling/appointmentList.list";
	}
	
}
