package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentCancelReasonDataDefinition;

@Handler(supports = AppointmentCancelReasonDataDefinition.class)
public class AppointmentCancelReasonDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "cancelReason";
    }

}
