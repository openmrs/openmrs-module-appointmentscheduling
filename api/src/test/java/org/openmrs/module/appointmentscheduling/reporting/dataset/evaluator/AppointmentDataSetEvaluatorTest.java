package org.openmrs.module.appointmentscheduling.reporting.dataset.evaluator;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentEndDateDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentReasonDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentStartDateDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentStatusDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetUtil;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class AppointmentDataSetEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    AppointmentDataService appointmentDataService;


    @Before
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    public void evaluate_shouldEvaluateDataSetDefinition() throws Exception {

        EvaluationContext context = new EvaluationContext();

        AppointmentDataSetDefinition d = new AppointmentDataSetDefinition();

        d.addColumn("Appointment Start Date", new AppointmentStartDateDataDefinition(), null);
        d.addColumn("Appointment End Date", new AppointmentEndDateDataDefinition(), null);
        d.addColumn("Appointment Status", new AppointmentStatusDataDefinition(), null);
        d.addColumn("Appointment Reason", new AppointmentReasonDataDefinition(), null);

        DataSet dataset = Context.getService(DataSetDefinitionService.class).evaluate(d, context);

        DataSetUtil.printDataSet(dataset, System.out);
    }

}
