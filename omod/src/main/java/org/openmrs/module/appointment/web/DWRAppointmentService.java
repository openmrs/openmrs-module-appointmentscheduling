package org.openmrs.module.appointment.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;

/**
 * DWR patient methods. The methods in here are used in the webapp to get data from the database via
 * javascript calls.
 * 
 * @see PatientService
 */
public class DWRAppointmentService {

	public PatientData getPatientDescription(Integer patientId) {
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null)
			return null;
		PatientData patientData = new PatientData();
		//Get Patient's phone
		Integer phonePropertyId = Integer.parseInt(Context.getAdministrationService().getGlobalProperty(
		    "appointment.phoneNumberPersonAttributeTypeId"));
		PersonAttribute phoneAttribute = patient.getAttribute(phonePropertyId);
		if (phoneAttribute != null)
			patientData.setPhoneNumber(phoneAttribute.getValue());
		//Checks if patient missed his/her last appointment.
		Appointment lastAppointment = Context.getService(AppointmentService.class).getLastAppointment(patient);
		//TODO: change hard coded "MISSED" to correct enum value
		if (lastAppointment != null && lastAppointment.getStatus() == "MISSED")
			patientData.setDateMissedLastAppointment(Context.getDateFormat().format(
			    lastAppointment.getTimeSlot().getStartDate()));

		return patientData;
	}

	public List<TimeSlot> getAvailableTimeSlots() {
		//TODO change to include constraints.
		List<TimeSlot> timeSlots = Context.getService(AppointmentService.class).getAllTimeSlots();
		return timeSlots;
	}
	
	public List<AppointmentBlockDetails> getAppointmentBlocks(Date fromDate, Date toDate, Integer locationId) {
		List<AppointmentBlock> appointmentBlockList = new ArrayList<AppointmentBlock>();
		List<AppointmentBlockDetails> appointmentBlockDetails = new Vector<AppointmentBlockDetails>();
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			Location location = null;
			if (locationId != null)
				location = Context.getLocationService().getLocation(locationId);
			appointmentBlockList = appointmentService.getAppointmentBlocks(fromDate, toDate, location);
			for (AppointmentBlock appointmentBlock : appointmentBlockList) {
				Set<AppointmentType> appointmentTypes = appointmentBlock.getTypes();
				String appointmentTypeNames = "";
				int appointmentTypeSize = appointmentTypes.size();
				for (AppointmentType appointmentType : appointmentTypes) {
					appointmentTypeNames += appointmentType.getName();
					//if it is not the last type, append ","
					if (appointmentTypeSize > 1)
						appointmentTypeNames += ", ";
					appointmentTypeSize--;
				}
				appointmentBlockDetails.add(new AppointmentBlockDetails(appointmentBlock.getId() + "", appointmentBlock
				        .getLocation().getName(), appointmentBlock.getProvider().getName(), appointmentTypeNames,
				        appointmentBlock.getStartDate().toString(), appointmentBlock.getEndDate().toString()));
			}
		}
		return appointmentBlockDetails;
	}
}
