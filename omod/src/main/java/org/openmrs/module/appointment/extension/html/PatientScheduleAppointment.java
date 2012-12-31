package org.openmrs.module.appointment.extension.html;

import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;

public class PatientScheduleAppointment extends Extension {
	
	private String patientId = "";
	
	@Override
	public void initialize(final Map<String, String> parameters) {
		patientId = parameters.get("patientId");
	}
	
	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}
	
	@Override
	public String getOverrideContent(String bodyContent) {
		String message = Context.getMessageSourceService().getMessage("appointment.Appointment.add");
		
		return "<a href='module/appointment/appointmentForm.form?patient=" + patientId + "' >" + message + "</a>";
	}
}
