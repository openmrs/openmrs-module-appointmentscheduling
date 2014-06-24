package org.openmrs.module.appointmentscheduling.reporting.query.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.reporting.data.AppointmentDataUtil;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentQueryResult;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.BasicAppointmentQuery;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Handler(supports = BasicAppointmentQuery.class)
public class BasicAppointmentQueryEvaluator implements AppointmentQueryEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    @Override
    public AppointmentQueryResult evaluate(AppointmentQuery appointmentQuery, EvaluationContext evaluationContext) throws EvaluationException {

        BasicAppointmentQuery q  = (BasicAppointmentQuery) appointmentQuery;
        HqlQueryBuilder query = new HqlQueryBuilder();

        // query builder excludes voided by default
        query.select("appointment.appointmentId").from(Appointment.class, "appointment")
                .whereLessOrEqualTo("appointment.timeSlot.startDate", q.getOnOrBefore())
                .whereGreaterOrEqualTo("appointment.timeSlot.endDate", q.getOnOrAfter())
                .whereEqual("appointment.timeSlot.appointmentBlock.location", q.getLocation());


        if (evaluationContext != null) {
            Set<Integer> appointmentIds = AppointmentDataUtil.getAppointmentIdsForContext(evaluationContext, true);
            query.whereIn("appointment.appointmentId", appointmentIds);
        }

        AppointmentQueryResult result = new AppointmentQueryResult(appointmentQuery, evaluationContext);
        List<Integer> results = evaluationService.evaluateToList(query, Integer.class, evaluationContext);
        result.add(results.toArray(new Integer[results.size()]));
        return result;

    }
}
