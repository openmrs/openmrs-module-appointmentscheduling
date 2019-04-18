package org.openmrs.module.appointmentscheduling.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MarkAppointmentsAsMissedTaskTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AppointmentService appointmentService;

    @Before
    public void before() throws Exception {
        executeDataSet("appointmentTestDataset.xml");

    }

    @Test
    public void shouldMarkPastScheduledAppointmentsAsMissed() {

        new MarkAppointmentsAsMissedTask().execute();

        assertThat(appointmentService.getAppointment(1).getStatus(), is(Appointment.AppointmentStatus.MISSED));
        assertThat(appointmentService.getAppointment(2).getStatus(), is(Appointment.AppointmentStatus.MISSED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getAppointment(3).getStatus(), is(Appointment.AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getAppointment(4).getStatus(), is(Appointment.AppointmentStatus.INCONSULTATION));
        assertThat(appointmentService.getAppointment(5).getStatus(), is(Appointment.AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getAppointment(6).getStatus(), is(Appointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
        assertThat(appointmentService.getAppointment(7).getStatus(), is(Appointment.AppointmentStatus.WALKIN));
        assertThat(appointmentService.getAppointment(8).getStatus(), is(Appointment.AppointmentStatus.WAITING));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new MarkAppointmentsAsMissedTask().execute();

        assertThat(appointmentService.getAppointment(9).getStatus(), is(Appointment.AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getAppointment(10).getStatus(), is(Appointment.AppointmentStatus.WAITING));

    }
}