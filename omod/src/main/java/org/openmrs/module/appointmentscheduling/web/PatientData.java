package org.openmrs.module.appointmentscheduling.web;

import java.util.List;

public class PatientData {
	
	List<String> identifiers;
	
	String phoneNumber;
	
	String dateMissedLastAppointment;
	
	String fullName;
	
	public PatientData() {
	}
	
	public PatientData(String phoneNumber, String dateMissed, List<String> identifiers, String fullName) {
		setPhoneNumber(phoneNumber);
		setDateMissedLastAppointment(dateMissed);
		setIdentifiers(identifiers);
		setFullName(fullName);
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
	
	public List<String> getIdentifiers() {
		return identifiers;
	}
	
	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
