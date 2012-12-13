package org.openmrs.module.appointment.web;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;

public class PatientData {
	
	List<String> identifiers;
	
	String phoneNumber;
	
	String dateMissedLastAppointment;
	
	public PatientData() {
	}
	
	public PatientData(String phoneNumber, String dateMissed, String patientId) {
		setPhoneNumber(phoneNumber);
		setDateMissedLastAppointment(dateMissed);
		setIdentifiers(patientId);
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
	
	public void setIdentifiers(String patientId) {
		Set<PatientIdentifier> identifiers = Context.getPatientService().getPatient(Integer.parseInt(patientId))
		        .getIdentifiers();
		this.identifiers = new LinkedList<String>();
		for (PatientIdentifier identifier : identifiers) {
			//Representation format: <identifier type name> : <identifier value> 
			//for example: "OpenMRS Identification Number: 7532AM-1" 
			String representation = identifier.getIdentifierType().getName() + ": " + identifier.getIdentifier();
			//Put "OpenMRS Identification Number" first.
			if (identifier.getPreferred())
				this.identifiers.add(0, representation);
			//Insert to the end of the list
			else
				this.identifiers.add(this.identifiers.size(), representation);
		}
	}
}
