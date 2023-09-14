package org.openmrs.module.appointmentscheduling.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.AppointmentDetail;
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
        assertThat(appointmentService.getAppointmentDetail(1).getStatus(), is(AppointmentDetail.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentDetail(2).getStatus(), is(AppointmentDetail.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentDetail(7).getStatus(), is(AppointmentDetail.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointmentDetail(8).getStatus(), is(AppointmentDetail.AppointmentStatus.MISSED));


        //should mark In-consultation as completed
        assertThat(appointmentService.getAppointmentDetail(4).getStatus(), is(AppointmentDetail.AppointmentStatus.COMPLETED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getAppointmentDetail(3).getStatus(), is(AppointmentDetail.AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointmentDetail(5).getStatus(), is(AppointmentDetail.AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointmentDetail(6).getStatus(), is(AppointmentDetail.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new CleanOpenAppointmentsTask().execute();

        assertThat(appointmentService.getAppointmentDetail(9).getStatus(), is(AppointmentDetail.AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointmentDetail(10).getStatus(), is(AppointmentDetail.AppointmentStatus.WAITING));

    }
}