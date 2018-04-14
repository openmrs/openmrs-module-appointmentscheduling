package org.openmrs.module.appointmentscheduling.reporting.query.service;

import org.openmrs.module.appointmentscheduling.reporting.query.definition.AppointmentQuery;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentQueryResult;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AppointmentQueryService extends DefinitionService<AppointmentQuery> {

    @Transactional(readOnly = true)
    @Authorized()
    public AppointmentQueryResult evaluate(AppointmentQuery query, EvaluationContext context) throws EvaluationException;

    @Transactional(readOnly = true)
    @Authorized()
    public AppointmentQueryResult evaluate(Mapped<? extends AppointmentQuery> mappedQuery, EvaluationContext context) throws EvaluationException;

}
