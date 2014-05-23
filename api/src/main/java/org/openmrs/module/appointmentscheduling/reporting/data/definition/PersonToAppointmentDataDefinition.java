package org.openmrs.module.appointmentscheduling.reporting.data.definition;

import org.openmrs.module.reporting.data.JoinDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;

/**
 * Adapter class for exposing a Person Data Definition as an Appointment Data Definition
 */
public class PersonToAppointmentDataDefinition extends JoinDataDefinition<PersonDataDefinition> implements AppointmentDataDefinition {

    /**
     * Default Constructor
     */
    public PersonToAppointmentDataDefinition() {
        super();
    }

    /**
     * Default Constructor
     */
    public PersonToAppointmentDataDefinition(PersonDataDefinition joinedDataDefinition) {
        super(joinedDataDefinition);
    }

    /**
     * Constructor to populate name
     */
    public PersonToAppointmentDataDefinition(String name, PersonDataDefinition joinedDataDefinition) {
        super(name, joinedDataDefinition);
    }

    /**
     * @see JoinDataDefinition#getJoinedDefinitionType()
     */
    @Override
    public Class<PersonDataDefinition> getJoinedDefinitionType() {
        return PersonDataDefinition.class;
    }
}
