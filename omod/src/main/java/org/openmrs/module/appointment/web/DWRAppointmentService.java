package org.openmrs.module.appointment.web;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.api.AppointmentService;

/**
 * DWR patient methods. The methods in here are used in the webapp to get data from the database via
 * javascript calls.
 * 
 * @see PatientService
 */
public class DWRAppointmentService {
	
	public PatientDescription getPatientDescription(Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null)
			return null;
		
		PatientDescription patientDescription = new PatientDescription("test", "test");
		/*patientDescription
		        .setPhoneNumber(patient.getAttribute(
		            Context.getAdministrationService().getGlobalProperty("appointment.phoneNumberPersonAttributeTypeId"))
		                .getValue());
		Date missedLastAppointment = Context.getService(AppointmentService.class).getMissedLastAppointment(patient);
		if (missedLastAppointment != null)
			patientDescription.setDateMissed(Context.getDateFormat().format(missedLastAppointment));
		*/
		return patientDescription;
	}
}
