package org.openmrs.module.appointmentscheduling.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.Provider;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;

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
		    AppointmentUtils.GP_PATIENT_PHONE_NUMBER));
		PersonAttribute phoneAttribute = patient.getAttribute(phonePropertyId);
		if (phoneAttribute != null)
			patientData.setPhoneNumber(phoneAttribute.getValue());
		//Checks if patient missed his/her last appointment.
		Appointment lastAppointment = Context.getService(AppointmentService.class).getLastAppointment(patient);
		if (lastAppointment != null && lastAppointment.getStatus() == AppointmentStatus.MISSED)
			patientData.setDateMissedLastAppointment(Context.getDateFormat().format(
			    lastAppointment.getTimeSlot().getStartDate()));
		
		return patientData;
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
					Set<String> typesNames = new HashSet<String>();
					Set<AppointmentType> appointmentTypes = appointmentBlock.getTypes();
					for (AppointmentType appointmentType : appointmentTypes) {
						typesNames.add(appointmentType.getName());
					}
					String dateOnly = Context.getDateFormat().format(appointmentBlock.getStartDate());
					String startTimeOnly = Context.getTimeFormat().format(appointmentBlock.getStartDate());
					String endTimeOnly = Context.getTimeFormat().format(appointmentBlock.getEndDate());
					
					appointmentBlockDatalist.add(new AppointmentBlockData(appointmentBlock.getId(), appointmentBlock
					        .getLocation().getName(), appointmentBlock.getProvider().getName(), typesNames, dateOnly,
					        startTimeOnly, endTimeOnly, this.getTimeSlotLength(appointmentBlock.getId()), appointmentBlock
					                .getStartDate()));
				}
			}
		}
		return appointmentBlockDatalist;
	}
	
	public Integer[] getNumberOfAppointmentsInAppointmentBlock(Integer appointmentBlockId) {
		Integer[] appointmentsCount = null;
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentBlockId != null) {
				appointmentsCount = new Integer[3];
				appointmentsCount[0] = 0;
				appointmentsCount[1] = 0;
				appointmentsCount[2] = 0;
				//Assumption - Exists such an appointment block in the data base with the given Id
				AppointmentBlock appointmentBlock = as.getAppointmentBlock(appointmentBlockId);
				//Getting the timeslots of the given appointment block
				List<TimeSlot> timeSlots = as.getTimeSlotsInAppointmentBlock(appointmentBlock);
				for (TimeSlot timeSlot : timeSlots) {
					List<Appointment> appointmentsInTimeSlot = as.getAppointmentsInTimeSlot(timeSlot);
					for (Appointment appointment : appointmentsInTimeSlot) {
						if (appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.INCONSULTATION.toString())
						        || appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.WAITING.toString())) {
							//Active appointments
							appointmentsCount[0]++;
						} else if (appointment.getStatus().toString().equalsIgnoreCase(
						    AppointmentStatus.SCHEDULED.toString())) {
							appointmentsCount[1]++; //Scheduled appointments
						} else {
							appointmentsCount[2]++; //Missed/Cancelled/Completed appointments
						}
					}
				}
			}
		}
		return appointmentsCount;
	}
	
	public boolean validateDates(String fromDate, String toDate) throws ParseException {
		boolean error = false;
		WebContext webContext = WebContextFactory.get();
		HttpSession httpSession = webContext.getHttpServletRequest().getSession();
		//date validation
		if (!Context.getDateTimeFormat().parse(fromDate).before(Context.getDateTimeFormat().parse(toDate))) {
			error = true;
		}
		return error;
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
	
	/**
	 * 
	 * Checks whether a provider has an ongoing open consultation
	 * 
	 * @param appointmentId - The appointment id from which we will load the provider
	 * @return True if has any open consultation, False otherwise
	 */
	public Boolean checkProviderOpenConsultations(Integer appointmentId) {
		if (appointmentId == null)
			return false;
		else {
			Appointment appointment = Context.getService(AppointmentService.class).getAppointment(appointmentId);
			Provider provider = appointment.getTimeSlot().getAppointmentBlock().getProvider();
			
			List<Appointment> inconsultationAppointments = Context.getService(AppointmentService.class)
			        .getAppointmentsByConstraints(null, null, null, provider, null, AppointmentStatus.INCONSULTATION);
			
			return (inconsultationAppointments.size() != 0);
		}
	}
	
	/**
	 * 
	 * Checks whether a provider has an ongoing open consultation
	 * 
	 * @param appointmentId - The patient id from which we will its most recent appointment's provider
	 * @return True if has any open consultation, False otherwise
	 */
	public Boolean checkProviderOpenConsultationsByPatient(Integer patientId) {
		if (patientId == null)
			return false;
		else {
			Appointment appointment = Context.getService(AppointmentService.class).getLastAppointment(
			    Context.getPatientService().getPatient(patientId));
			Provider provider = appointment.getTimeSlot().getAppointmentBlock().getProvider();
			
			List<Appointment> inconsultationAppointments = Context.getService(AppointmentService.class)
			        .getAppointmentsByConstraints(null, null, null, provider, null, AppointmentStatus.INCONSULTATION);
			
			return (inconsultationAppointments.size() != 0);
		}
	}
	
}
