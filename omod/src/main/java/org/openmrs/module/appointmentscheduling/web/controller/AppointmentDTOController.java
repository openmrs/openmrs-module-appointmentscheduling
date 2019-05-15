package org.openmrs.module.appointmentscheduling.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentDTO;
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
@RequestMapping("/rest/" + RestConstants.VERSION_1 + AppointmentRestController.APPOINTMENT_SCHEDULING_REST_NAMESPACE + "/saveappointment")
public class AppointmentDTOController {
    protected final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public AppointmentDTO saveAppointment(@RequestBody AppointmentDTO appointmentDTO) throws ParseException {
        AppointmentService service = Context.getService(AppointmentService.class);
        Appointment appointment = new Appointment();
        appointment.setPatient(Context.getPatientService().getPatientByUuid(appointmentDTO.getPatient()));
        appointment.setStatus(Appointment.AppointmentStatus.valueOf(appointmentDTO.getStatus()));
        appointment.setAppointmentType(Context.getService(AppointmentService.class).getAppointmentTypeByUuid(appointmentDTO.getAppointmentType()));

        Date appointmentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(appointmentDTO.getDate());

        TimeSlot timeSlot = service.getRequiredTimeslot(Context.getLocationService().getLocationByUuid(appointmentDTO.getLocation()),
                Context.getProviderService().getProviderByUuid(appointmentDTO.getProvider()), appointment.getAppointmentType(), appointmentDate);
        appointment.setTimeSlot(timeSlot);
        ValidateUtil.validate(appointment);

        AppointmentDTO error = new AppointmentDTO("", "error", "appointmentscheduling.Appointment.error.timeSlotFul", "", "", "");
        try {
             Context.getService(AppointmentService.class).bookAppointment(appointment, false);
            return appointmentDTO;
        } catch (TimeSlotFullException e) {
            Errors errors = new BindException(appointment, "");
            errors.reject("appointmentscheduling.Appointment.error.timeSlotFull");
            return error;
        }


    }

}