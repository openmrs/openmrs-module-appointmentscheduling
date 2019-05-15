package org.openmrs.module.appointmentscheduling.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentRequisition;
import org.openmrs.module.appointmentscheduling.TimeSlot;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.module.appointmentscheduling.exception.TimeSlotFullException;
import org.openmrs.module.appointmentscheduling.rest.controller.AppointmentRestController;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.validator.ValidateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/createappointment")
public class AppointmentRequisitionController {
    protected final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AppointmentRequisition createAppointment(@RequestBody AppointmentRequisition appointmentRequisition) throws ParseException {
        AppointmentService service = Context.getService(AppointmentService.class);
        Appointment appointment = new Appointment();
        appointment.setPatient(Context.getPatientService().getPatientByUuid(appointmentRequisition.getPatient()));
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(appointmentRequisition.getStatus()));
        appointment.setAppointmentType(Context.getService(AppointmentService.class).getAppointmentTypeByUuid(appointmentRequisition.getAppointmentType()));
        appointment.setVisit(Context.getVisitService().getVisitByUuid(appointmentRequisition.getVisit()));

        Date appointmentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(appointmentRequisition.getDate());

        TimeSlot timeSlot = service.getTimeslotForAppointment(Context.getLocationService().getLocationByUuid(appointmentRequisition.getLocation()),
                Context.getProviderService().getProviderByUuid(appointmentRequisition.getProvider()), appointment.getAppointmentType(), appointmentDate);
        if (timeSlot == null) {
            timeSlot = service.createTimeSlotUsingProviderSchedule(appointmentDate, Context.getProviderService().getProviderByUuid(appointmentRequisition.getProvider()),
                    Context.getLocationService().getLocationByUuid(appointmentRequisition.getLocation()));
        }
        appointment.setTimeSlot(timeSlot);
        ValidateUtil.validate(appointment);

        AppointmentRequisition error = new AppointmentRequisition("", "error", "appointmentscheduling.Appointment.error.timeSlotFul", "", "", "", "");
        try {
            Context.getService(AppointmentService.class).bookAppointment(appointment, false);
            return appointmentRequisition;
        } catch (TimeSlotFullException e) {
            Errors errors = new BindException(appointment, "");
            errors.reject("appointmentscheduling.Appointment.error.timeSlotFull");
            return error;
        }


    }

}