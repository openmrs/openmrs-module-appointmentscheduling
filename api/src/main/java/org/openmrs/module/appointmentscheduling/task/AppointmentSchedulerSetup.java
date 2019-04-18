package org.openmrs.module.appointmentscheduling.task;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class AppointmentSchedulerSetup {
    public static void setupMarkAppointmentAsMissedTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName("Mark Appointments Missed");

        if (task != null) {
            task.setStarted(false);
            schedulerService.deleteTask(task.getId());
        }
        task = null;

        if (task == null) {
            task = new TaskDefinition();
            task.setName("Mark Appointments Missed");
            task.setDescription("Mark Appointments Missed");
            task.setTaskClass(MarkAppointmentsAsMissedTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(14400));
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
            }
        } else {
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule mark appointments as missed or completed task", e);
                }
            }
        }
    }

}