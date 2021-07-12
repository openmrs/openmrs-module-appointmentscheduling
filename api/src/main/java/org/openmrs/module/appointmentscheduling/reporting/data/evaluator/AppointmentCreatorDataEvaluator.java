package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentCreatorDataDefinition;

@Handler(supports = AppointmentCreatorDataDefinition.class)
public class AppointmentCreatorDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "creator";
    }

}
