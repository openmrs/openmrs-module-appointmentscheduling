package org.openmrs.module.appointment.web;

public class AppointmentBlockDetails {
	
	String location;
	
	String providor;
	
	String types;
	
	String startDate;
	
	String endDate;
	
	public AppointmentBlockDetails() {
	}
	
	public AppointmentBlockDetails(String location, String providor, String types, String startDate, String endDate) {
		setLocation(location);
		setProvidor(providor);
		setTypes(types);
		setStartDate(startDate);
		setEndDate(endDate);
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getProvidor() {
		return providor;
	}
	
	public void setProvidor(String providor) {
		this.providor = providor;
	}
	
	public String getTypes() {
		return types;
	}
	
	public void setTypes(String types) {
		this.types = types;
	}
	
	public String getStartDate() {
		return startDate;
	}
	
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
