package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentReasonDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentReasonDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;


    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void shouldFetchReasonFromAppointment() throws Exception {

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(2));

        EvaluatedAppointmentData result = appointmentDataService.evaluate(new AppointmentReasonDataDefinition(), context);

        assertThat(result.getData().size(), is(1));
        assertThat((String) result.getData().get(2), is("scheduled checkup"));

    }
}
