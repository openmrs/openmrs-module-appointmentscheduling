package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentEndDateDataDefinition;

@Handler(supports = AppointmentEndDateDataDefinition.class)
public class AppointmentEndDateDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "timeSlot.endDate";
    }

}
