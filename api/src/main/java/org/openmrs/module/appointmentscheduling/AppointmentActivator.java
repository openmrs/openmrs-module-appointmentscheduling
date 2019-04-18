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
package org.openmrs.module.appointmentscheduling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.appointmentscheduling.task.AppointmentSchedulerSetup;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class AppointmentActivator implements ModuleActivator {
	
	protected Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see ModuleActivator#willRefreshContext()
	 */
	public void willRefreshContext() {
		log.info("Refreshing Appointment Module");
	}
	
	/**
	 * @see ModuleActivator#contextRefreshed()
	 */
	public void contextRefreshed() {
		log.info("Appointment Module refreshed");
	}
	
	/**
	 * @see ModuleActivator#willStart()
	 */
	public void willStart() {
		log.info("Starting Appointment Module");
	}
	
	/**
	 * @see ModuleActivator#started()
	 */
	public void started() {
		log.info("Appointment Module started");

		Boolean markAppointmentAsMissedTask = Boolean.parseBoolean(Context.getAdministrationService().getGlobalProperty(
				AppointmentUtils.GP_MARK_APPOINTMENT_AS_MISSED));
		if(markAppointmentAsMissedTask == true) {
			AppointmentSchedulerSetup.setupMarkAppointmentAsMissedTask();
		}
	}
	
	/**
	 * @see ModuleActivator#willStop()
	 */
	public void willStop() {
		log.info("Stopping Appointment Module");
	}
	
	/**
	 * @see ModuleActivator#stopped()
	 */
	public void stopped() {
		log.info("Appointment Module stopped");
	}
	
}
