package org.openmrs.module.appointment.web;

public class PatientData {
	
	String patientId;
	
	String phoneNumber;
	
	String dateMissedLastAppointment;
	
	public PatientData() {
	}
	
	public PatientData(String phoneNumber, String dateMissed, String patientId) {
		setPhoneNumber(phoneNumber);
		setDateMissedLastAppointment(dateMissed);
		setPatientId(patientId);
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getDateMissedLastAppointment() {
		return dateMissedLastAppointment;
	}
	
	public void setDateMissedLastAppointment(String dateMissed) {
		this.dateMissedLastAppointment = dateMissed;
	}
	
	public String getPatientId() {
		return patientId;
	}
	
	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
	
}
