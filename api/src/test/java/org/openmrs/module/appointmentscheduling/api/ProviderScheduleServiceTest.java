package org.openmrs.module.appointmentscheduling.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.ProviderSchedule;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ProviderScheduleServiceTest extends BaseModuleContextSensitiveTest {
    protected final Log log = LogFactory.getLog(this.getClass());

    private static final int TOTAL_PROVIDER_SCHEDULES = 5;

    @Autowired
    private AppointmentService service;

    @Before
    public void before() throws Exception {
        executeDataSet("standardAppointmentTestDataset.xml");
    }

    @Test
    @Verifies(value = "should get all provider schedules", method = "getAllProviderSchedules()")
    public void getAllProviderSchedules_shouldGetAllProviderSchedules() throws Exception {
        List<ProviderSchedule> schedules = service.getAllProviderSchedules();
        assertEquals(TOTAL_PROVIDER_SCHEDULES, schedules.size());
    }

    @Test
    @Verifies(value = "should get correct  ProviderSchedule", method = "getProviderSchedule(Integer)")
    public void getProviderSchedule_shouldGetProviderSchedule() throws Exception {
        ProviderSchedule providerSchedule = service.getProviderSchedule(1);
        assertNotNull(providerSchedule);
        assertEquals("2019-05-05 00:00:00.0", providerSchedule.getStartDate().toString());

        providerSchedule = service.getProviderSchedule(2);
        assertNotNull(providerSchedule);
        assertEquals("2019-05-05 00:00:00.0", providerSchedule.getStartDate().toString());
    }

    @Test
    @Verifies(value = "should get correct  ProviderSchedule", method = "getProviderScheduleByUuid(String)")
    public void getProviderScheduleByUuid_shouldGetProviderScheduleByUuid() throws Exception {
        ProviderSchedule providerSchedule = service.getProviderScheduleByUuid("c0c579b0-ffff-401d-8a4a-976a0b183599");
        assertNotNull(providerSchedule);
        assertEquals("2019-05-05 00:00:00.0", providerSchedule.getStartDate().toString());

        providerSchedule = service.getProviderScheduleByUuid("759799ab-gggg-435e-b671-77773ada99e9");
        assertNotNull(providerSchedule);
        assertEquals("2019-05-05 00:00:00.0", providerSchedule.getStartDate().toString());
    }

    @Test
    @Verifies(value = "should save new provider schedule", method = "saveProviderSchedule(ProviderSchedule)")
    public void saveProviderSchedule_shouldSaveProviderSchedule() throws Exception {
        List<ProviderSchedule> providerSchedules = service.getAllProviderSchedules(true);
        assertEquals(TOTAL_PROVIDER_SCHEDULES, providerSchedules.size());

        providerSchedules = service.getAllProviderSchedules();
        assertEquals(5, providerSchedules.size());

        Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
        Provider provider = Context.getProviderService().getProvider(1);
        ProviderSchedule schedule = new ProviderSchedule(7, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-05-05 00:00:00"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2119-05-05 00:00:00"),
                new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse("07:00:00").getTime()), new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse("10:00:00").getTime()),  provider, new Location(1), appointmentTypes);
        service.saveProviderSchedule(schedule);

        schedule = service.getProviderSchedule(7);
        assertNotNull(schedule);
    }

    @Test
    @Verifies(value = "save a providerless provider schedule", method = "saveProviderSchedule(ProviderSchedule)")
    public void saveProviderSchedule_shouldSaveAProviderlessProviderSchedule() throws Exception {
        List<ProviderSchedule> providerSchedules = service.getAllProviderSchedules(true);
        assertEquals(TOTAL_PROVIDER_SCHEDULES, providerSchedules.size());

        Date started = new Date();
        Set<AppointmentType> appointmentTypes = service.getAllAppointmentTypes();
        ProviderSchedule schedule = new ProviderSchedule(8, new Date(), OpenmrsUtil.getLastMomentOfDay(started),
                new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse("07:00:00").getTime()), new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse("10:00:00").getTime()),
                null,  new Location(1), appointmentTypes);
        service.saveProviderSchedule(schedule);

        schedule = service.getProviderSchedule(8);
        assertThat(schedule.getProvider(), is(nullValue()));
    }


    @Test
    @Verifies(value = "should save edited provider schedule", method = "saveProviderSchedule(ProviderSchedule)")
    public void saveProviderSchedule_shouldUpdateProviderSchedule() throws Exception {

        ProviderSchedule providerSchedule = service.getProviderSchedule(1);
        assertNotNull(providerSchedule);
        assertEquals("2019-05-05 00:00:00.0", providerSchedule.getStartDate().toString());
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-05-05 00:00:00");
        providerSchedule.setStartDate(date);
        providerSchedule.setEndDate(OpenmrsUtil.getLastMomentOfDay(date));
        service.saveProviderSchedule(providerSchedule);

        providerSchedule = service.getProviderSchedule(1);
        assertNotNull(providerSchedule);
        assertEquals(date, providerSchedule.getStartDate());

        //Should not change the number of appointment types
        assertEquals(TOTAL_PROVIDER_SCHEDULES, service.getAllProviderSchedules().size());

    }

    @Test
    @Verifies(value = "should void given provider schedule", method = "voidProviderSchedule(ProviderSchedule, String)")
    public void voidProviderSchedule_shouldVoidProviderSchedule() throws Exception {
        ProviderSchedule schedule = service.getProviderSchedule(1);
        assertNotNull(schedule);
        Assert.assertFalse(schedule.isVoided());
        assertNull(schedule.getVoidReason());

        service.voidProviderSchedule(schedule, "void reason");

        schedule = service.getProviderSchedule(1);
        assertNotNull(schedule);
        assertTrue(schedule.isVoided());
        assertEquals("void reason", schedule.getVoidReason());

        //Should not change the number of provider schedules.
        assertEquals(TOTAL_PROVIDER_SCHEDULES, service.getAllProviderSchedules().size());
    }


    @Test
    @Verifies(value = "should delete given provider schedule", method = "purgeProviderSchedule(ProviderSchedule)")
    public void purgeProviderSchedule_shouldDeleteGivenProviderSchedule() throws Exception {
        ProviderSchedule schedule = service.getProviderSchedule(3);
        assertNotNull(schedule);

        service.purgeProviderSchedule(schedule);

        schedule = service.getProviderSchedule(3);
        assertNull(schedule);

        //Should decrease the number of provider schedules by one.
        assertEquals(TOTAL_PROVIDER_SCHEDULES - 1, service.getAllProviderSchedules().size());
    }


    @Test
    @Verifies(value = "should get all provider schedules with given conditions", method = "getProviderSchedulesByConstraints(Provider, Location, null)")
    public void getProviderSchedulesByConstraints_shouldGetProviderSchedulesByConstraints() throws Exception {
        Provider provider = Context.getProviderService().getProvider(1);
        Location location = Context.getLocationService().getLocation(2);
        Location location1 = Context.getLocationService().getLocation(3);
        List<ProviderSchedule> schedules = service.getProviderSchedulesByConstraints(location, provider, null);
        assertEquals(2, schedules.size());

        List<ProviderSchedule> providerSchedules = service.getProviderSchedulesByConstraints(location1, null, null);
        assertEquals(2, providerSchedules.size());
    }
}
