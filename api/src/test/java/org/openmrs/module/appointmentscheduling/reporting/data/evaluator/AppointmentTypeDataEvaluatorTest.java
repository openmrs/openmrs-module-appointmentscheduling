package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentTypeDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;

import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AppointmentTypeDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;


    @BeforeEach
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void shouldFetchTypeFromAppointment() throws Exception {

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(8));

        EvaluatedAppointmentData result = appointmentDataService.evaluate(new AppointmentTypeDataDefinition(), context);

        assertThat(result.getData().size(), is(1));
        assertThat(((AppointmentType) result.getData().get(8)).getId(), is(1));

    }
}
