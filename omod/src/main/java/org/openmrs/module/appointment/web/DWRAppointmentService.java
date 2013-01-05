package org.openmrs.module.appointment.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.TimeSlot;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.web.WebConstants;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestParam;

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
		patientData
		        .setIdentifiers(Context.getService(AppointmentService.class).getPatientIdentifiersRepresentation(patient));
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
	
	public List<AppointmentBlockData> getAppointmentBlocks(String fromDate, String toDate, Integer locationId)
	        throws ParseException {
		List<AppointmentBlock> appointmentBlockList = new ArrayList<AppointmentBlock>();
		List<AppointmentBlockData> appointmentBlockDatalist = new ArrayList<AppointmentBlockData>();
		Date fromAsDate = null;
		Date toAsDate = null;
		//location needs authentication
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			Location location = null;
			if (locationId != null) {
				location = Context.getLocationService().getLocation(locationId);
			}
			//In case the user selected a date.
			if (!fromDate.isEmpty()) {
				fromAsDate = Context.getDateTimeFormat().parse(fromDate);
			}
			if (!toDate.isEmpty()) {
				toAsDate = Context.getDateTimeFormat().parse(toDate);
			}
			appointmentBlockList = appointmentService
			        .getAppointmentBlocks(fromAsDate, toAsDate, buildLocationList(location));
			
			for (AppointmentBlock appointmentBlock : appointmentBlockList) {
				//don't include voided appointment blocks
				if (!appointmentBlock.isVoided()) {
					appointmentBlockDatalist
					        .add(new AppointmentBlockData(appointmentBlock.getId(), appointmentBlock.getLocation(),
					                appointmentBlock.getProvider(), appointmentBlock.getTypes(), appointmentBlock
					                        .getStartDate(), appointmentBlock.getEndDate(), this
					                        .getTimeSlotLength(appointmentBlock.getId())));
				}
			}
		}
		return appointmentBlockDatalist;
	}
	
	private String buildLocationList(Location location) {
		String ans = "";
		if (location != null) {
			ans = location.getId() + "";
			if (location.getChildLocations().size() == 0)
				return ans;
			else {
				for (Location locationChild : location.getChildLocations()) {
					ans += "," + buildLocationList(locationChild);
				}
			}
		}
		return ans;
	}
	
	private String getTimeSlotLength(Integer appointmentBlockId) {
		if (appointmentBlockId == null)
			return "";
		else {
			if (Context.isAuthenticated()) {
				AppointmentService as = Context.getService(AppointmentService.class);
				AppointmentBlock appointmentBlock = as.getAppointmentBlock(appointmentBlockId);
				TimeSlot timeSlot = Context.getService(AppointmentService.class).getTimeSlotsInAppointmentBlock(
				    appointmentBlock).get(0);
				return (timeSlot.getEndDate().getTime() - timeSlot.getStartDate().getTime()) / 60000 + "";
			}
		}
		return "";
	}
}
