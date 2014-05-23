package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentLocationDataDefinition;

@Handler(supports = AppointmentLocationDataDefinition.class)
public class AppointmentLocationDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "timeSlot.appointmentBlock.location";
    }

}

