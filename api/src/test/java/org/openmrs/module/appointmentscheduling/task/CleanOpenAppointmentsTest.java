package org.openmrs.module.appointmentscheduling.task;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.PatientAppointment;
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
        assertThat(appointmentService.getPatientAppointment(1).getStatus(), is(PatientAppointment.AppointmentStatus.MISSED));
        assertThat(appointmentService.getPatientAppointment(2).getStatus(), is(PatientAppointment.AppointmentStatus.MISSED));
        assertThat(appointmentService.getPatientAppointment(7).getStatus(), is(PatientAppointment.AppointmentStatus.MISSED));
        assertThat(appointmentService.getPatientAppointment(8).getStatus(), is(PatientAppointment.AppointmentStatus.MISSED));


        //should mark In-consultation as completed
        assertThat(appointmentService.getPatientAppointment(4).getStatus(), is(PatientAppointment.AppointmentStatus.COMPLETED));

        // status of other appointments should not be changed
        assertThat(appointmentService.getPatientAppointment(3).getStatus(), is(PatientAppointment.AppointmentStatus.COMPLETED));
        assertThat(appointmentService.getPatientAppointment(5).getStatus(), is(PatientAppointment.AppointmentStatus.CANCELLED));
        assertThat(appointmentService.getPatientAppointment(6).getStatus(), is(PatientAppointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));

    }

    @Test
    public void shouldNotMarkFutureAppointmentsAsMissedOrCompleted() {

        new CleanOpenAppointmentsTask().execute();

        assertThat(appointmentService.getPatientAppointment(9).getStatus(), is(PatientAppointment.AppointmentStatus.SCHEDULED));
        assertThat(appointmentService.getPatientAppointment(10).getStatus(), is(PatientAppointment.AppointmentStatus.WAITING));

    }
}