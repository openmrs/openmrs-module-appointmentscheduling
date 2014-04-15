package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Provider;

import java.util.Date;
import java.util.List;

/**
 * Class that reference the scheduled appointments of an appointment block
 */
public class ScheduledAppointmentBlock extends BaseOpenmrsData {
	
	private Integer id;
	
	private List<Appointment> appointments;
	
	private AppointmentBlock appointmentBlock;
	
	public ScheduledAppointmentBlock(List<Appointment> appointments, AppointmentBlock appointmentBlock) {
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
	
	public AppointmentBlock getAppointmentBlock() {
		return appointmentBlock;
	}
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
}
