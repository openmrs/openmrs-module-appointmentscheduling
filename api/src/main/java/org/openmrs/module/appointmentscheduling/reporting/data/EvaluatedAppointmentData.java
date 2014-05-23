package org.openmrs.module.appointmentscheduling.reporting.data;

import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public class EvaluatedAppointmentData extends AppointmentData implements Evaluated<AppointmentDataDefinition> {

    //***** PROPERTIES *****

    private AppointmentDataDefinition definition;
    private EvaluationContext context;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public EvaluatedAppointmentData() {
        super();
    }

    /**
     * Full Constructor
     */
    public EvaluatedAppointmentData(AppointmentDataDefinition definition, EvaluationContext context) {
        this.definition = definition;
        this.context = context;
    }

    //***** PROPERTY ACCESS *****

    /**
     * @return the definition
     */
    public AppointmentDataDefinition getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(AppointmentDataDefinition definition) {
        this.definition = definition;
    }

    /**
     * @return the context
     */
    public EvaluationContext getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(EvaluationContext context) {
        this.context = context;
    }
}
