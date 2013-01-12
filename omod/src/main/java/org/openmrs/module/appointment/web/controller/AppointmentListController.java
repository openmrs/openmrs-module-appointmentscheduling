package org.openmrs.module.appointment.web.controller;

import java.security.Provider.Service;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.module.appointment.web.AppointmentEditor;
import org.openmrs.module.appointment.web.AppointmentTypeEditor;
import org.openmrs.module.appointment.web.ProviderEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for listing appointments.
 */
@Controller
public class AppointmentListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(AppointmentType.class, new AppointmentTypeEditor());
		binder.registerCustomEditor(Appointment.class, new AppointmentEditor());
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	@ModelAttribute("pageTimeout")
	public Integer getPageTimout() {
		if (Context.isAuthenticated()) {
			String timeoutString = Context.getAdministrationService().getGlobalProperty(
			    "appointment.manageAppointmentsFormTimout");
			Integer timeout = Integer.parseInt(timeoutString);
			return timeout;
		}
		return -1;
	}
	
	@ModelAttribute("selectedProvider")
	public Provider getSelectedProvider(@RequestParam(value = "providerSelect", required = false) Provider provider) {
		return provider;
	}
	
	@ModelAttribute("appointmentList")
	public List<Appointment> getAppointmentList(HttpServletRequest request,
	        @RequestParam(value = "includeCancelled", required = false) String includeCancelled,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "locationId", required = false) Location location,
	        @RequestParam(value = "providerSelect", required = false) Provider provider,
	        @RequestParam(value = "appointmentTypeSelect", required = false) AppointmentType appointmentType,
	        @RequestParam(value = "appointmentStatusSelect", required = false) String status) {
		status = (status == null || status.isEmpty()) ? null : status;
		
		if (Context.isAuthenticated()) {
			
			List<Appointment> appointments = new LinkedList<Appointment>();
			if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod())) {
				//Default date filter - today 00:00 till 23:59
				Calendar cal = Calendar.getInstance();
				fromDate = new Date();
				cal.setTime(fromDate);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				fromDate = cal.getTime();
				
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				toDate = cal.getTime();
			}
			try {
				appointments = Context.getService(AppointmentService.class).getAppointmentsByConstraints(fromDate, toDate,
				    location, provider, appointmentType, status);
			}
			catch (APIException ex) {
				return new LinkedList<Appointment>();
			}
			
			List<Appointment> filteredAppointments = new LinkedList<Appointment>();
			
			for (Appointment appointment : appointments) {
				boolean valid = true;
				if (includeCancelled == null) {
					//TODO use enum status
					if (appointment.getStatus().toLowerCase().equals("cancelled"))
						valid = false;
				}
				
				if (valid)
					filteredAppointments.add(appointment);
			}
			
			return filteredAppointments;
			
		} else
			return new LinkedList<Appointment>();
	}
	
	@ModelAttribute("providerSelect")
	public Provider getSelectedProvider(HttpServletRequest request,
	        @RequestParam(value = "providerSelect", required = false) Provider selectedProvider) {
		if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod())) {
			//Default provider filter - if user is provider, set to user
			Provider provider = null;
			Person person = Context.getAuthenticatedUser().getPerson();
			for (Provider providerIterator : Context.getProviderService().getAllProviders()) {
				if (providerIterator.getPerson() != null && providerIterator.getPerson().equals(person)) {
					provider = providerIterator;
					break;
				}
			}
			return provider;
		} else
			return selectedProvider;
	}
	
	@ModelAttribute("selectedLocation")
	public Location getLocation(HttpServletRequest request,
	        @RequestParam(value = "locationId", required = false) Location location) {
		if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod())) {
			//Default provider filter - if user is provider, set location to user default
			Provider provider = null;
			Person person = Context.getAuthenticatedUser().getPerson();
			for (Provider providerIterator : Context.getProviderService().getAllProviders()) {
				if (providerIterator.getPerson() != null && providerIterator.getPerson().equals(person)) {
					provider = providerIterator;
					break;
				}
			}
			Location defaultLocation = null;
			if (provider != null)
				defaultLocation = Context.getUserContext().getLocation();
			
			return defaultLocation;
		} else
			return location;
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getProviderService().getAllProviders(false);
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypes(false);
	}
	
	@ModelAttribute("appointmentStatusList")
	public Set<String> getAppointmentStatusList() {
		//TODO centeralize this in an enum
		Set<String> statuses = new HashSet<String>();
		statuses.add("Scheduled");
		statuses.add("Rescheduled");
		statuses.add("Walk-In");
		statuses.add("Waiting");
		statuses.add("In-Consultation");
		statuses.add("Completed");
		statuses.add("Missed");
		statuses.add("Cancelled");
		
		return statuses;
	}
	
	@ModelAttribute("waitingTimes")
	public Map<Integer, String> getWaitingTimes(ModelMap model,
	        @ModelAttribute("appointmentList") List<Appointment> appointments) {
		//Mapping appointment Id to waiting time left string
		Map<Integer, String> times = new HashMap<Integer, String>();
		
		//Mapping appointment Id to sortable number of the waiting time left
		Map<Integer, Integer> sortableTimes = new HashMap<Integer, Integer>();
		
		for (Appointment appointment : appointments) {
			//TODO change to use enum
			if (appointment.getStatus().toLowerCase().equals("waiting")) {
				Date lastChanged = Context.getService(AppointmentService.class).getAppointmentCurrentStatusStartDate(
				    appointment);
				Date now = new Date();
				
				int diffMinutes = (int) Math.floor((now.getTime() - lastChanged.getTime()) / (1000 * 60));
				int diffHours = ((diffMinutes - (diffMinutes / 60)) > 0) ? (diffMinutes / 60) : 0;
				diffMinutes -= 60 * diffHours;
				int diffDays = ((diffHours - (diffHours / 24)) > 0) ? (diffHours / 24) : 0;
				diffHours -= 24 * diffDays;
				
				String minutes = Context.getMessageSourceService().getMessage("appointment.Appointment.minutes");
				String hours = Context.getMessageSourceService().getMessage("appointment.Appointment.hours");
				String days = Context.getMessageSourceService().getMessage("appointment.Appointment.days");
				
				String representation = "";
				
				if (diffDays > 0)
					representation += diffDays + " " + days + " ";
				if (diffHours > 0)
					representation += diffHours + " " + hours + " ";
				if (diffMinutes > 0)
					representation += diffMinutes + " " + minutes + " ";
				
				times.put(appointment.getId(), representation);
				sortableTimes.put(appointment.getId(), (diffMinutes + 60 * diffHours + 60 * 24 * diffDays));
				
			} else {
				times.put(appointment.getId(), "");
				sortableTimes.put(appointment.getId(), 0);
			}
		}
		
		model.put("sortableWaitingTimes", sortableTimes);
		return times;
	}
	
	@RequestMapping(value = "/module/appointment/appointmentList", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
		
	}
	
	@RequestMapping(value = "/module/appointment/appointmentList", method = RequestMethod.POST)
	public void onSubmit(HttpServletRequest request, @ModelAttribute("appointmentList") List<Appointment> appointmentList,
	        Errors errors, @RequestParam(value = "selectAppointment", required = false) Appointment selectedAppointment,
	        ModelMap model, @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate) {
		if (fromDate != null && toDate != null && !fromDate.before(toDate)) {
			errors.reject("appointment.Appointment.error.InvalidDateInterval");
			model.put("errors", errors);
			return;
		}
		//TODO change to use enum
		if (request.getParameter("startConsultation") != null) {
			Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment, "In-Consultation");
			
			//Start a new visit
			//			Visit visit = new Visit(selectedAppointment.getPatient(), null, new Date());
			//			selectedAppointment.setVisit(visit);
			//			Context.getService(AppointmentService.class).saveAppointment(selectedAppointment);
			//			Context.getVisitService().saveVisit(visit);
		} else if (request.getParameter("endConsultation") != null) {
			Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment, "Completed");
			
			//End visit
			//			Visit visit = selectedAppointment.getVisit();
			//			if(visit!=null){
			//				Context.getVisitService().endVisit(visit, new Date());
			//				Context.getVisitService().saveVisit(visit);
			//			}
		} else if (request.getParameter("checkIn") != null)
			Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment, "Waiting");
		else if (request.getParameter("missAppointment") != null)
			Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment, "Missed");
		else if (request.getParameter("cancelAppointment") != null)
			Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment, "Cancelled");
		
		if (selectedAppointment != null) {
			Map<Integer, String> waitingTimes = (Map<Integer, String>) model.get("waitingTimes");
			String representation = (selectedAppointment.getStatus().equalsIgnoreCase("Waiting") ? "0 "
			        + Context.getMessageSourceService().getMessage("appointment.Appointment.minutes") : "");
			waitingTimes.put(selectedAppointment.getId(), representation);
			model.put("waitingTimes", waitingTimes);
			
			Map<Integer, Integer> sortableTimes = (Map<Integer, Integer>) model.get("sortableWaitingTimes");
			Integer sortable = (selectedAppointment.getStatus().equalsIgnoreCase("Waiting") ? 1 : 0);
			sortableTimes.put(selectedAppointment.getId(), sortable);
			model.put("sortableWaitingTimes", sortableTimes);
		}
	}
}
