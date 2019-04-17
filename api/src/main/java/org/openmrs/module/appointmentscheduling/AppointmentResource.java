package org.openmrs.module.appointmentscheduling;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.APIException;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class AppointmentResource extends BaseOpenmrsData {

    private static final long serialVersionUID = 124L;

    private Integer appointmentResource;

    private Date startDate;

    private Date endDate;

    private Time startTime;

    private Time endTime;

    private Provider provider;

    private Location location;

    private Set<AppointmentType> types;

    private List<BlockExcludedDays> daysList;

    private boolean includeWeekends;

    public AppointmentResource() {
    }

    public AppointmentResource(Integer appointmentResource, Date startDate, Date endDate, Time startTime, Time endTime, Provider provider, Location location, Set<AppointmentType> types, List<BlockExcludedDays> daysList, boolean includeWeekends) {
        setAppointmentResource(appointmentResource);
        setStartDate(startDate);
        setEndDate(endDate);
        setStartTime(startTime);
        setEndTime(endTime);
        setProvider(provider);
        setLocation(location);
        setTypes(types);
        setDaysList(daysList);
        setIncludeWeekends(includeWeekends);
    }

    @Override
    public Integer getId() {
        return getAppointmentResource();
    }

    @Override
    public void setId(Integer id) {
        setAppointmentResource(id);
    }

    public Integer getAppointmentResource() {
        return appointmentResource;
    }

    public void setAppointmentResource(Integer appointmentResource) {
        this.appointmentResource = appointmentResource;
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

    public boolean isIncludeWeekends() {
        return includeWeekends;
    }

    public void setIncludeWeekends(boolean includeWeekends) {
        this.includeWeekends = includeWeekends;
    }

    public List<BlockExcludedDays> getDaysList() {
        if (this.daysList == null) {
            this.daysList = new ArrayList<>();
        }
        return daysList;
    }

    public void setDaysList(List<BlockExcludedDays> daysList) {
        this.daysList = daysList;
    }


    public void addExcludedDays(BlockExcludedDays excludedDays, Integer position) {
        Integer listIndex = findListIndexForGivenPosition(position);
        excludedDays.setAppointmentResource(this);
        this.getDaysList().add(listIndex, excludedDays);
    }

    private Integer findListIndexForGivenPosition(Integer position) {
        Integer size = this.getDaysList().size();
        if (position != null) {
            if (position < 0 && position >= (-1 - size)) {
                position = position + size + 1;
            } else if (position > size) {
                throw new APIException("Cannot add a member which is out of range of the list");
            }
        } else {
            position = size;
        }
        return position;
    }

    public void removeExcludedDays(BlockExcludedDays excludedDays) {
        if (getDaysList().contains(excludedDays)) {
            getDaysList().remove(excludedDays);
            excludedDays.setAppointmentResource(null);
        }
    }

    public void addExcludedDays(BlockExcludedDays excludedDays) {
        this.addExcludedDays(excludedDays, null);
    }

}