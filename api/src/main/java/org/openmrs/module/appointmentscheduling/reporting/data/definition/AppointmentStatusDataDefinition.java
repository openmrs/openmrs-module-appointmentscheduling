package org.openmrs.module.appointmentscheduling.reporting.data.definition;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy=ConfigurationPropertyCachingStrategy.class)
@Localized("appointmentscheduling.AppointmentStatusDataDefinition")
public class AppointmentStatusDataDefinition extends BaseDataDefinition implements AppointmentDataDefinition {

    public static final long serialVersionUID = 1L;

    /**
     * Default Constructor
     */
    public AppointmentStatusDataDefinition() {
        super();
    }

    /**
     * Constructor to populate name only
     */
    public AppointmentStatusDataDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return Appointment.AppointmentStatus.class;
    }

}
