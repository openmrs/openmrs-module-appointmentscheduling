package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.AppointmentSchedulingConstants;
import org.openmrs.module.appointmentscheduling.reporting.data.AppointmentDataUtil;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PersonToAppointmentDataDefinition;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.context.PersonEvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.openmrs.module.reporting.query.person.PersonIdSet;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Handler(supports = PersonToAppointmentDataDefinition.class, order=50)
public class PersonToAppointmentDataEvaluator implements AppointmentDataEvaluator {

    @Autowired
    EvaluationService evaluationService;

    @Override
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException {

        EvaluatedAppointmentData evaluatedAppointmentData = new EvaluatedAppointmentData(definition,context);

        // create a map of appointment ids -> patient (person) ids
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
            // create a new Person evaluation context using the retrieved ids
            PersonEvaluationContext personEvaluationContext = new PersonEvaluationContext();
            personEvaluationContext.setBaseCohort(new Cohort(convertedIds.values()));
            personEvaluationContext.setBasePersons(new PersonIdSet(new HashSet<Integer>(convertedIds.values())));

            // evaluate the joined definition via this person context
            PersonToAppointmentDataDefinition def = (PersonToAppointmentDataDefinition) definition;
            EvaluatedPersonData pd = Context.getService(PersonDataService.class).evaluate(def.getJoinedDefinition(), personEvaluationContext);

            // now create the result set by mapping the results in the person data set to appointment ids
            for (Integer apptId : convertedIds.keySet())  {
                evaluatedAppointmentData.addData(apptId, pd.getData().get(convertedIds.get(apptId)));
            }

        }

        return evaluatedAppointmentData;
    }
}
