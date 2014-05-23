package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentReasonDataDefinition;

@Handler(supports = AppointmentReasonDataDefinition.class)
public class AppointmentReasonDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "reason";
    }

}
