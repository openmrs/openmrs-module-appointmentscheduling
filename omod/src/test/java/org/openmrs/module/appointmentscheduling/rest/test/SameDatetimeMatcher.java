package org.openmrs.module.appointmentscheduling.rest.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.openmrs.module.webservices.rest.web.ConversionUtil;

import java.util.Date;

/**
 * A matcher that will compare two strings representing datetimes with time zones, accounting for
 * the fact that midnight in Boston is 9pm in Seattle, etc.
 */
public class SameDatetimeMatcher extends BaseMatcher<String> {
	
	private Date expected;
	
	public SameDatetimeMatcher(Date expected) {
		this.expected = expected;
	}
	
	public SameDatetimeMatcher(String expected) {
		this.expected = (Date) ConversionUtil.convert(expected, Date.class);
	}
	
	@Override
	public boolean matches(Object item) {
		Date actual = (Date) ConversionUtil.convert((String) item, Date.class);
		return expected.getTime() == actual.getTime();
	}
	
	@Override
	public void describeTo(Description description) {
		description.appendText("same time as " + expected);
	}
	
	// convenience factory methods
	
	public static SameDatetimeMatcher sameDatetime(String expected) {
		return new SameDatetimeMatcher(expected);
	}
	
	public static SameDatetimeMatcher sameDatetime(Date expected) {
		return new SameDatetimeMatcher(expected);
	}
	
}
