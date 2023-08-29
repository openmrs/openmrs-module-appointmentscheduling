package org.openmrs.module.appointmentscheduling.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.AppointmentData;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CleanOpenAppointmentsTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AppointmentService appointmentService;

    @Before
    public void before() throws Exception {
        executeDataSet("appointmentTestDataset.xml");

    }

    @Test
    public void shouldUpdatePastAppointmentStatusCorrectly() {

        new CleanOpenAppointmentsTask().execute();

        //should mark Scheduled, Waiting and Walkin as missed
        assertThat(appointmentService.getAppointmentData(1).getStatus(), is(AppointmentData.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentData(2).getStatus(), is(AppointmentData.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentData(7).getStatus(), is(AppointmentData.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentData(8).getStatus(), is(AppointmentData.AppointmentStatus.MISSED));


        //should mark In-consultation as completed
        assertThat(appointmentService.getAppointmentData(4).getStatus(), is(AppointmentData.AppointmentStatus.COMPLETED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getAppointmentData(3).getStatus(), is(AppointmentData.AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointmentData(5).getStatus(), is(AppointmentData.AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointmentData(6).getStatus(), is(AppointmentData.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new CleanOpenAppointmentsTask().execute();

        assertThat(appointmentService.getAppointmentData(9).getStatus(), is(AppointmentData.AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointmentData(10).getStatus(), is(AppointmentData.AppointmentStatus.WAITING));

    }
}