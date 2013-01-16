package org.openmrs.module.appointment.extension.html;

import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.api.AppointmentService;

public class PatientDashboardAppointmentExt extends Extension {
	
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
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientId));
		Appointment appointment = Context.getService(AppointmentService.class).getLastAppointment(patient);
		
		String value = "";
		String action = "";
		
		//Check if there is an ongoing visit
		if (appointment != null && appointment.getVisit().getStopDatetime() == null) {
			value = Context.getMessageSourceService().getMessage("appointment.Appointment.list.button.endConsultation");
			action = "endConsult";
		}
		//No ongoing visit
		else {
			value = Context.getMessageSourceService().getMessage("appointment.Appointment.add");
			action = "scheduleAppointment";
		}
		
		return "<input type=\"button\" value=\"" + value
		        + "\" onclick=\"window.location.href='module/appointment/patientDashboardAppointmentExt.form?patientId="
		        + patientId + "&action=" + action + "'\" />";
	}
}
