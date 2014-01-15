package org.openmrs.module.appointmentscheduling.web.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.AppointmentUtils;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.web.AppointmentEditor;
import org.openmrs.module.appointmentscheduling.web.AppointmentTypeEditor;
import org.openmrs.module.appointmentscheduling.web.ProviderEditor;
import org.openmrs.util.OpenmrsUtil;
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
	
	@ModelAttribute("providerSelect")
	public Provider getSelectedProvider(HttpServletRequest request,
	        @RequestParam(value = "providerSelect", required = false) Provider selectedProvider) {
		
		if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod()) && Context.getAuthenticatedUser() != null) {
			//Set Default provider filter on GET request - if user is provider, set to user
			Provider provider = null;
			Person person = Context.getAuthenticatedUser().getPerson();
			for (Provider providerIterator : Context.getProviderService().getAllProviders(false)) {
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
		if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod()) && Context.getAuthenticatedUser() != null) {
			//Set Default provider filter on GET request - if user is provider, set location to user default
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
		return Context.getService(AppointmentService.class).getAllProvidersSorted(false);
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypesSorted(false);
	}
	
	@ModelAttribute("appointmentStatusList")
	public Set<AppointmentStatus> getAppointmentStatusList() {
		Set<AppointmentStatus> statuses = new HashSet<AppointmentStatus>();
		for (AppointmentStatus status : AppointmentStatus.values())
			statuses.add(status);
		
		return statuses;
	}
	
	//Filter the appointments by the given filters and save in list
	@ModelAttribute("appointmentList")
	public List<Appointment> getAppointmentList(HttpServletRequest request, HttpSession session, ModelMap model,
	        @RequestParam(value = "includeCancelled", required = false) String includeCancelled,
	        @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate,
	        @RequestParam(value = "locationId", required = false) Location location,
	        @RequestParam(value = "providerSelect", required = false) Provider provider,
	        @RequestParam(value = "appointmentTypeSelect", required = false) AppointmentType appointmentType,
	        @RequestParam(value = "appointmentStatusSelect", required = false) AppointmentStatus status) {
		
		List<Appointment> filteredAppointments = new LinkedList<Appointment>();
		
		if (Context.isAuthenticated()) {
			List<Appointment> appointments = new LinkedList<Appointment>();
			if (RequestMethod.GET.toString().equalsIgnoreCase(request.getMethod())) {
				//Set Default date filter on GET request - today 00:00 till 23:59
				fromDate = new Date();
				fromDate = OpenmrsUtil.firstSecondOfDay(fromDate);
				
				toDate = new Date();
				toDate = OpenmrsUtil.getLastMomentOfDay(toDate);
				
				provider = this.getSelectedProvider(request, provider);
				location = this.getLocation(request, location);
			}
			try {
				appointments = Context.getService(AppointmentService.class).getAppointmentsByConstraints(fromDate, toDate,
				    location, provider, appointmentType, status);
			}
			catch (APIException ex) {
				return new LinkedList<Appointment>();
			}
			
			//Filter appointments by includeCancelled checkbox
			for (Appointment appointment : appointments) {
				boolean valid = true;
				if (includeCancelled == null) {
					if (appointment.getStatus() == AppointmentStatus.CANCELLED)
						valid = false;
				}
				
				if (valid)
					filteredAppointments.add(appointment);
			}
			
			getWaitingTimes(model, filteredAppointments);
			return filteredAppointments;
			
		} else
			return filteredAppointments;
	}
	
	public void getWaitingTimes(ModelMap model, List<Appointment> appointments) {
		//Mapping of waiting times to use in the "Waiting Time" column
		
		//Mapping appointment Id to waiting time left string
		Map<Integer, String> times = new HashMap<Integer, String>();
		
		//Mapping appointment Id to sortable number of the waiting time left
		Map<Integer, Integer> sortableTimes = new HashMap<Integer, Integer>();
		
		//Calculate for each waiting appointment the waiting time
		for (Appointment appointment : appointments) {
			if (appointment.getStatus() == AppointmentStatus.WAITING || appointment.getStatus() == AppointmentStatus.WALKIN) {
				Date lastChanged = Context.getService(AppointmentService.class).getAppointmentCurrentStatusStartDate(
				    appointment);
				Date now = new Date();
				
				//Calculate waiting time as a composition of minutes, hours and days
				int diffMinutes = (int) Math.floor((now.getTime() - lastChanged.getTime()) / (1000 * 60));
				int diffHours = ((diffMinutes - (diffMinutes / 60)) > 0) ? (diffMinutes / 60) : 0;
				diffMinutes -= 60 * diffHours;
				int diffDays = ((diffHours - (diffHours / 24)) > 0) ? (diffHours / 24) : 0;
				diffHours -= 24 * diffDays;
				
				//String labels of minutes, hours and days
				String minutes = Context.getMessageSourceService().getMessage("appointmentscheduling.Appointment.minutes");
				String hours = Context.getMessageSourceService().getMessage("appointmentscheduling.Appointment.hours");
				String days = Context.getMessageSourceService().getMessage("appointmentscheduling.Appointment.days");
				
				String representation = "";
				
				//Concatenate values into a string
				if (diffDays > 0)
					representation += diffDays + " " + days + " ";
				if (diffHours > 0)
					representation += diffHours + " " + hours + " ";
				if (diffMinutes > 0)
					representation += diffMinutes + " " + minutes + " ";
				if (representation.length() == 0)
					representation = "0 " + minutes;
				
				times.put(appointment.getId(), representation);
				sortableTimes.put(appointment.getId(), (diffMinutes + 60 * diffHours + 60 * 24 * diffDays));
				
			} else {
				//if appointment isn't on "waiting" status
				times.put(appointment.getId(), "");
				sortableTimes.put(appointment.getId(), 0);
			}
		}
		model.put("waitingTimes", times);
		model.put("sortableWaitingTimes", sortableTimes);
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentList", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentList", method = RequestMethod.POST)
	public String onSubmit(HttpServletRequest request, @ModelAttribute("appointmentList") List<Appointment> appointmentList,
	        Errors errors, @RequestParam(value = "selectAppointment", required = false) Appointment selectedAppointment,
	        ModelMap model, @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate) {
		
		if (Context.isAuthenticated()) {
			AppointmentStatus newStatus = null;
			
			//Handle Status changes
			if (request.getParameter("startConsultation") != null
			        && selectedAppointment.getStatus() != AppointmentStatus.INCONSULTATION) {
				Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment,
				    AppointmentStatus.INCONSULTATION);
				
				String patientId = selectedAppointment.getPatient().getId().toString();
				
				newStatus = AppointmentStatus.INCONSULTATION;
				
			} else if (request.getParameter("endConsultation") != null
			        && selectedAppointment.getStatus() != AppointmentStatus.COMPLETED) {
				//End visit
				Visit visit = selectedAppointment.getVisit();
				//also check whether the visit ended
				if (visit != null && visit.getStopDatetime() == null) {
					Context.getVisitService().endVisit(visit, new Date());
					Context.getVisitService().saveVisit(visit);
				}
				
				Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment,
				    AppointmentStatus.COMPLETED);
				
				newStatus = AppointmentStatus.COMPLETED;
				
			} else if (request.getParameter("checkIn") != null
			        && selectedAppointment.getStatus() != AppointmentStatus.WAITING) {
				
				String visitTypeIdString = Context.getAdministrationService().getGlobalProperty(
				    AppointmentUtils.GP_DEFAULT_VISIT_TYPE);
				Integer visitTypeId = Integer.parseInt(visitTypeIdString);
				VisitType defaultVisitType = Context.getVisitService().getVisitType(visitTypeId);
				
				//Start a new visit
				Visit visit = new Visit(selectedAppointment.getPatient(), defaultVisitType, new Date());
				visit.setLocation(selectedAppointment.getTimeSlot().getAppointmentBlock().getLocation());
				visit = Context.getVisitService().saveVisit(visit);
				selectedAppointment.setVisit(visit);
				Context.getService(AppointmentService.class).saveAppointment(selectedAppointment);
				
				Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment,
				    AppointmentStatus.WAITING);
				
				newStatus = AppointmentStatus.WAITING;
				
			} else if (request.getParameter("missAppointment") != null
			        && selectedAppointment.getStatus() != AppointmentStatus.MISSED) {
				//End visit
				Visit visit = selectedAppointment.getVisit();
				//also check whether the visit ended
				if (visit != null && visit.getStopDatetime() == null) {
					Context.getVisitService().endVisit(visit, new Date());
					Context.getVisitService().saveVisit(visit);
				}
				
				Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment,
				    AppointmentStatus.MISSED);
				
				newStatus = AppointmentStatus.MISSED;
				
			} else if (request.getParameter("cancelAppointment") != null
			        && selectedAppointment.getStatus() != AppointmentStatus.CANCELLED) {
				//End visit
				Visit visit = selectedAppointment.getVisit();
				//also check whether the visit ended
				if (visit != null && visit.getStopDatetime() == null) {
					Context.getVisitService().endVisit(visit, new Date());
					Context.getVisitService().saveVisit(visit);
				}
				
				Context.getService(AppointmentService.class).changeAppointmentStatus(selectedAppointment,
				    AppointmentStatus.CANCELLED);
				
				newStatus = AppointmentStatus.CANCELLED;
				
			}
			if (selectedAppointment != null) {
				if (newStatus == AppointmentStatus.WAITING) {
					//Update waiting time according to new status
					Map<Integer, String> waitingTimes = (Map<Integer, String>) model.get("waitingTimes");
					waitingTimes.put(selectedAppointment.getId(),
					    "0 " + Context.getMessageSourceService().getMessage("appointmentscheduling.Appointment.minutes"));
					model.put("waitingTimes", waitingTimes);
					
					Map<Integer, Integer> sortableTimes = (Map<Integer, Integer>) model.get("sortableWaitingTimes");
					sortableTimes.put(selectedAppointment.getId(), 1);
					model.put("sortableWaitingTimes", sortableTimes);
				} else if (newStatus == AppointmentStatus.CANCELLED || newStatus == AppointmentStatus.INCONSULTATION
				        || newStatus == AppointmentStatus.MISSED) {
					//Update waiting time according to new status
					Map<Integer, String> waitingTimes = (Map<Integer, String>) model.get("waitingTimes");
					waitingTimes.put(selectedAppointment.getId(), "");
					model.put("waitingTimes", waitingTimes);
					
					Map<Integer, Integer> sortableTimes = (Map<Integer, Integer>) model.get("sortableWaitingTimes");
					sortableTimes.put(selectedAppointment.getId(), 0);
					model.put("sortableWaitingTimes", sortableTimes);
				}
				
				if (newStatus == AppointmentStatus.INCONSULTATION)
					return "redirect:/patientDashboard.form?patientId="
					        + selectedAppointment.getPatient().getId().toString();
				
			}
		}
		
		return null;
	}
}
