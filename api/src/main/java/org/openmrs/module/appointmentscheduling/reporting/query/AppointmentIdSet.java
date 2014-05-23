package org.openmrs.module.appointmentscheduling.reporting.query;

import org.openmrs.module.appointmentscheduling.Appointment;
import org.openmrs.module.reporting.query.BaseIdSet;

import java.util.List;
import java.util.Set;

public class AppointmentIdSet extends BaseIdSet<Appointment> {

    public AppointmentIdSet() {
        super();
    }

    public AppointmentIdSet(Set<Integer> memberIds) {
        setMemberIds(memberIds);
    }

    public AppointmentIdSet(List<Integer> memberIds) {
        add(memberIds.toArray(new Integer[0]));
    }

    public AppointmentIdSet(Integer... memberIds) {
        add(memberIds);
    }
}
