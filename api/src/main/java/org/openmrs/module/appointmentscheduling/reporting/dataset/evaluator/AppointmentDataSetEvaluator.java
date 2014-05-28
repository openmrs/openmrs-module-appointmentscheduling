package org.openmrs.module.appointmentscheduling.reporting.dataset.evaluator;

import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.appointmentscheduling.reporting.data.service.AppointmentDataService;
import org.openmrs.module.appointmentscheduling.reporting.dataset.definition.AppointmentDataSetDefinition;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.BasicAppointmentQuery;
import org.openmrs.module.appointmentscheduling.reporting.query.service.AppointmentQueryService;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.data.DataUtil;
import org.openmrs.module.reporting.data.MappedData;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.column.definition.RowPerObjectColumnDefinition;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.evaluator.DataSetEvaluator;
import org.openmrs.module.reporting.definition.DefinitionUtil;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.query.QueryUtil;

@Handler(supports = AppointmentDataSetDefinition.class)
public class AppointmentDataSetEvaluator implements DataSetEvaluator {

    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Public constructor
     */
    public AppointmentDataSetEvaluator() { }

    /**
     * @see DataSetEvaluator#evaluate(org.openmrs.module.reporting.dataset.definition.DataSetDefinition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @SuppressWarnings("unchecked")
    public DataSet evaluate(DataSetDefinition dataSetDefinition, EvaluationContext context) throws EvaluationException {

        AppointmentDataSetDefinition dsd = (AppointmentDataSetDefinition) dataSetDefinition;
        context = ObjectUtil.nvl(context, new EvaluationContext());

        SimpleDataSet dataSet = new SimpleDataSet(dsd, context);
        dataSet.setSortCriteria(dsd.getSortCriteria());

        // Construct a AppointmentEvaluationContext based on the visit filter
        AppointmentIdSet r = null;
        if (dsd.getRowFilters() != null) {
            for (Mapped<? extends AppointmentQuery> q : dsd.getRowFilters()) {
                AppointmentIdSet s = Context.getService(AppointmentQueryService.class).evaluate(q, context);
                r = QueryUtil.intersectNonNull(r, s);
            }
        }
        if (r == null) {
            r = Context.getService(AppointmentQueryService.class).evaluate(new BasicAppointmentQuery(), context);
        }
        AppointmentEvaluationContext aec = new AppointmentEvaluationContext(context, r);
        aec.setBaseCohort(null); // We can do this because the appointmentIdSet is already limited by these

        // Evaluate each specified ColumnDefinition for all of the included rows and add these to the dataset
        for (RowPerObjectColumnDefinition cd : dsd.getColumnDefinitions()) {

            if (log.isDebugEnabled()) {
                log.debug("Evaluating column: " + cd.getName());
                log.debug("With Data Definition: " + DefinitionUtil.format(cd.getDataDefinition().getParameterizable()));
                log.debug("With Mappings: " + cd.getDataDefinition().getParameterMappings());
                log.debug("With Parameters: " + aec.getParameterValues());
            }

            StopWatch sw = new StopWatch();
            sw.start();

            MappedData<? extends AppointmentDataDefinition> dataDef = (MappedData<? extends AppointmentDataDefinition>) cd.getDataDefinition();
            EvaluatedAppointmentData data = Context.getService(AppointmentDataService.class).evaluate(dataDef, aec);

            DataSetColumn column = new DataSetColumn(cd.getName(), cd.getName(), dataDef.getParameterizable().getDataType()); // TODO: Support One-Many column definition to column

            for (Integer id : r.getMemberIds()) {
                Object val = data.getData().get(id);
                val = DataUtil.convertData(val, dataDef.getConverters());
                dataSet.addColumnValue(id, column, val);
            }

            sw.stop();

            if (log.isDebugEnabled()) {
                log.debug("Added column: " + sw.toString());
            }

        }

        return dataSet;
    }


}
