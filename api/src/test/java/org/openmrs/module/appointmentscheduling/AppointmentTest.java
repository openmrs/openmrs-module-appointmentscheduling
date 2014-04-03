package org.openmrs.module.appointmentscheduling;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class AppointmentTest {

	@Test
	public void shouldReturnAllActiveStatuses() {

		List<Appointment.AppointmentStatus> statuses = Appointment.AppointmentStatus
				.getAppointmentsStatusByType(Appointment.AppointmentStatusType.ACTIVE);
		Assert.assertEquals(3, statuses.size());
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.WAITING));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.INCONSULTATION));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.WALKIN));

	}

	@Test
	public void shouldReturnAllCancelledAndMissedStatuses() {

		List<Appointment.AppointmentStatus> statuses = Appointment.AppointmentStatus
				.getAppointmentsStatusByTypes(Arrays.asList(
						Appointment.AppointmentStatusType.CANCELLED,
						Appointment.AppointmentStatusType.MISSED));
		Assert.assertEquals(3, statuses.size());
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.CANCELLED));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.MISSED));

	}

	@Test
	public void shouldReturnAllNotCancelledStatus() {

		List<Appointment.AppointmentStatus> statuses = Appointment.AppointmentStatus
				.getNotCancelledAppointmentStatuses();
		Assert.assertEquals(7, statuses.size());
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.WAITING));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.INCONSULTATION));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.WALKIN));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.MISSED));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.SCHEDULED));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.RESCHEDULED));
		Assert.assertTrue(statuses
				.contains(Appointment.AppointmentStatus.COMPLETED));

	}
}
