package org.openmrs.module.appointmentscheduling;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;
import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatusType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.is;

public class AppointmentTest {

	@Test
	public void shouldReturnAllActiveStatuses() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getAppointmentsStatusByType(AppointmentStatusType.ACTIVE);
		assertThat(statuses.size(),is(3));
		assertTrue(statuses
				.contains(AppointmentStatus.WAITING));
		assertTrue(statuses
				.contains(AppointmentStatus.INCONSULTATION));
		assertTrue(statuses
				.contains(AppointmentStatus.WALKIN));

	}

	@Test
	public void shouldReturnAllCancelledAndMissedStatuses() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getAppointmentsStatusByTypes(Arrays.asList(
						AppointmentStatusType.CANCELLED,
						AppointmentStatusType.MISSED));
		assertThat(statuses.size(),is(3));
		assertTrue(statuses
				.contains(AppointmentStatus.CANCELLED));
		assertTrue(statuses
				.contains(AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE));
		assertTrue(statuses
				.contains(AppointmentStatus.MISSED));

	}

	@Test
	public void shouldReturnAllNotCancelledStatus() {

		List<AppointmentStatus> statuses = AppointmentStatus
				.getNotCancelledAppointmentStatuses();
		assertThat(statuses.size(),is(7));
		assertTrue(statuses
				.contains(AppointmentStatus.WAITING));
		assertTrue(statuses
				.contains(AppointmentStatus.INCONSULTATION));
		assertTrue(statuses
				.contains(AppointmentStatus.WALKIN));
		assertTrue(statuses
				.contains(AppointmentStatus.MISSED));
		assertTrue(statuses
				.contains(AppointmentStatus.SCHEDULED));
		assertTrue(statuses
				.contains(AppointmentStatus.RESCHEDULED));
		assertTrue(statuses
				.contains(AppointmentStatus.COMPLETED));

	}
}
