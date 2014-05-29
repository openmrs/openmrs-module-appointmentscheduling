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
package org.openmrs.module.appointmentscheduling.reporting.query.definition;

import org.openmrs.Location;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.query.BaseQuery;

import java.util.Date;

/**
 * Returns all (non-voided) visits within the specified time frame
 */
@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
public class BasicAppointmentQuery extends BaseQuery<Appointment> implements AppointmentQuery {

    public static final long serialVersionUID = 1L;

    @ConfigurationProperty(required = false)
    private Date onOrAfter;

    @ConfigurationProperty(required = false)
    private Date onOrBefore;

    @ConfigurationProperty(required = false)
    private Location location;

    public Date getOnOrAfter() {
        return onOrAfter;
    }

    public void setOnOrAfter(Date onOrAfter) {
        this.onOrAfter = onOrAfter;
    }

    public Date getOnOrBefore() {
        return onOrBefore;
    }

    public void setOnOrBefore(Date onOrBefore) {
        this.onOrBefore = onOrBefore;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
