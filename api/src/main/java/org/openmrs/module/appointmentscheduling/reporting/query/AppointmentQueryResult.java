package org.openmrs.module.appointmentscheduling.reporting.query;

import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.module.reporting.evaluation.Evaluated;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

public class AppointmentQueryResult extends AppointmentIdSet implements Evaluated<AppointmentQuery> {

    //***** PROPERTIES *****

    private AppointmentQuery definition;
    private EvaluationContext context;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public AppointmentQueryResult() {
        super();
    }

    /**
     * Full Constructor
     */
    public AppointmentQueryResult(AppointmentQuery definition, EvaluationContext context) {
        this.definition = definition;
        this.context = context;
    }

    //***** PROPERTY ACCESS *****

    /**
     * @return the definition
     */
    public AppointmentQuery getDefinition() {
        return definition;
    }

    /**
     * @param definition the definition to set
     */
    public void setDefinition(AppointmentQuery definition) {
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
