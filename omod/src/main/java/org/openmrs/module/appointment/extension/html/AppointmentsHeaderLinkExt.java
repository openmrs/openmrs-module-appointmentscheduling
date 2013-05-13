package org.openmrs.module.appointment.extension.html;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentUtils;
import org.openmrs.module.web.extension.LinkExt;

public class AppointmentsHeaderLinkExt extends LinkExt {
	
	@Override
	public String getLabel() {
		return Context.getMessageSourceService().getMessage("appointment.header.link");
	}
	
	@Override
	public String getRequiredPrivilege() {
		return AppointmentUtils.PRIV_VIEW_APPOINTMENTS;
	}
	
	@Override
	public String getUrl() {
		return "module/appointment/appointmentList.list";
	}
	
}
