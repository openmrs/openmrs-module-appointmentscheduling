package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PersonToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PersonToAppointmentDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;

    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void evaluate_shouldReturnPersonDataForEachAppointmentInThePassedContext() throws Exception {
        PersonToAppointmentDataDefinition d = new PersonToAppointmentDataDefinition(new BirthdateDataDefinition());

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(1,2));
        EvaluatedAppointmentData ed = Context.getService(AppointmentDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(2));
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        assertThat(c.convert(ed.getData().get(1)),is("1948-01-01"));
        assertThat(c.convert(ed.getData().get(2)),is("1975-04-08"));

    }

    @Test
    public void evaluate_shouldReturnPersonDataForNonConfidentialAppointments() throws Exception {
        Context.becomeUser("butch");

        PersonToAppointmentDataDefinition d = new PersonToAppointmentDataDefinition(new BirthdateDataDefinition());

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(2, 4));
        EvaluatedAppointmentData ed = Context.getService(AppointmentDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(1));
        BirthdateConverter c = new BirthdateConverter("yyyy-MM-dd");
        assertThat(c.convert(ed.getData().get(4)),is("1948-01-01"));
//        Assert.assertEquals("1975-04-08", c.convert(ed.getData().get(2)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {
        PersonToAppointmentDataDefinition d = new PersonToAppointmentDataDefinition(new BirthdateDataDefinition());

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet());
        EvaluatedAppointmentData ed = Context.getService(AppointmentDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }
}
