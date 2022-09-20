package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Provider;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentProviderDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentProviderDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;


    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void shouldFetchProviderFromAppointment() throws Exception {

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(8));

        EvaluatedAppointmentData result = appointmentDataService.evaluate(new AppointmentProviderDataDefinition(), context);

        assertThat(result.getData().size(), is(1));
        assertThat(((Provider) result.getData().get(8)).getId(), is(1));

    }
}

