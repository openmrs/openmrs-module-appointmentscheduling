package org.openmrs.module.appointment.web.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.Appointment;
import org.openmrs.module.appointment.api.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for listing appointments.
 */
@Controller
public class AppointmentListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@ModelAttribute("appointmentList")
	public List<Appointment> getAppointmentList() {
		if (Context.isAuthenticated())
			return Context.getService(AppointmentService.class).getAllAppointments();
		else
			return new LinkedList<Appointment>();
	}
	
	@RequestMapping(value = "/module/appointment/appointmentList", method = RequestMethod.GET)
	public void showForm(ModelMap model) throws IOException {
		
	}
}
