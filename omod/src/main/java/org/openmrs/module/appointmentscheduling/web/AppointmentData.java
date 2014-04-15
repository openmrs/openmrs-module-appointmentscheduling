package org.openmrs.module.appointmentscheduling.web;

public class AppointmentData {
	
	PatientData patient;
	
	String appointmentType;
	
	String date;
	
	String startTime;
	
	String endTime;
	
	String reason;
	
	public AppointmentData() {
		
	}
	
	public AppointmentData(PatientData patient, String appointmentType, String date, String startTime, String endTime,
	    String reason) {
		setPatient(patient);
		setAppointmentType(appointmentType);
		setDate(date);
		setStartTime(startTime);
		setEndTime(endTime);
		setReason(reason);
	}
	
	public PatientData getPatient() {
		return patient;
	}
	
	public void setPatient(PatientData patient) {
		this.patient = patient;
	}
	
	public String getAppointmentType() {
		return appointmentType;
	}
	
	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
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
	
	public String getReason() {
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
