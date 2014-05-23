package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.reporting.definition.evaluator.DefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

/**
 * Each implementation of this class is expected to evaluate one or more type of AppointmentDataDefinition to produce a AppointmentData result
 */
public interface AppointmentDataEvaluator extends DefinitionEvaluator<AppointmentDataDefinition> {

    /**
     * Evaluate an AppointmentDataDefinition for the given EvaluationContext
     */
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException;


}
