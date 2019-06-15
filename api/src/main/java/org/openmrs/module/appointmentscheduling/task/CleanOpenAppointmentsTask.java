package org.openmrs.module.appointmentscheduling.task;

import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.scheduler.tasks.AbstractTask;

public class CleanOpenAppointmentsTask extends AbstractTask {

    @Override
    public void execute() {

        AppointmentService appointmentService = Context.getService(AppointmentService.class);
        appointmentService.cleanOpenAppointments();
    }
}