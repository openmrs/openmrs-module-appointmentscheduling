package org.openmrs.module.appointment.web;

public class PatientData {
	
	String phoneNumber;
	
	String dateMissedLastAppointment;
	
	public PatientData() {
	}
	
	public PatientData(String phoneNumber, String dateMissed) {
		setPhoneNumber(phoneNumber);
		setDateMissedLastAppointment(dateMissed);
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
	
}
