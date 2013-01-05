package org.openmrs.module.appointment.web.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
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
		binder.registerCustomEditor(Provider.class, new ProviderEditor());
	}
	
	@ModelAttribute("appointmentList")
	public List<Appointment> getAppointmentList(
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
	
	@ModelAttribute("selectedLocation")
	public Location getLocation(@RequestParam(value = "locationId", required = false) Location location) {
		if (location != null)
			return location;
		else
			return null;
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getProviderService().getAllProviders();
	}
	
	@ModelAttribute("appointmentTypeList")
	public Set<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypes();
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
		
		return statuses;
	}
	
	@RequestMapping(value = "/module/appointment/appointmentList", method = RequestMethod.GET)
	public void showForm(ModelMap model) {
		if (Context.isAuthenticated()) {
			Calendar cal = Calendar.getInstance();
			Date todayStart = new Date();
			cal.setTime(todayStart);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			todayStart = cal.getTime();
			
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date todayEnd = cal.getTime();
			
			List<Appointment> appointments = getAppointmentList(null, todayStart, todayEnd, null, null, null, null);
			model.put("appointmentList", appointments);
		}
	}
	
	@RequestMapping(value = "/module/appointment/appointmentList", method = RequestMethod.POST)
	public void onSubmit(@ModelAttribute("appointmentList") List<Appointment> appointmentList, Errors errors,
	        ModelMap model, @RequestParam(value = "fromDate", required = false) Date fromDate,
	        @RequestParam(value = "toDate", required = false) Date toDate) {
		if (fromDate != null && toDate != null && !fromDate.before(toDate))
			errors.reject("appointment.Appointment.error.InvalidDateInterval");
	}
}
