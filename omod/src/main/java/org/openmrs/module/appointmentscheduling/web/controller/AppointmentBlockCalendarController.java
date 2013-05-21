/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.appointmentscheduling.web.controller;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for listing appointment types.
 */
@Controller
public class AppointmentBlockCalendarController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockCalendar", method = RequestMethod.GET)
	public void showForm(HttpServletRequest request, ModelMap model) {
		if (Context.isAuthenticated()) {
			model.addAttribute("localizedDatePattern", OpenmrsUtil.getDateTimeFormat(Context.getLocale())
			        .toLocalizedPattern());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String loadForm(HttpServletRequest request, ModelMap model,
	        @RequestParam(value = "action", required = false) String action,
	        @RequestParam(value = "date", required = false) Long fromDate) {
		if (Context.isAuthenticated()) {
			if (request.getAttribute("calendarContent") != null) { //request forwarded from appointmentBlockList with the appointment blocks data
				//update the calendar content from the request
				String calendarContentAsString = "'" + (String) request.getAttribute("calendarContent") + "'";
				model.addAttribute("calendarContent", calendarContentAsString);
			} else {
				if (action != null && action.equals("addNewAppointmentBlock")) {
					//Fill the request from the user with selected date and forward it to appointmentBlockForm
					Calendar cal = OpenmrsUtil.getDateTimeFormat(Context.getLocale()).getCalendar();
					cal.setTimeInMillis(fromDate);
					Date fromDateAsDate = cal.getTime();
					return "redirect:appointmentBlockForm.form?startDate="
					        + Context.getDateTimeFormat().format(fromDateAsDate);
				}
			}
		}
		
		return null;
	}
}
