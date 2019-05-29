package org.openmrs.module.appointmentscheduling.web.controller;

import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentDailyCount;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE +"/dailyappointmentcount")
public class AppointmentDailyCountController {

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<AppointmentDailyCount> getDailyAggregates(
            @RequestParam(value = "fromDate", required = true) String fromDate,
            @RequestParam(value = "toDate", required = true) String toDate,
            @RequestParam(value = "location", required = false) Location location,
            @RequestParam(value = "provider", required = false) Provider provider,
            @RequestParam(value = "status", required = false) Appointment.AppointmentStatus status
    )
    {
        List<AppointmentDailyCount> dailyCounts = Context.getService(AppointmentService.class)
                .getAppointmentDailyCount(fromDate, toDate, location, provider, status);
        return dailyCounts;
    }

}