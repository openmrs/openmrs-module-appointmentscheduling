package org.openmrs.module.appointmentscheduling.validator;

import java.util.Arrays;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

public class AppointmentBlockValidatorComponentTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private AppointmentBlockValidator appointmentBlockValidator;
	
	private Errors errors;
	
	@Before
	public void before() throws Exception {
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	public void shouldNotAllowCreationOfOverlappingAppointmentBlock() {
		
		AppointmentBlock appointmentBlock = new AppointmentBlock();
		// this overlaps with appointment block #1 in the test dataset
		appointmentBlock.setStartDate(new DateTime(2005, 1, 1, 0, 0).toDate());
		appointmentBlock.setEndDate(new DateTime(2005, 1, 2, 0, 0).toDate());
		
		appointmentBlock.setProvider(Context.getProviderService().getProvider(1));
		appointmentBlock.setLocation(Context.getLocationService().getLocation(1));
		appointmentBlock.setTypes(new HashSet(Arrays.asList(Context.getService(AppointmentService.class).getAppointmentType(
		    1))));
		
		errors = new BindException(appointmentBlock, "test");
		appointmentBlockValidator.validate(appointmentBlock, errors);
		
		Assert.assertEquals(1, errors.getFieldErrorCount());
		Assert.assertEquals("appointmentscheduling.AppointmentBlock.error.appointmentBlockOverlap",
		    errors.getFieldError("provider").getCode());
	}
	
	@Test
	public void shouldAllowCreationOfNonOverlappingAppointmentBlock() {
		
		AppointmentBlock appointmentBlock = new AppointmentBlock();
		appointmentBlock.setStartDate(new DateTime(2007, 1, 1, 0, 0).toDate());
		appointmentBlock.setEndDate(new DateTime(2007, 1, 2, 0, 0).toDate());
		
		appointmentBlock.setProvider(Context.getProviderService().getProvider(1));
		appointmentBlock.setLocation(Context.getLocationService().getLocation(1));
		appointmentBlock.setTypes(new HashSet(Arrays.asList(Context.getService(AppointmentService.class).getAppointmentType(
		    1))));
		
		errors = new BindException(appointmentBlock, "test");
		appointmentBlockValidator.validate(appointmentBlock, errors);
		
		Assert.assertEquals(0, errors.getFieldErrorCount());
	}
}
