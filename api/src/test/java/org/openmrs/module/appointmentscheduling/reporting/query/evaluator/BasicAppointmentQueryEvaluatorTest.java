package org.openmrs.module.appointmentscheduling.reporting.query.evaluator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.api.LocationService;
import org.openmrs.module.appointmentscheduling.reporting.context.AppointmentEvaluationContext;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentIdSet;
import org.openmrs.module.appointmentscheduling.reporting.query.AppointmentQueryResult;
import org.openmrs.module.appointmentscheduling.reporting.query.definition.BasicAppointmentQuery;
import org.openmrs.module.appointmentscheduling.reporting.query.service.AppointmentQueryService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BasicAppointmentQueryEvaluatorTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private AppointmentQueryService appointmentQueryService;

    @Autowired
    private LocationService locationService;

    @Before
    public void setup() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }


    @Test
    public void shouldReturnAllNonVoidedAppointments() throws Exception {

        BasicAppointmentQuery query = new BasicAppointmentQuery();
        AppointmentQueryResult result = appointmentQueryService.evaluate(query, null);

        assertThat(result.getMemberIds().size(), is(12));
        assertTrue(result.getMemberIds().contains(1));
        assertTrue(result.getMemberIds().contains(2));
        assertTrue(result.getMemberIds().contains(4));
        assertTrue(result.getMemberIds().contains(5));
        assertTrue(result.getMemberIds().contains(6));
        assertTrue(result.getMemberIds().contains(7));
        assertTrue(result.getMemberIds().contains(8));
        assertTrue(result.getMemberIds().contains(9));
        assertTrue(result.getMemberIds().contains(11));
        assertTrue(result.getMemberIds().contains(12));
    }

    @Test
    public void shouldRestrictAppointmentReturnedByDate() throws Exception {

        BasicAppointmentQuery query = new BasicAppointmentQuery();
        query.setOnOrAfter(new DateTime(2006,1,1,0,0,0).toDate());
        query.setOnOrBefore(new DateTime(2006,1,1,1,0,0).toDate());

        AppointmentQueryResult result = appointmentQueryService.evaluate(query, null);
        assertThat(result.getMemberIds().size(), is(2));
        assertTrue(result.getMemberIds().contains(1));
        assertTrue(result.getMemberIds().contains(2));
    }

    @Test
    public void shouldRestrictAppointmentReturnedByPatient() throws Exception {

        BasicAppointmentQuery query = new BasicAppointmentQuery();
        EvaluationContext context = new EvaluationContext();
        context.setBaseCohort(new Cohort(Collections.singleton(1)));

        AppointmentQueryResult result = appointmentQueryService.evaluate(query, context);
        assertThat(result.getMemberIds().size(), is(6));
        assertTrue(result.getMemberIds().contains(1));
        assertTrue(result.getMemberIds().contains(4));
        assertTrue(result.getMemberIds().contains(5));
        assertTrue(result.getMemberIds().contains(6));
        assertTrue(result.getMemberIds().contains(7));
        assertTrue(result.getMemberIds().contains(8));
    }

    @Test
    public void shouldRestrictAppointmentReturnedByAppointment() throws Exception {

        BasicAppointmentQuery query = new BasicAppointmentQuery();
        AppointmentEvaluationContext context = new AppointmentEvaluationContext();
        context.setBaseAppointments(new AppointmentIdSet(1));

        AppointmentQueryResult result = appointmentQueryService.evaluate(query, context);
        assertThat(result.getMemberIds().size(), is(1));
        assertTrue(result.getMemberIds().contains(1));
    }


    @Test
    public void shouldRestrictAppointmentReturnedByLocation() throws Exception {

        BasicAppointmentQuery query = new BasicAppointmentQuery();
        query.setLocation(locationService.getLocation(2));

        AppointmentQueryResult result = appointmentQueryService.evaluate(query, null);
        assertThat(result.getMemberIds().size(), is(4));
        assertTrue(result.getMemberIds().contains(7));
        assertTrue(result.getMemberIds().contains(8));
        assertTrue(result.getMemberIds().contains(9));
        assertTrue(result.getMemberIds().contains(11));

    }

}
