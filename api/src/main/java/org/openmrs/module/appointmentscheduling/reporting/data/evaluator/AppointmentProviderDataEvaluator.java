package org.openmrs.module.appointmentscheduling.reporting.data.evaluator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentProviderDataDefinition;

@Handler(supports = AppointmentProviderDataDefinition.class)
public class AppointmentProviderDataEvaluator extends AppointmentPropertyDataEvaluator {

    @Override
    public String getPropertyName() {
        return "timeSlot.appointmentBlock.provider";
    }

}
