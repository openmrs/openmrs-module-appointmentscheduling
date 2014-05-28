package org.openmrs.module.appointmentscheduling.reporting.dataset.definition;

import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PatientToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.PersonToAppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.DataDefinition;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.RowPerObjectDataSetDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.ParameterizableUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * DataSetDefinition for Producing a DataSet that has one row per Appointment
 * @see org.openmrs.module.reporting.dataset.definition.DataSetDefinition
 */
@Localized("appointmentscheduling.AppointmentDataSetDefinition")
public class AppointmentDataSetDefinition extends RowPerObjectDataSetDefinition {

    public static final long serialVersionUID = 1L;

    //***** PROPERTIES *****

    @ConfigurationProperty
    private List<Mapped<? extends AppointmentQuery>> rowFilters;

    //***** CONSTRUCTORS *****

    /**
     * Default Constructor
     */
    public AppointmentDataSetDefinition() {
        super();
    }

    /**
     * Public constructor
     */
    public AppointmentDataSetDefinition(String name) {
        super(name);
    }

    //***** INSTANCE METHODS *****

    /**
     * @see RowPerObjectDataSetDefinition#getSupportedDataDefinitionTypes()
     */
    @Override
    public List<Class<? extends DataDefinition>> getSupportedDataDefinitionTypes() {
        List<Class<? extends DataDefinition>> l = new ArrayList<Class<? extends DataDefinition>>();
        l.add(AppointmentDataDefinition.class);
        l.add(PatientDataDefinition.class);
        l.add(PersonDataDefinition.class);
        return l;
    }

    /**
     * Adds a new Column Definition given the passed parameters
     */
    public void addColumn(String name, DataDefinition dataDefinition, String mappings,  DataConverter... converters) {
        if (dataDefinition == null) {
            throw new IllegalArgumentException("Cannot add a null dataDefinition as a column on a DSD");
        } else if (dataDefinition instanceof AppointmentDataDefinition) {
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, dataDefinition, mappings, converters));
        } else if (dataDefinition instanceof PatientDataDefinition) {
            AppointmentDataDefinition edd = new PatientToAppointmentDataDefinition((PatientDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, edd, mappings, converters));
        } else if (dataDefinition instanceof PersonDataDefinition) {
            AppointmentDataDefinition edd = new PersonToAppointmentDataDefinition((PersonDataDefinition) dataDefinition);
            getColumnDefinitions().add(new RowPerObjectColumnDefinition(name, edd, mappings, converters));
        } else {
            throw new IllegalArgumentException("Unable to add data definition of type " + dataDefinition.getClass().getSimpleName());
        }
    }

    /**
     * @see RowPerObjectDataSetDefinition#addColumns(String, RowPerObjectDataSetDefinition, String, org.openmrs.module.reporting.common.TimeQualifier, Integer, DataConverter...)
     */
    @Override
    public void addColumns(String name, RowPerObjectDataSetDefinition dataSetDefinition, String mappings,
                           TimeQualifier whichValues, Integer numberOfValues, DataConverter... converters) {

        // TODO Implement this
    }

    /**
     * Add a new row filter with the passed parameter mappings
     */
    public void addRowFilter(Mapped<AppointmentQuery> filter) {
        getRowFilters().add(filter);
    }

    /**
     * Add a new row filter with the passed parameter mappings
     */
    public void addRowFilter(AppointmentQuery filter, String mappings) {
        addRowFilter(new Mapped<AppointmentQuery>(filter, ParameterizableUtil.createParameterMappings(mappings)));
    }

    //***** PROPERTY ACCESS *****

    /**
     * @return the rowFilters
     */
    public List<Mapped<? extends AppointmentQuery>> getRowFilters() {
        if (rowFilters == null) {
            rowFilters = new ArrayList<Mapped<? extends AppointmentQuery>>();
        }
        return rowFilters;
    }

    /**
     * @param rowFilters the rowFilters to set
     */
    public void setRowFilters(List<Mapped<? extends AppointmentQuery>> rowFilters) {
        this.rowFilters = rowFilters;
    }

}
