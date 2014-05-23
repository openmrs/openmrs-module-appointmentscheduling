package org.openmrs.module.appointmentscheduling.reporting.context;

import org.openmrs.OpenmrsData;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.query.IdSet;

import java.util.Date;
import java.util.Map;

/**
 * Extends the patient-based EvaluationContext to add an additional Appointment filter for use within Appointment specific queries and data extraction
 * Note that this cache is cleared whenever any changes are made to baseAppointments
 */
public class AppointmentEvaluationContext extends EvaluationContext {

    // ***** PROPERTIES *****

    private AppointmentIdSet baseAppointments;

    // ***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public AppointmentEvaluationContext() {
        super();
    }

    /**
     * Constructor which sets the Evaluation Date to a particular date
     */
    public AppointmentEvaluationContext(Date evaluationDate) {
        super(evaluationDate);
    }

    /**
     * Constructs a new AppointmentEvaluationContext given the passed EvaluationContext and AppointmentIdSet
     */
    public AppointmentEvaluationContext(EvaluationContext context, AppointmentIdSet baseAppointments) {
        super(context);
        this.baseAppointments = baseAppointments;
    }

    /**
     * Constructs a new EvaluationContext given the passed EvaluationContext
     */
    public AppointmentEvaluationContext(AppointmentEvaluationContext context) {
        super(context);
        this.baseAppointments = context.baseAppointments;
    }

    // *******************
    // INSTANCE METHODS
    // *******************

    @Override
    public Map<Class<? extends OpenmrsData>, IdSet<?>> getAllBaseIdSets() {
        Map<Class<? extends OpenmrsData>, IdSet<?>> ret = super.getAllBaseIdSets();
        if (getBaseAppointments() != null) {
            ret.put(Appointment.class, getBaseAppointments());
        }
        return ret;
    }

    /**
     * @return a shallow copy of the current instance
     */
    @Override
    public AppointmentEvaluationContext shallowCopy() {
        return new AppointmentEvaluationContext(this);
    }

    /**
     * @return the baseAppointments
     */
    public AppointmentIdSet getBaseAppointments() {
        return baseAppointments;
    }

    /**
     * @param baseAppointments the baseAppointments to set
     */
    public void setBaseAppointments(AppointmentIdSet baseAppointments) {
        clearCache();
        this.baseAppointments = baseAppointments;
    }

}
