package org.openmrs.module.appointmentscheduling.reporting.data.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;

/**
 * Adapter class for exposing a Patient Data Definition as an Appointment Data Definition
 */
public class PatientToAppointmentDataDefinition extends JoinDataDefinition<PatientDataDefinition> implements AppointmentDataDefinition {

    /**
     * Default Constructor
     */
    public PatientToAppointmentDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PatientToAppointmentDataDefinition(PatientDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PatientToAppointmentDataDefinition(String name, PatientDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PatientDataDefinition> getJoinedDefinitionType() {
        return PatientDataDefinition.class;
    }
}