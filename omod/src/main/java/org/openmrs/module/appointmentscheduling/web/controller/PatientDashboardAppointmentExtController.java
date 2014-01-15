package org.openmrs.module.appointmentscheduling.web.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatientDashboardAppointmentExtController {
	
	@RequestMapping("/module/appointmentscheduling/patientDashboardAppointmentExt.form")
	public String showForm(HttpServletRequest request, @RequestParam("patientId") Integer patientId,
	        @RequestParam("action") String action) {
		//End the consulation
		if (action.equals("endConsult")) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			Appointment appointment = Context.getService(AppointmentService.class).getLastAppointment(patient);
			Visit visit = appointment.getVisit();
			
			//Check if was ended already
			if (visit.getStopDatetime() == null) {
				Context.getVisitService().endVisit(visit, new Date());
				Context.getVisitService().saveVisit(visit);
			}
			
			appointment.setStatus(AppointmentStatus.COMPLETED);
			Context.getService(AppointmentService.class).saveAppointment(appointment);
			
			return "redirect:/module/appointmentscheduling/appointmentList.list";
		} else if (action.equals("startConsult")) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			Appointment appointment = Context.getService(AppointmentService.class).getLastAppointment(patient);
			
			appointment.setStatus(AppointmentStatus.INCONSULTATION);
			Context.getService(AppointmentService.class).saveAppointment(appointment);
			
			return "redirect:/patientDashboard.form?patientId=" + patientId;
		}
		
		return "";
	}
}
