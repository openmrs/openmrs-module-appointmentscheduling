package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.reporting.data.AppointmentDataUtil;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PatientToAppointmentDataDefinition;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

@Handler(supports = PatientToAppointmentDataDefinition.class, order=50)
public class PatientToAppointmentDataEvaluator implements AppointmentDataEvaluator {

    @Autowired
    EvaluationService evaluationService;

    @Override
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedAppointmentData evaluatedAppointmentData = new EvaluatedAppointmentData(definition,context);

        // create a map of appointment ids -> patient ids
        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("a.appointmentId", "a.patient.patientId");
        q.from(Appointment.class, "a");

        if (context != null) {
            Set<Integer> appointmentIds = AppointmentDataUtil.getAppointmentIdsForContext(context, true);
            q.whereIn("a.appointmentId", appointmentIds);
        }

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class);

        if (!convertedIds.keySet().isEmpty()) {
            // create a new patient evaluation context using the retrieved ids
            EvaluationContext patientEvaluationContext = new EvaluationContext();
            patientEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));

            // evaluate the joined definition via this patient context
            PatientToAppointmentDataDefinition def = (PatientToAppointmentDataDefinition) definition;
            EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(def.getJoinedDefinition(), patientEvaluationContext);

            // now create the result set by mapping the results in the patient data set to appointment ids
            for (Integer apptId : convertedIds.keySet())  {
                evaluatedAppointmentData.addData(apptId, pd.getData().get(convertedIds.get(apptId)));
            }

        }

        return evaluatedAppointmentData;
    }
}
