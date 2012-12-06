package org.openmrs.module.appointment.web;


public class PatientDescription {
	String phoneNumber;
	String dateMissed;
	
	public PatientDescription(){
	}
	
	public PatientDescription(String phoneNumber, String dateMissed){
		setPhoneNumber(phoneNumber);
		setDateMissed(dateMissed);
	}

	
    public String getPhoneNumber() {
    	return phoneNumber;
    }

	
    public void setPhoneNumber(String phoneNumber) {
    	this.phoneNumber = phoneNumber;
    }

	
    public String getDateMissed() {
    	return dateMissed;
    }

	
    public void setDateMissed(String dateMissed) {
    	this.dateMissed = dateMissed;
    }
	
	
}
