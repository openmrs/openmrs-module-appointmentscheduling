package org.openmrs.module.appointmentscheduling.reporting.query.service;

import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentQueryResult;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

public class AppointmentQueryServiceImpl extends BaseDefinitionService<AppointmentQuery> implements AppointmentQueryService {

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#getDefinitionType()
     */
    @Transactional(readOnly = true)
    public Class<AppointmentQuery> getDefinitionType() {
        return AppointmentQuery.class;
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * <strong>Should</strong> evaluate an encounter query
     */
    @Transactional(readOnly = true)
    public AppointmentQueryResult evaluate(AppointmentQuery query, EvaluationContext context) throws EvaluationException {
        return (AppointmentQueryResult)super.evaluate(query, context);
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.parameter.Mapped, EvaluationContext)
     */
    @Transactional(readOnly = true)
    public AppointmentQueryResult evaluate(Mapped<? extends AppointmentQuery> mappedQuery, EvaluationContext context) throws EvaluationException {
        return (AppointmentQueryResult)super.evaluate(mappedQuery, context);
    }

}
