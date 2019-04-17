package org.openmrs.module.appointmentscheduling.api.db;

import org.openmrs.Location;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.AppointmentResource;
import org.openmrs.module.appointmentscheduling.BlockExcludedDays;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public interface AppointmentResourceDAO extends SingleClassDAO {

    List<AppointmentResource> getResourceByConstraints(Date startDate, Date endDate, Time startTime, Time endTime, Location location, Date appointmentDate) throws DAOException;

    List<String> getResourceExcludedDays(Date appointmentDate, Location location) throws DAOException;

    BlockExcludedDays geyExcludedDayByUuid(String uuid) throws  DAOException;

}