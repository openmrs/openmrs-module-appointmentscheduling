/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.appointment.api.db;

import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.module.appointment.AppointmentBlock;

public interface AppointmentBlockDAO extends SingleClassDAO {
	
	List<AppointmentBlock> getAppointmentBlocks(Date fromDate, Date toDate, Location location);
}
