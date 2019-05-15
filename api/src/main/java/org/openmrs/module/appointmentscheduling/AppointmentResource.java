package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;

import java.sql.Time;
import java.util.Date;
import java.util.Set;

public class AppointmentResource extends BaseOpenmrsData {

    private static final long serialVersionUID = 124L;

    private Integer appointmentResourceId;

    private Date startDate;

    private Date endDate;

    private Time startTime;

    private Time endTime;

    private Provider provider;

    private Location location;

    private Set<AppointmentType> types;

    public AppointmentResource() {
    }

    public AppointmentResource(Integer appointmentResourceId, Date startDate, Date endDate, Time startTime, Time endTime, Provider provider, Location location, Set<AppointmentType> types) {
        this.appointmentResourceId = appointmentResourceId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.provider = provider;
        this.location = location;
        this.types = types;
    }

    public Integer getAppointmentResourceId() {
        return appointmentResourceId;
    }

    public void setAppointmentResourceId(Integer appointmentResourceId) {
        this.appointmentResourceId = appointmentResourceId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<AppointmentType> getTypes() {
        return types;
    }

    public void setTypes(Set<AppointmentType> types) {
        this.types = types;
    }

    @Override
    public Integer getId() {
        return getAppointmentResourceId();
    }

    @Override
    public void setId(Integer id) {
        setAppointmentResourceId(id);
    }
}
