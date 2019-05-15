package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;

import java.sql.Time;
import java.util.Date;
import java.util.Set;

public class ProviderSchedule extends BaseOpenmrsData {

    private static final long serialVersionUID = 124L;

    private Integer providerScheduleId;

    private Date startDate;

    private Date endDate;

    private Time startTime;

    private Time endTime;

    private Provider provider;

    private Location location;

    private Set<AppointmentType> types;

    public ProviderSchedule() {
    }

    public ProviderSchedule(Integer providerScheduleId, Date startDate, Date endDate, Time startTime,
                            Time endTime, Provider provider, Location location, Set<AppointmentType> types) {
        setProviderScheduleId(providerScheduleId);
        setStartDate(startDate);
        setEndDate(endDate);
        setStartTime(startTime);
        setEndTime(endTime);
        setProvider(provider);
        setLocation(location);
        setTypes(types);
    }

    public Integer getProviderScheduleId() {
        return providerScheduleId;
    }

    public void setProviderScheduleId(Integer providerScheduleId) {
        this.providerScheduleId = providerScheduleId;
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
        return getProviderScheduleId();
    }

    @Override
    public void setId(Integer id) {
        setProviderScheduleId(id);
    }
}
