package org.openmrs.module.appointment.web;

import java.util.Date;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.module.appointment.AppointmentType;

public class AppointmentBlockData {
	
	Integer appointmentBlockId;
	
	String location;
	
	String provider;
	
	Set<String> types;
	
	Date startDate;
	
	Date endDate;
	
	String timeSlotLength;
	
	public AppointmentBlockData() {
	}
	
	public AppointmentBlockData(Integer appointmentBlockId, String location, String provider, Set<String> types,
	    Date startDate, Date endDate, String timeSlotLength) {
		setAppointmentBlockId(appointmentBlockId);
		setLocation(location);
		setProvider(provider);
		setTypes(types);
		setStartDate(startDate);
		setEndDate(endDate);
		setTimeSlotLength(timeSlotLength);
	}
	
	public Integer getAppointmentBlockId() {
		return appointmentBlockId;
	}
	
	public void setAppointmentBlockId(Integer appointmentBlockId) {
		this.appointmentBlockId = appointmentBlockId;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getProvider() {
		return provider;
	}
	
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	public Set<String> getTypes() {
		return types;
	}
	
	public void setTypes(Set<String> types) {
		this.types = types;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getTimeSlotLength() {
		return timeSlotLength;
	}
	
	public void setTimeSlotLength(String timeSlotLength) {
		this.timeSlotLength = timeSlotLength;
	}
	
}
