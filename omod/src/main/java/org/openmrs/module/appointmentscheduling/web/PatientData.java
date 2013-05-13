package org.openmrs.module.appointmentscheduling.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;

public class PatientData {
	
	List<String> identifiers;
	
	String phoneNumber;
	
	String dateMissedLastAppointment;
	
	public PatientData() {
	}
	
	public PatientData(String phoneNumber, String dateMissed, List<String> identifiers) {
		setPhoneNumber(phoneNumber);
		setDateMissedLastAppointment(dateMissed);
		setIdentifiers(identifiers);
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
}
