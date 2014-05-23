package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentStartDateDataDefinition;

@Handler(supports = AppointmentStartDateDataDefinition.class)
public class AppointmentStartDateDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "timeSlot.startDate";
    }

}
