package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.reporting.data.AppointmentDataUtil;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 * Evaluates a property on an Appointment to produce a AppointmentData
 */
public abstract class AppointmentPropertyDataEvaluator implements AppointmentDataEvaluator {

    @Autowired
    private EvaluationService evaluationService;

    public abstract String getPropertyName();

    @Override
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException {
        EvaluatedAppointmentData appointmentData = new EvaluatedAppointmentData(definition, context);
        HqlQueryBuilder q = new HqlQueryBuilder();
        q.select("a.appointmentId", "a."+getPropertyName());
        q.from(Appointment.class, "a");

        if (context != null) {
            Set<Integer> appointmentIds = AppointmentDataUtil.getAppointmentIdsForContext(context, true);
            q.whereIn("a.appointmentId", appointmentIds);
        }

        Map<Integer, Object> data = evaluationService.evaluateToMap(q, Integer.class, Object.class, context);
        appointmentData.setData(data);
        return appointmentData;
    }

}
