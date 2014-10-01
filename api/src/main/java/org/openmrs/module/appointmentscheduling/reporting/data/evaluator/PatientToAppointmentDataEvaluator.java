package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentSchedulingConstants;
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

import java.util.Iterator;
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

        Map<Integer, Integer> convertedIds = evaluationService.evaluateToMap(q, Integer.class, Integer.class, context);

        if (!Context.hasPrivilege(AppointmentSchedulingConstants.PRIVILEGE_VIEW_CONFIDENTIAL_APPOINTMENT_DETAILS)) {
            // build a map of appointment ids to whether it is confidential
            HqlQueryBuilder confidentialQuery = new HqlQueryBuilder();
            confidentialQuery.select("a.appointmentId", "case a.appointmentType.confidential when 0 then false else true end");
            confidentialQuery.from(Appointment.class, "a");

            if (context != null) {
                Set<Integer> appointmentIds = AppointmentDataUtil.getAppointmentIdsForContext(context, true);
                confidentialQuery.whereIn("a.appointmentId", appointmentIds);
            }

            // remove confidential ones
            Map<Integer, Boolean> confidentialMap = evaluationService.evaluateToMap(confidentialQuery, Integer.class, Boolean.class, context);
            for (Iterator<Map.Entry<Integer, Integer>> iterator = convertedIds.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Integer, Integer> entry = iterator.next();
                Integer appointmentId = entry.getKey();
                if (confidentialMap.get(appointmentId)) {
                    iterator.remove();
                }
            }
        }

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
