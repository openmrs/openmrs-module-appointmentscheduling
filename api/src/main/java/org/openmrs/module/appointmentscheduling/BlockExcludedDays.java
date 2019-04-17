package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;

import java.util.Date;

public class BlockExcludedDays extends BaseOpenmrsData {

    private Integer excludedDateId;

    private AppointmentResource appointmentResource;

    private Date excludedDate;

    public BlockExcludedDays() {
    }

    public BlockExcludedDays(AppointmentResource appointmentResource, Date excludedDate) {
        setAppointmentResource(appointmentResource);
        setExcludedDate(excludedDate);
    }

    public AppointmentResource getAppointmentResource() {
        return appointmentResource;
    }

    public void setAppointmentResource(AppointmentResource appointmentResource) {
        this.appointmentResource = appointmentResource;
    }

    public Date getExcludedDate() {
        return excludedDate;
    }

    public void setExcludedDate(Date excludedDate) {
        this.excludedDate = excludedDate;
    }

    public Integer getExcludedDateId() {
        return excludedDateId;
    }

    public void setExcludedDateId(Integer excludedDateId) {
        this.excludedDateId = excludedDateId;
    }

    @Override
    public Integer getId() {
        return getExcludedDateId();
    }

    @Override
    public void setId(Integer id) {
        setExcludedDateId(id);
    }


}