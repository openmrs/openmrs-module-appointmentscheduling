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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getService(AppointmentService.class).getAllProvidersSorted(false);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String loadForm(HttpServletRequest request, ModelMap model,
	        @RequestParam(value = "action", required = false) String action,
	        @RequestParam(value = "fromDate", required = false) Long fromDate,
	        @RequestParam(value = "toDate", required = false) Long toDate,
	        @RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId) {
		if (Context.isAuthenticated()) {
			//If the user wants to add new appointment block (clicked on a day)
			if (action != null && action.equals("addNewAppointmentBlock")) {
				String getRequest = "";
				//Fill the request from the user with selected date and forward it to appointmentBlockForm
				Calendar cal = OpenmrsUtil.getDateTimeFormat(Context.getLocale()).getCalendar();
				cal.setTimeInMillis(fromDate);
				Date fromDateAsDate = cal.getTime();
				getRequest += "startDate=" + Context.getDateTimeFormat().format(fromDateAsDate);
				if (toDate != null && !toDate.equals(fromDate)) { //If the fromDate is not the same as toDate (not a day click on month view)
					cal.setTimeInMillis(toDate);
					Date toDateAsDate = cal.getTime();
					getRequest += "&endDate=" + Context.getDateTimeFormat().format(toDateAsDate);
				}
				getRequest += "&redirectedFrom=appointmentBlockCalendar.list";
				return "redirect:appointmentBlockForm.form?" + getRequest;
			}
			//If the user wants to edit an existing appointment block (clicked on an event)
			else if (action != null && action.equals("editAppointmentBlock")) {
				return "redirect:appointmentBlockForm.form?appointmentBlockId=" + appointmentBlockId
				        + "&redirectedFrom=appointmentBlockCalendar.list";
			}
		}
		
		return null;
	}
}
