package org.openmrs.module.appointmentscheduling.api.db;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.AppointmentResource;

import java.util.Date;
import java.util.List;

public interface AppointmentResourceDAO extends SingleClassDAO {

    List<AppointmentResource> getResourceByConstraints(Location location, Provider provider, Date appointmentDate) throws DAOException;

}