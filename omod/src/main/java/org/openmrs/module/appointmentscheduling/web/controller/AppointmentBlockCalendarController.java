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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointmentscheduling.AppointmentType;
import org.openmrs.module.appointmentscheduling.api.AppointmentService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Controller for listing appointment types.
 */
@Controller
public class AppointmentBlockCalendarController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockCalendar", method = RequestMethod.GET)
	public void showForm(HttpServletRequest request, ModelMap model) throws ParseException {
		if (Context.isAuthenticated()) {
			//Set the location from the session
			if (request.getSession().getAttribute("chosenLocation") != null) {
				Location location = (Location) request.getSession().getAttribute("chosenLocation");
				model.addAttribute("chosenLocation", location);
			}
			//Set the provider from the session
			if (request.getSession().getAttribute("chosenProvider") != null) {
				Provider provider = Context.getProviderService().getProvider(
				    (Integer) request.getSession().getAttribute("chosenProvider"));
				model.addAttribute("chosenProvider", provider.getProviderId());
			}
			//Set the appointmentType from the session
			if (request.getSession().getAttribute("chosenType") != null) {
				AppointmentType appointmentType = Context.getService(AppointmentService.class).getAppointmentType(
				    (Integer) request.getSession().getAttribute("chosenType"));
				model.addAttribute("chosenType", appointmentType.getAppointmentTypeId());
			}
			//Set the date interval from the session
			String fromDate;
			String toDate;
			Long fromDateAsLong;
			Long toDateAsLong;
			
			fromDate = (String) request.getSession().getAttribute("fromDate");
			toDate = (String) request.getSession().getAttribute("toDate");
			Calendar cal = Context.getDateTimeFormat().getCalendar();
			if (fromDate == null && toDate == null) {
				//In case the user loaded the page for the first time, we will set to default the time interval (1 week from today).
				Date startDate = OpenmrsUtil.firstSecondOfDay(new Date());
				fromDate = Context.getDateTimeFormat().format(startDate);
				fromDateAsLong = startDate.getTime();
				cal.setTime(OpenmrsUtil.getLastMomentOfDay(new Date()));
				cal.add(Calendar.DAY_OF_MONTH, 6);
				Date endDate = cal.getTime();
				toDate = Context.getDateTimeFormat().format(endDate);
				toDateAsLong = endDate.getTime();
			} else {
				//Session is not empty and we need to change the locale if we have to.
				Locale lastLocale = (Locale) request.getSession().getAttribute("lastLocale");
				Locale currentLocale = Context.getLocale();
				Date startDate;
				Date endDate;
				//check if the last locale equals to the current locale
				if (lastLocale != null && lastLocale.toString().compareTo(currentLocale.toString()) != 0) {
					//if the locals are different 
					startDate = OpenmrsUtil.getDateTimeFormat(lastLocale).parse(fromDate);
					endDate = OpenmrsUtil.getDateTimeFormat(lastLocale).parse(toDate);
					fromDate = Context.getDateTimeFormat().format(startDate);
					toDate = Context.getDateTimeFormat().format(endDate);
					fromDateAsLong = startDate.getTime();
					toDateAsLong = endDate.getTime();
				} else {
					startDate = OpenmrsUtil.getDateTimeFormat(currentLocale).parse(fromDate);
					endDate = OpenmrsUtil.getDateTimeFormat(currentLocale).parse(toDate);
					fromDateAsLong = startDate.getTime();
					toDateAsLong = endDate.getTime();
				}
			}
			//Update session variables - this will be updated in every locale change.
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute("fromDate", fromDate);
			httpSession.setAttribute("toDate", toDate);
			httpSession.setAttribute("lastLocale", Context.getLocale());
			
			//Update model variables - what the page shows.
			model.addAttribute("fromDate", fromDateAsLong);
			model.addAttribute("toDate", toDateAsLong);
			
		}
	}
	
	@ModelAttribute("appointmentTypeList")
	public List<AppointmentType> getAppointmentTypeList() {
		return Context.getService(AppointmentService.class).getAllAppointmentTypesSorted(false);
	}
	
	@ModelAttribute("providerList")
	public List<Provider> getProviderList() {
		return Context.getService(AppointmentService.class).getAllProvidersSorted(false);
	}
	
	@RequestMapping(value = "/module/appointmentscheduling/appointmentBlockCalendar", method = RequestMethod.POST)
	public String loadForm(HttpServletRequest request, ModelMap model,
	        @RequestParam(value = "action", required = false) String action,
	        @RequestParam(value = "locationId", required = false) Location location,
	        @RequestParam(value = "chosenType", required = false) Integer appointmentTypeId,
	        @RequestParam(value = "chosenProvider", required = false) Integer providerId,
	        @RequestParam(value = "fromDate", required = false) Long fromDate,
	        @RequestParam(value = "toDate", required = false) Long toDate,
	        @RequestParam(value = "appointmentBlockId", required = false) Integer appointmentBlockId) {
		if (Context.isAuthenticated()) {
			//Updating session variables
			Calendar cal = OpenmrsUtil.getDateTimeFormat(Context.getLocale()).getCalendar();
			cal.setTimeInMillis(fromDate);
			Date fromDateAsDate = cal.getTime();
			cal.setTimeInMillis(toDate);
			Date toDateAsDate = cal.getTime();
			HttpSession httpSession = request.getSession();
			httpSession.setAttribute("chosenLocation", location);
			httpSession.setAttribute("lastLocale", Context.getLocale());
			httpSession.setAttribute("chosenProvider", providerId);
			httpSession.setAttribute("chosenType", appointmentTypeId);
			//If the user wants to add new appointment block (clicked on a day)
			if (action != null && action.equals("addNewAppointmentBlock")) {
				String getRequest = "";
				//Fill the request from the user with selected date and forward it to appointmentBlockForm
				getRequest += "fromDate=" + Context.getDateTimeFormat().format(fromDateAsDate);
				if (toDate != null && !toDate.equals(fromDate)) { //If the fromDate is not the same as toDate (not a day click on month view)
					getRequest += "&toDate=" + Context.getDateTimeFormat().format(toDateAsDate);
				}
				getRequest += "&redirectedFrom=appointmentBlockCalendar.list";
				return "redirect:appointmentBlockForm.form?" + getRequest;
			}
			//If the user wants to change the view to table view
			else if (action != null && action.equals("changeToTableView")) {
				return "redirect:appointmentBlockList.list";
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
