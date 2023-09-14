package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.module.appointmentscheduling.AppointmentDetail;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointmentallowingoverbook", supportedClass = AppointmentDetail.class,
            supportedOpenmrsVersions = {"1.9.* - 9.*"})
public class AppointmentAllowingOverbookResource1_9 extends AppointmentResource1_9 {

	@Override
	public AppointmentDetail save(AppointmentDetail appointment) {
		return save(appointment, true);
	}

}
