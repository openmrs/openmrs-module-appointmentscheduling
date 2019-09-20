package org.openmrs.module.appointmentscheduling.rest.search;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class EarlyAndLateAppointmentSearchHandler  implements SearchHandler {
    protected final Log log = LogFactory.getLog(this.getClass());


    private final SearchConfig searchConfig = new SearchConfig("appointmentSearch", RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/appointment",
            Arrays.asList("1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*", "2.3.*", "2.4.*"),
            Arrays.asList(new SearchQuery.Builder("Allows retrieval of late and early appointments")
                    .withRequiredParameters("statusValue", "fromDate", "toDate").withOptionalParameters("location", "provider", "appointmentType").build()));

    @Override
    public SearchConfig getSearchConfig() {
        return this.searchConfig;
    }

    @Override
    public PageableResult search(RequestContext context) throws ResponseException {

        String statusValue = context.getRequest().getParameter("statusValue");

        Date fromDate = context.getParameter("fromDate") != null ? (Date) ConversionUtil.convert(
                context.getParameter("fromDate"), Date.class) : null;

        Date toDate = context.getParameter("toDate") != null ? (Date) ConversionUtil.convert(
                context.getParameter("toDate"), Date.class) : null;

        Provider provider = context.getParameter("provider") != null ? Context.getProviderService().getProviderByUuid(
                context.getParameter("provider")) : null;

        Location location = context.getParameter("location") != null ? Context.getLocationService().getLocationByUuid(
                context.getParameter("location")) : null;

        AppointmentType appointmentType = context.getParameter("appointmentType") != null ? Context.getService(
                AppointmentService.class).getAppointmentTypeByUuid(context.getParameter("appointmentType")) : null;

        if (StringUtils.isNotBlank(statusValue)) {
            if (statusValue.equals("LATE")){
                return new NeedsPaging<Appointment>(Context.getService(AppointmentService.class).getLateAppointments(
                        fromDate, toDate, location, provider, appointmentType), context);
            }else if (statusValue.equals("EARLY")){
                return new NeedsPaging<Appointment>(Context.getService(AppointmentService.class).getEarlyAppointments(
                        fromDate, toDate, location, provider, appointmentType), context);
            }else {
                return null;
            }
        }else {
            return null;
        }
    }
}