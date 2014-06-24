package org.openmrs.module.appointmentscheduling.reporting.data;

import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.querybuilder.HqlQueryBuilder;
import org.openmrs.module.reporting.evaluation.service.EvaluationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppointmentDataUtil {

    public static Set<Integer> getAppointmentIdsForContext(EvaluationContext context, boolean returnNullForAllAppointmentIds) {

        Cohort patIds = context.getBaseCohort();
        AppointmentIdSet appointmentIds = (context instanceof AppointmentEvaluationContext ? ((AppointmentEvaluationContext) context).getBaseAppointments() : null);

        // If either context filter is not null and empty, return an empty set
        if ((patIds != null && patIds.isEmpty()) || (appointmentIds != null && appointmentIds.isEmpty())) {
            return new HashSet<Integer>();
        }

        // Retrieve the appointments for the baseCohort if specified
        if (patIds != null) {
            Set<Integer> appointmentIdsForPatients = getAppointmentIdsForPatients(patIds.getMemberIds());
            if (appointmentIds == null) {
                appointmentIds = new AppointmentIdSet(appointmentIdsForPatients);
            }
            else {
                appointmentIds.getMemberIds().retainAll(appointmentIdsForPatients);
            }
        }

        // If any filter was applied, return the results of this
        if (appointmentIds != null) {
            return appointmentIds.getMemberIds();
        }

        // Otherwise, all visit are needed, so return appropriate value
        if (returnNullForAllAppointmentIds) {
            return null;
        }
        return getAppointmentIdsForPatients(null);
    }

    public static Set<Integer> getAppointmentIdsForPatients(Set<Integer> patientIds) {
        EvaluationContext context = new EvaluationContext();
        if (patientIds != null) {
            context.setBaseCohort(new Cohort(patientIds));
        }
        HqlQueryBuilder qb = new HqlQueryBuilder();
        qb.select("a.appointmentId").from(Appointment.class, "a").wherePatientIn("a.patient.patientId", context);
        List<Integer> ids = Context.getService(EvaluationService.class).evaluateToList(qb, Integer.class, context);
        return new HashSet<Integer>(ids);
    }
}
