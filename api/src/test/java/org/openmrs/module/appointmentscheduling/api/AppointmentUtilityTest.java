package org.openmrs.module.appointmentscheduling.api;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AppointmentUtilityTest extends BaseModuleContextSensitiveTest {
	
	private AppointmentService service;
	
	@Before
	public void before() throws Exception {
		service = Context.getService(AppointmentService.class);
		executeDataSet("standardAppointmentTestDataset.xml");
	}
	
	@Test
	@Verifies(value = "should get all providers sorted alphabetically", method = "getAllProvidersSorted(boolean)")
	public void getAllProvidersSorted_shouldGetCorrectSort() {
		List<Provider> providers = service.getAllProvidersSorted(false);
		
		Provider shouldBeFirst = Context.getProviderService().getProvider(1);
		Provider shouldBeLast = Context.getProviderService().getProvider(2);
		assertNotNull(shouldBeFirst);
		assertNotNull(shouldBeLast);
		
		assertEquals(shouldBeFirst, providers.get(0));
		assertEquals(shouldBeLast, providers.get(providers.size() - 1));
		assertEquals(2, providers.size());
		
		providers = service.getAllProvidersSorted(true);
		
		shouldBeFirst = Context.getProviderService().getProvider(1);
		shouldBeLast = Context.getProviderService().getProvider(3);
		assertNotNull(shouldBeFirst);
		assertNotNull(shouldBeLast);
		
		assertEquals(shouldBeFirst, providers.get(0));
		assertEquals(shouldBeLast, providers.get(providers.size() - 1));
		assertEquals(3, providers.size());
		
	}
	
	@Test
	@Verifies(value = "should get all appointment types sorted alphabetically", method = "getAllAppointmentTypesSorted(boolean)")
	public void getAllAppointmentTypesSorted_shouldGetCorrectSort() {
		List<AppointmentType> appointmentTypes = service.getAllAppointmentTypesSorted(false);
		
		AppointmentType shouldBeFirst = service.getAppointmentType(3);
		AppointmentType shouldBeLast = service.getAppointmentType(2);
		assertNotNull(shouldBeFirst);
		assertNotNull(shouldBeLast);
		
		assertEquals(shouldBeFirst, appointmentTypes.get(0));
		assertEquals(shouldBeLast, appointmentTypes.get(appointmentTypes.size() - 1));
		assertEquals(3, appointmentTypes.size());
		
		appointmentTypes = service.getAllAppointmentTypesSorted(true);
		
		shouldBeFirst = service.getAppointmentType(4);
		shouldBeLast = service.getAppointmentType(2);
		assertNotNull(shouldBeFirst);
		assertNotNull(shouldBeLast);
		
		assertEquals(shouldBeFirst, appointmentTypes.get(0));
		assertEquals(shouldBeLast, appointmentTypes.get(appointmentTypes.size() - 1));
		assertEquals(4, appointmentTypes.size());
		
	}
	
	@Test
	@Verifies(value = "should retrieve correct descendants", method = "getAllLocationDescendants(Location , Set<Location> )")
	public void getAllLocationDescendants_shouldGetCorrectDescendants() {
		Location location = Context.getLocationService().getLocation(2);
		assertNotNull(location);
		
		Set<Location> descendants = new HashSet<Location>();
		descendants.add(Context.getLocationService().getLocation(3));
		descendants.add(Context.getLocationService().getLocation(4));
		
		assertEquals(descendants, service.getAllLocationDescendants(location, null));
	}
	
}
