package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PatientToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PatientToAppointmentDataEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;

    @Before
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void evaluate_shouldReturnPatientDataForEachAppointmentInThePassedContext() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToAppointmentDataDefinition d = new PatientToAppointmentDataDefinition(pidd);
        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(1, 2, 9));     // in our demo set include two appointments for same patient
        EvaluatedAppointmentData ad = appointmentDataService.evaluate(d, context);

        assertThat(ad.getData().size(), is(3));
        assertThat((PatientIdentifier) ad.getData().get(1), is(Context.getPatientService().getPatient(1).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ad.getData().get(2), is(Context.getPatientService().getPatient(2).getPatientIdentifier(pit)));
        assertThat((PatientIdentifier) ad.getData().get(9), is(Context.getPatientService().getPatient(2).getPatientIdentifier(pit)));

    }

    @Test
    public void evaluate_shouldReturnEmptySetIfInputSetEmpty() throws Exception {

        PatientIdentifierType pit = Context.getPatientService().getPatientIdentifierType(2);
        PatientIdentifierDataDefinition pidd = new PatientIdentifierDataDefinition();
        pidd.setIncludeFirstNonNullOnly(true);
        pidd.addType(pit);

        PatientToAppointmentDataDefinition d = new PatientToAppointmentDataDefinition(pidd);
        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet());
        EvaluatedAppointmentData ed = Context.getService(AppointmentDataService.class).evaluate(d, context);

        assertThat(ed.getData().size(), is(0));
    }
}
