package org.openmrs.module.appointmentscheduling.web;

import java.util.Date;
import java.util.Set;

public class AppointmentBlockData {
	
	Integer appointmentBlockId;
	
	String location;
	
	String provider;
	
	Set<String> types;
	
	String date;
	
	String startTime;
	
	String endTime;
	
	String timeSlotLength;
	
	Date startDate;
	
	Date endDate;
	
	public AppointmentBlockData() {
	}
	
	public AppointmentBlockData(Integer appointmentBlockId, String location, String provider, Set<String> types,
	    String date, String startTime, String endTime, String timeSlotLength, Date startDate, Date endDate) {
		setAppointmentBlockId(appointmentBlockId);
		setLocation(location);
		setProvider(provider);
		setTypes(types);
		setDate(date);
		setStartTime(startTime);
		setEndTime(endTime);
		setTimeSlotLength(timeSlotLength);
		setStartDate(startDate);
		setEndDate(endDate);
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
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getTimeSlotLength() {
		return timeSlotLength;
	}
	
	public void setTimeSlotLength(String timeSlotLength) {
		this.timeSlotLength = timeSlotLength;
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
	
}
