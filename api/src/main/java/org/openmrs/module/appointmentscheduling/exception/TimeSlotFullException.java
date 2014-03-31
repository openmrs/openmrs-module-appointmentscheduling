package org.openmrs.module.appointmentscheduling.exception;

public class TimeSlotFullException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public TimeSlotFullException() {
		super();
	}
	
	public TimeSlotFullException(String message) {
		super(message);
	}
	
}
