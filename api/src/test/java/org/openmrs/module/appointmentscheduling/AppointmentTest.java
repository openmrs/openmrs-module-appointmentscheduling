package org.openmrs.module.appointmentscheduling;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatusType;

public class AppointmentTest {

	@Test
	public void shouldReturnAllActiveStatuses() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getAppointmentsStatusByType(AppointmentStatusType.ACTIVE);
		Assert.assertEquals(3, statuses.size());
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.WAITING));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.INCONSULTATION));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.WALKIN));

	}

	@Test
	public void shouldReturnAllCancelledAndMissedStatuses() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getAppointmentsStatusByTypes(Arrays.asList(
						AppointmentStatusType.CANCELLED,
						AppointmentStatusType.MISSED));
		Assert.assertEquals(3, statuses.size());
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.CANCELLED));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.MISSED));

	}

	@Test
	public void shouldReturnAllNotCancelledStatus() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getNotCancelledAppointmentStatuses();
		Assert.assertEquals(7, statuses.size());
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.WAITING));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.INCONSULTATION));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.WALKIN));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.MISSED));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.SCHEDULED));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.RESCHEDULED));
		Assert.assertTrue(statuses
				.contains(AppointmentStatus.COMPLETED));

	}
}
