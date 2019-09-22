package org.openmrs.module.appointmentscheduling.rest.resource.openmrs1_9;

import org.openmrs.module.appointmentscheduling.AppointmentRequest;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.Listable;
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE
        + "/appointmenttrequeststatus", supportedClass = AppointmentRequest.AppointmentRequestStatus.class,
        supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"})
public class AppointmentRequestStatusResource1_9  implements Listable, Searchable {

    @Override
    public SimpleObject getAll(RequestContext requestContext) throws ResponseException {
        AppointmentRequest.AppointmentRequestStatus[] appointmentRequestStatuses = AppointmentRequest.AppointmentRequestStatus.values();
        SimpleObject simpleObject = new SimpleObject().add("results", appointmentRequestStatuses);
        return simpleObject;
    }

    @Override
    public String getUri(Object o) {
        return RestConstants.URI_PREFIX + "/appointmentscheduling/appointmentrequeststatus/"
                + ((AppointmentRequest.AppointmentRequestStatus) o);
    }

    @Override
    public SimpleObject search(RequestContext requestContext) throws ResponseException {
        return null;
    }

}
