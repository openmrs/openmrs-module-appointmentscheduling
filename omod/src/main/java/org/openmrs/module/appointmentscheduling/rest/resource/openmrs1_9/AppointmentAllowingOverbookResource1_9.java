package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentallowingoverbook", supportedClass = Appointment.class,
            supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentAllowingOverbookResource1_9 extends AppointmentResource1_9 {

	@Override
	public Appointment save(Appointment appointment) {
		return save(appointment, true);
	}

}
