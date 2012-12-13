package org.openmrs.module.appointment.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Provider;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
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
		patientData.setPatientId(patientId.toString());
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
	
	public List<AppointmentBlock> getAppointmentBlocks(String selectedDate, Integer locationId) throws ParseException {
		List<AppointmentBlock> appointmentBlockList = new ArrayList<AppointmentBlock>();
		Date fromDate = null;
		//location needs authentication
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			Location location = null;
			if (locationId != null)
				location = Context.getLocationService().getLocation(locationId);
			//In case the user selected a date.
			if (!selectedDate.isEmpty()) {
				fromDate = Context.getDateTimeFormat().parse(selectedDate);
			}
			appointmentBlockList = appointmentService.getAppointmentBlocks(fromDate, location);
		}
		return appointmentBlockList;
	}
	
	public void purgeAppointmentBlock(Integer appointmentBlockId) {
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			AppointmentBlock appointmentBlock = appointmentService.getAppointmentBlock(appointmentBlockId);
			appointmentService.purgeAppointmentBlock(appointmentBlock);
		}
	}
}
