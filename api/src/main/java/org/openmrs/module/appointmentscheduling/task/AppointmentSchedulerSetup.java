package org.openmrs.module.appointmentscheduling.task;

import org.apache.commons.lang.time.DateUtils;
import org.openmrs.api.context.Context;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;

import java.util.Date;

public class AppointmentSchedulerSetup {
    public static void setupCleanOpenAppointmentsTask() {

        SchedulerService schedulerService = Context.getSchedulerService();

        TaskDefinition task = schedulerService.getTaskByName("Clean Open Appointments Task");

        if (task != null) {
            task.setStarted(false);
            schedulerService.deleteTask(task.getId());
        }
        task = null;

        if (task == null) {
            task = new TaskDefinition();
            task.setName("Clean Open Appointments Task");
            task.setDescription("Clean Open Appointments Task");
            task.setTaskClass(CleanOpenAppointmentsTask.class.getName());
            task.setStartTime(DateUtils.addMinutes(new Date(), 5));
            task.setRepeatInterval(new Long(14400));
            task.setStartOnStartup(true);
            try {
                schedulerService.scheduleTask(task);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule Clean Open Appointments Task", e);
            }
        } else {
            if (!task.getStarted()) {
                task.setStarted(true);
                try {
                    schedulerService.scheduleTask(task);
                } catch (SchedulerException e) {
                    throw new RuntimeException("Failed to schedule Clean Open Appointments Task", e);
                }
            }
        }
    }

}