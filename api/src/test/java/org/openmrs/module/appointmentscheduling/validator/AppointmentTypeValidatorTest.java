package org.openmrs.module.appointmentscheduling.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

public class AppointmentTypeValidatorTest {
	
	AppointmentService appointmentService;
	
	private AppointmentTypeValidator appointmentTypeValidator;
	
	private AppointmentType appointmentType;
	
	private Errors errors;
	
	@Before
	public void setUp() throws Exception {
		appointmentService = mock(AppointmentService.class);
		appointmentType = new AppointmentType("name", "desciption", 10);
		appointmentTypeValidator = new AppointmentTypeValidator();
		appointmentTypeValidator.setAppointmentService(appointmentService);
		errors = mock(Errors.class);
		
	}
	
	@Test
	public void mustRejectAppointmentTypeWithDuplicatedName() throws Exception {
		when(appointmentService.verifyDuplicatedAppointmentTypeName(appointmentType)).thenReturn(true);
		
		appointmentTypeValidator.validate(appointmentType, errors);
		
		Mockito.verify(errors).rejectValue("name", "appointmentscheduling.AppointmentType.nameDuplicated");
	}
	
	@Test
	public void mustAcceptAppointmentTypeWithNotDuplicatedName() throws Exception {
		when(appointmentService.verifyDuplicatedAppointmentTypeName(appointmentType)).thenReturn(false);
		
		appointmentTypeValidator.validate(appointmentType, errors);
		
		Mockito.verify(errors, never()).rejectValue("name", "appointmentscheduling.AppointmentType.nameDuplicated");
	}
}
