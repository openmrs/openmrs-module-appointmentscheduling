package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentStatusDataDefinition;

@Handler(supports = AppointmentStatusDataDefinition.class)
public class AppointmentStatusDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "status";
    }

}

