package org.openmrs.module.appointmentscheduling.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.openmrs.util.OpenmrsUtil;

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
		//Get Patient's full name
		patientData.setFullName(patient.getPersonName().getFullName());
		
		return patientData;
	}
	
	public List<AppointmentBlockData> getAppointmentBlocksForCalendar(Long fromDate, Long toDate, Integer locationId,
	        Integer providerId, Integer appointmentTypeId) throws ParseException {
		List<AppointmentBlockData> appointmentBlockDatalist = new ArrayList<AppointmentBlockData>();
		if (Context.isAuthenticated()) {
			Calendar cal = OpenmrsUtil.getDateTimeFormat(Context.getLocale()).getCalendar();
			cal.setTimeInMillis(fromDate);
			Date fromDateAsDate = cal.getTime();
			cal.setTimeInMillis(toDate);
			Date toDateAsDate = cal.getTime();
			
			appointmentBlockDatalist = this.getAppointmentBlocks(Context.getDateTimeFormat().format(fromDateAsDate), Context
			        .getDateTimeFormat().format(toDateAsDate), locationId, providerId, appointmentTypeId);
		}
		return appointmentBlockDatalist;
	}
	
	public List<AppointmentBlockData> getAppointmentBlocks(String fromDate, String toDate, Integer locationId,
	        Integer providerId, Integer appointmentTypeId) throws ParseException {
		List<AppointmentBlock> appointmentBlockList = new ArrayList<AppointmentBlock>();
		List<AppointmentBlockData> appointmentBlockDatalist = new ArrayList<AppointmentBlockData>();
		Date fromAsDate = null;
		Date toAsDate = null;
		Provider provider = null;
		AppointmentType appointmentType = null;
		//location needs authentication
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			Location location = null;
			//In case the user selected a locaiton
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
			//In case the user selected a provider.
			if (providerId != null) {
				provider = Context.getProviderService().getProvider(providerId);
			}
			//In case the user selected an appointment type.
			if (appointmentTypeId != null) {
				appointmentType = appointmentService.getAppointmentType(appointmentTypeId);
			}
			appointmentBlockList = appointmentService.getAppointmentBlocks(fromAsDate, toAsDate,
			    buildLocationList(location), provider, appointmentType);
			
			for (AppointmentBlock appointmentBlock : appointmentBlockList) {
				//don't include voided appointment blocks
				if (!appointmentBlock.isVoided()) {
					Set<String> typesNames = new HashSet<String>();
					Set<AppointmentType> appointmentTypes = appointmentBlock.getTypes();
					for (AppointmentType type : appointmentTypes) {
						typesNames.add(type.getName());
					}
					String dateOnly = Context.getDateFormat().format(appointmentBlock.getStartDate());
					String startTimeOnly = Context.getTimeFormat().format(appointmentBlock.getStartDate());
					String endTimeOnly = Context.getTimeFormat().format(appointmentBlock.getEndDate());
					
					appointmentBlockDatalist.add(new AppointmentBlockData(appointmentBlock.getId(), appointmentBlock
					        .getLocation().getName(), appointmentBlock.getProvider() != null ? appointmentBlock
					        .getProvider().getName() : "", typesNames, dateOnly, startTimeOnly, endTimeOnly, this
					        .getTimeSlotLength(appointmentBlock.getId()), appointmentBlock.getStartDate(), appointmentBlock
					        .getEndDate()));
				}
			}
		}
		return appointmentBlockDatalist;
	}
	
	public List<List<AppointmentData>> getPatientsInAppointmentBlock(Integer appointmentBlockId) {
		List<List<AppointmentData>> patients = null;
		if (Context.isAuthenticated()) {
			AppointmentService as = Context.getService(AppointmentService.class);
			if (appointmentBlockId != null) {
				patients = new ArrayList<List<AppointmentData>>();
				patients.add(new ArrayList<AppointmentData>()); //active appointments
				patients.add(new ArrayList<AppointmentData>()); //scheduled appointments
				patients.add(new ArrayList<AppointmentData>()); //Missed/Cancelled/Complete appointments
				//Assumption - Exists such an appointment block in the data base with the given Id
				AppointmentBlock appointmentBlock = as.getAppointmentBlock(appointmentBlockId);
				//Getting the timeslots of the given appointment block
				List<TimeSlot> timeSlots = as.getTimeSlotsInAppointmentBlock(appointmentBlock);
				for (TimeSlot timeSlot : timeSlots) {
					List<Appointment> appointmentsInTimeSlot = as.getAppointmentsInTimeSlot(timeSlot);
					for (Appointment appointment : appointmentsInTimeSlot) {
						//Create an AppointmentData object
						PatientData patientDescription = this.getPatientDescription(appointment.getPatient().getPatientId());
						TimeSlot appointmentTimeSlot = appointment.getTimeSlot();
						String dateOnly = Context.getDateFormat().format(appointment.getTimeSlot().getStartDate());
						String startTimeOnly = Context.getTimeFormat().format(appointment.getTimeSlot().getStartDate());
						String endTimeOnly = Context.getTimeFormat().format(appointment.getTimeSlot().getEndDate());
						AppointmentData appointmentdata = new AppointmentData(patientDescription, appointment
						        .getAppointmentType().getName(), dateOnly, startTimeOnly, endTimeOnly,
						        appointment.getReason());
						if (appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.INCONSULTATION.toString())
						        || appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.WAITING.toString())
						        || appointment.getStatus().toString().equalsIgnoreCase(AppointmentStatus.WALKIN.toString())) {
							//Active appointments (In-Consultation\Waiting\Walk-In)
							patients.get(0).add(appointmentdata);
						} else if (appointment.getStatus().toString()
						        .equalsIgnoreCase(AppointmentStatus.SCHEDULED.toString())) {
							patients.get(1).add(appointmentdata); //Scheduled appointments
						} else {
							patients.get(2).add(appointmentdata); //Missed\Cancelled\Completed appointments
						}
					}
				}
			}
		}
		return patients;
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
				TimeSlot timeSlot = Context.getService(AppointmentService.class)
				        .getTimeSlotsInAppointmentBlock(appointmentBlock).get(0);
				return (timeSlot.getEndDate().getTime() - timeSlot.getStartDate().getTime()) / 60000 + "";
			}
		}
		return "";
	}
	
	/**
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
	 * Checks whether a provider has an ongoing open consultation
	 * 
	 * @param appointmentId - The patient id from which we will its most recent appointment's
	 *            provider
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
	
	/**
	 * Computes the average duration (in Minutes) of a status history by appointment type
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return An array of <String, Double> appointment_type_name, average_history_status_time
	 * @throws ParseException
	 */
	private Object[][] getAverageHistoryDurationByCriteria(String fromDate, String toDate, AppointmentStatus status)
	        throws ParseException {
		
		Date fromDateAsDate = Context.getDateTimeFormat().parse(fromDate);
		Date toDateAsDate = Context.getDateTimeFormat().parse(toDate);
		
		Object[][] plotData = null;
		
		Map<AppointmentType, Double> averages = Context.getService(AppointmentService.class)
		        .getAverageHistoryDurationByConditions(fromDateAsDate, toDateAsDate, status);
		plotData = new Object[averages.size()][2];
		
		int i = 0;
		for (Map.Entry<AppointmentType, Double> entry : averages.entrySet()) {
			plotData[i][0] = entry.getKey().getName();
			plotData[i][1] = entry.getValue().toString();
			i++;
		}
		
		return plotData;
		
	}
	
	/**
	 * Computes the average duration (in Minutes) of a "waiting" history by appointment type
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return An array of <String, Double> appointment_type_name, average_history_waiting_time
	 * @throws ParseException
	 */
	public Object[][] getAverageWaitingTimeByType(String fromDate, String toDate) throws ParseException {
		return getAverageHistoryDurationByCriteria(fromDate, toDate, AppointmentStatus.WAITING);
	}
	
	/**
	 * Computes the average duration (in Minutes) of a "in-consultation" history by appointment type
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return An array of <String, Double> appointment_type_name,
	 *         average_history_inconsultation_time
	 * @throws ParseException
	 */
	public Object[][] getAverageConsultationTimeByType(String fromDate, String toDate) throws ParseException {
		return getAverageHistoryDurationByCriteria(fromDate, toDate, AppointmentStatus.INCONSULTATION);
	}
	
	/**
	 * Retrieves the amount of status history objects in the given criteria
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return The amount of status history objects in the given criteria
	 * @throws ParseException
	 */
	private Integer getCountOfHistoriesInStatus(String fromDate, String toDate, AppointmentStatus status)
	        throws ParseException {
		Date fromDateAsDate = Context.getDateTimeFormat().parse(fromDate);
		Date toDateAsDate = Context.getDateTimeFormat().parse(toDate);
		
		return Context.getService(AppointmentService.class)
		        .getHistoryCountByConditions(fromDateAsDate, toDateAsDate, status);
	}
	
	/**
	 * Retrieves the amount of "waiting" status history objects in the given criteria
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return The amount of status history objects in the given criteria
	 * @throws ParseException
	 */
	public Integer getCountOfWaitingHistories(String fromDate, String toDate) throws ParseException {
		return getCountOfHistoriesInStatus(fromDate, toDate, AppointmentStatus.WAITING);
	}
	
	/**
	 * Retrieves the amount of "in-consultation" status history objects in the given criteria
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return The amount of status history objects in the given criteria
	 * @throws ParseException
	 */
	public Integer getCountOfConsultationHistories(String fromDate, String toDate) throws ParseException {
		return getCountOfHistoriesInStatus(fromDate, toDate, AppointmentStatus.INCONSULTATION);
	}
	
	/**
	 * Retrieves the appointment type distribution in the given date interval.
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param toDate The upper bound of the date interval.
	 * @return Two-Dimensional array of the structure [[AppointmentType,Integer]]
	 * @throws ParseException Date parse exception
	 */
	public Object[][] getAppointmentTypeDistribution(String fromDate, String toDate) throws ParseException {
		Date fromDateAsDate = Context.getDateTimeFormat().parse(fromDate);
		Date toDateAsDate = Context.getDateTimeFormat().parse(toDate);
		Map<AppointmentType, Integer> distribution = Context.getService(AppointmentService.class)
		        .getAppointmentTypeDistribution(fromDateAsDate, toDateAsDate);
		
		Object[][] values = new Object[distribution.size()][2];
		int i = 0;
		for (Map.Entry<AppointmentType, Integer> entry : distribution.entrySet()) {
			values[i][0] = entry.getKey().getName();
			values[i][1] = entry.getValue();
			i++;
		}
		
		return values;
	}
	
	/**
	 * Get the count of appointments in the given date interval
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param toDate The upper bound of the date interval.
	 * @return The amount of appointments in the given date interval.
	 * @throws ParseException Date parse exception
	 */
	public Integer getAppointmentsCount(String fromDate, String toDate) throws ParseException {
		Date fromDateAsDate = Context.getDateTimeFormat().parse(fromDate);
		Date toDateAsDate = Context.getDateTimeFormat().parse(toDate);
		Map<AppointmentType, Integer> distribution = Context.getService(AppointmentService.class)
		        .getAppointmentTypeDistribution(fromDateAsDate, toDateAsDate);
		
		Integer count = 0;
		for (Map.Entry<AppointmentType, Integer> entry : distribution.entrySet())
			count += entry.getValue();
		
		return count;
	}
	
	/**
	 * Computes the average duration (in Minutes) of a status history by provider
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @param status The AppointmentStatus status to filter histories by.
	 * @return An array of <String, Double> provider_name, average_history_status_time
	 * @throws ParseException
	 */
	private Object[][] getAverageHistoryDurationByCriteriaByProvider(String fromDate, String toDate, AppointmentStatus status)
	        throws ParseException {
		
		Date fromDateAsDate = Context.getDateTimeFormat().parse(fromDate);
		Date toDateAsDate = Context.getDateTimeFormat().parse(toDate);
		
		Object[][] plotData = null;
		
		Map<Provider, Double> averages = Context.getService(AppointmentService.class)
		        .getAverageHistoryDurationByConditionsPerProvider(fromDateAsDate, toDateAsDate, status);
		plotData = new Object[averages.size()][2];
		
		int i = 0;
		for (Map.Entry<Provider, Double> entry : averages.entrySet()) {
			plotData[i][0] = entry.getKey().getName();
			plotData[i][1] = entry.getValue().toString();
			i++;
		}
		
		return plotData;
		
	}
	
	/**
	 * Computes the average duration (in Minutes) of a "waiting" history by provider
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return An array of <String, Double> provider_name, average_history_waiting_time
	 * @throws ParseException
	 */
	public Object[][] getAverageWaitingTimeByProvider(String fromDate, String toDate) throws ParseException {
		return getAverageHistoryDurationByCriteriaByProvider(fromDate, toDate, AppointmentStatus.WAITING);
	}
	
	/**
	 * Computes the average duration (in Minutes) of a "in-consultation" history by provider
	 * 
	 * @param fromDate The lower bound of the date interval.
	 * @param endDate The upper bound of the date interval.
	 * @return An array of <String, Double> provider_name, average_history_inconsultation_time
	 * @throws ParseException
	 */
	public Object[][] getAverageConsultationTimeByProvider(String fromDate, String toDate) throws ParseException {
		return getAverageHistoryDurationByCriteriaByProvider(fromDate, toDate, AppointmentStatus.INCONSULTATION);
	}
	
}
