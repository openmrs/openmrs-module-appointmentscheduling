package org.openmrs.module.appointmentscheduling.validator;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentBlock;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentBlockValidatorComponentTest extends BaseModuleContextSensitiveTest {
	
	@Autowired
	private AppointmentBlockValidator appointmentBlockValidator;
	
	private Errors errors;
	
	@BeforeEach
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
		
		assertThat(errors.getFieldErrorCount(),is(1));
		assertThat(errors.getFieldError("provider").getCode(), is("appointmentscheduling.AppointmentBlock.error.appointmentBlockOverlap"));
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
		
		assertThat(errors.getFieldErrorCount(), is(0));
	}

    @Test
    public void shouldNotAllowEditingAnAppointmentBlockToRemoveAnAppointmentTypeIfThatTypeAlreadyScheduled() {

        AppointmentBlock appointmentBlock = Context.getService(AppointmentService.class).getAppointmentBlock(1);

        // appointment block #1 already has a appt of type #1
        appointmentBlock.getTypes().remove(Context.getService(AppointmentService.class).getAppointmentType(1));

        errors = new BindException(appointmentBlock, "test");
        appointmentBlockValidator.validate(appointmentBlock, errors);

        assertThat(errors.getFieldErrorCount(),is(2));
        assertThat(errors.getFieldError("types").getCode(),is("appointmentscheduling.AppointmentBlock.error.cannotRemoveTypeFromBlockIfAppointmentScheduled"));

    }


    @Test
    public void shouldAllowEditingAnAppointmentBlockToRemoveAnAppointmentTypeIfThatTypeIsNotAlreadyScheduled() {

        AppointmentBlock appointmentBlock = Context.getService(AppointmentService.class).getAppointmentBlock(4);

        // appointment block #4 does not have a appt of type #2
        appointmentBlock.getTypes().remove(Context.getService(AppointmentService.class).getAppointmentType(2));

        errors = new BindException(appointmentBlock, "test");
        appointmentBlockValidator.validate(appointmentBlock, errors);

        assertThat(errors.getFieldErrorCount(),is(0));
    }

    @Test
    public void shouldAllowEditingAnAppointmentBlockToRemoveAnAppointmentTypeIfAppointmentsOfThatTypeHaveBeenCancelled() {

        // first the appointment associated with this block
        Appointment appointment = Context.getService(AppointmentService.class).getAppointment(12);
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED_AND_NEEDS_RESCHEDULE);
        Context.getService(AppointmentService.class).saveAppointment(appointment);
        Context.flushSession();

        AppointmentBlock appointmentBlock = Context.getService(AppointmentService.class).getAppointmentBlock(5);
        appointmentBlock.getTypes().remove(Context.getService(AppointmentService.class).getAppointmentType(1));

        errors = new BindException(appointmentBlock, "test");
        appointmentBlockValidator.validate(appointmentBlock, errors);

        // should be no errors after cancelling
        assertThat(errors.getFieldErrorCount(),is(0));
    }

}
