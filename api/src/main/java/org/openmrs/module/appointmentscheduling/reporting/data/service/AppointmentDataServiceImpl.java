package org.openmrs.module.appointmentscheduling.reporting.data.service;

import org.openmrs.module.appointmentscheduling.reporting.data.EvaluatedAppointmentData;
import org.openmrs.module.appointmentscheduling.reporting.data.definition.AppointmentDataDefinition;
import org.openmrs.module.reporting.definition.service.BaseDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.springframework.transaction.annotation.Transactional;

public class AppointmentDataServiceImpl extends BaseDefinitionService<AppointmentDataDefinition> implements AppointmentDataService {

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#getDefinitionType()
     */
    @Transactional(readOnly = true)
    public Class<AppointmentDataDefinition> getDefinitionType() {
        return AppointmentDataDefinition.class;
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.Definition, org.openmrs.module.reporting.evaluation.EvaluationContext)
     * <strong>Should</strong> evaluate a Appointment data definition
     */
    @Transactional(readOnly = true)
    public EvaluatedAppointmentData evaluate(AppointmentDataDefinition definition, EvaluationContext context) throws EvaluationException {
        if (context.getBaseCohort() != null && context.getBaseCohort().isEmpty()) {
            return new EvaluatedAppointmentData(definition, context);
        }
        return (EvaluatedAppointmentData)super.evaluate(definition, context);
    }

    /**
     * @see org.openmrs.module.reporting.definition.service.DefinitionService#evaluate(org.openmrs.module.reporting.evaluation.parameter.Mapped < org.openmrs.module.reporting.evaluation.Definition >, EvaluationContext)
     */
    @Transactional(readOnly = true)
    public EvaluatedAppointmentData evaluate(Mapped<? extends AppointmentDataDefinition> mappedDefinition, EvaluationContext context) throws EvaluationException {
        return (EvaluatedAppointmentData)super.evaluate(mappedDefinition, context);
    }

}
