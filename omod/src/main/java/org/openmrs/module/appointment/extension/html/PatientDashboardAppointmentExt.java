package org.openmrs.module.appointment.extension.html;

import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.Appointment.AppointmentStatus;
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
		
		//Check if latest appointment is In Consultation
		if (appointment != null && appointment.getStatus() == AppointmentStatus.INCONSULTATION) {
			String value = Context.getMessageSourceService().getMessage(
			    "appointment.Appointment.list.button.endConsultation");
			String action = "endConsult";
			
			return "<input type=\"button\" value=\"" + value
			        + "\" onclick=\"window.location.href='module/appointment/patientDashboardAppointmentExt.form?patientId="
			        + patientId + "&action=" + action + "'\" />";
		}
		//Check if latest appointment is Waiting
		else if (appointment != null && appointment.getStatus() == AppointmentStatus.WAITING) {
			String value = Context.getMessageSourceService().getMessage(
			    "appointment.Appointment.list.button.startConsultation");
			String action = "startConsult";
			
			return "<input type=\"button\" value=\"" + value
			        + "\" onclick=\"window.location.href='module/appointment/patientDashboardAppointmentExt.form?patientId="
			        + patientId + "&action=" + action + "'\" />";
		}
		
		return "";
		
	}
}
