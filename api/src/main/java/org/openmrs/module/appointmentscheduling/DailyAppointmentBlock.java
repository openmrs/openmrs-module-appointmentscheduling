package org.openmrs.module.appointmentscheduling;

import org.openmrs.Provider;

import java.util.Date;
import java.util.List;

public class DailyAppointmentBlock {
	
	private List<Appointment> appointments;
	
	private AppointmentBlock appointmentBlock;
	
	public DailyAppointmentBlock(List<Appointment> appointments, AppointmentBlock appointmentBlock) {
		this.appointments = appointments;
		this.appointmentBlock = appointmentBlock;
	}
	
	public Date getStartDate() {
		return appointmentBlock.getStartDate();
	}
	
	public Date getEndDate() {
		return appointmentBlock.getEndDate();
	}
	
	public Provider getProvider() {
		return appointmentBlock.getProvider();
	}
	
	public List<Appointment> getAppointments() {
		return appointments;
	}
}
