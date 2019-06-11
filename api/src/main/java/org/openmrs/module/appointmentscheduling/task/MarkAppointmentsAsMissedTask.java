package org.openmrs.module.appointmentscheduling.task;

import org.joda.time.DateTime;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.scheduler.tasks.AbstractTask;

import java.util.Arrays;
import java.util.Date;

public class MarkAppointmentsAsMissedTask extends AbstractTask {

    @Override
    public void execute() {

        AppointmentService appointmentService = Context.getService(AppointmentService.class);

        Date endOfYesterday = new DateTime().withTime(23, 59, 59, 999).minusDays(1).toDate();

        for (Appointment appointment : appointmentService.getAppointmentsByConstraints(null, endOfYesterday, null, null, null, null,
                Appointment.AppointmentStatus.getAppointmentsStatusByTypes(Arrays.asList(Appointment.AppointmentStatusType.SCHEDULED)))) {
            appointmentService.changeAppointmentStatus(appointment, Appointment.AppointmentStatus.MISSED);
        }
    }
}