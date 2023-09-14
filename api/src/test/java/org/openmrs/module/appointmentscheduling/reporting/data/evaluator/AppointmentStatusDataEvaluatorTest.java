package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.appointmentscheduling.PatientAppointment;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AppointmentStatusDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;


    @Before
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void shouldFetchStatusFromAppointment() throws Exception {

        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(8));

        EvaluatedAppointmentData result = appointmentDataService.evaluate(new AppointmentStatusDataDefinition(), context);

        assertThat(result.getData().size(), is(1));
        assertThat((PatientAppointment.AppointmentStatus) result.getData().get(8), is(PatientAppointment.AppointmentStatus.CANCELLED));

    }
}

