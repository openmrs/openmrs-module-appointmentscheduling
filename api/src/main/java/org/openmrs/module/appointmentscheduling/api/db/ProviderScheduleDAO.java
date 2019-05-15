package org.openmrs.module.appointmentscheduling.api.db;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.appointmentscheduling.ProviderSchedule;

import java.util.Date;
import java.util.List;

public interface ProviderScheduleDAO extends SingleClassDAO {

    List<ProviderSchedule> getProviderScheduleByConstraints(Location location, Provider provider, Date appointmentDate) throws DAOException;

}