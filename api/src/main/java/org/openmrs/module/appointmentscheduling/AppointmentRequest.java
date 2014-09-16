package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Patient;
import org.openmrs.Provider;

import java.util.Date;

public class AppointmentRequest extends BaseOpenmrsData {

    public enum TimeFrameUnits { DAYS, MONTHS, YEARS }

    public enum AppointmentRequestStatus { PENDING, FULFILLED, CANCELLED }

    private Integer appointmentRequestId;

    private Patient patient;

    private AppointmentType appointmentType;

    private Provider provider;

    private AppointmentRequestStatus status;

    private String notes;

    private Provider requestedBy;

    private Date requestedOn;

    // TODO
    // TODO link to the existing appointment somehow?

    @Override
    public Integer getId() {
        return appointmentRequestId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setId(Integer id) {
        this.appointmentRequestId = id;
    }

    public Integer getAppointmentRequestId() {
        return appointmentRequestId;
    }

    public void setAppointmentRequestId(Integer appointmentRequestId) {
        this.appointmentRequestId = appointmentRequestId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public AppointmentRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentRequestStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Provider getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Provider requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Date getRequestedOn() {
        return requestedOn;
    }

    public void setRequestedOn(Date requestedOn) {
        this.requestedOn = requestedOn;
    }
}
