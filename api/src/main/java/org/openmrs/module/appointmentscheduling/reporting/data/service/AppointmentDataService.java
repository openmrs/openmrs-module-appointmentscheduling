package org.openmrs.module.appointmentscheduling.reporting.data.service;


import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.reporting.definition.service.DefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;
import org.openmrs.annotation.Authorized;

public interface AppointmentDataService extends DefinitionService<AppointmentDataDefinition> {

    /**
     * @see DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     */
    @Transactional(readOnly = true)
    @Authorized()
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException;

    /**
     * @see DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.parameter.Mapped < org.openmrs.module.reporting.evaluation.Definition >, EvaluationContext)
     */
    @Transactional(readOnly = true)
    @Authorized()
    public EvaluatedAppointmentData evaluate(Mapped<? extends AppointmentDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException;

}


