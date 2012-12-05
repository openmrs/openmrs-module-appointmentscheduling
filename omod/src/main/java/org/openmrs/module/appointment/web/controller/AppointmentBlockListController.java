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
package org.openmrs.module.appointment.web.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.context.Context;
import org.openmrs.module.appointment.AppointmentBlock;
import org.openmrs.module.appointment.AppointmentType;
import org.openmrs.module.appointment.api.AppointmentService;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for listing appointment types.
 */
@Controller
public class AppointmentBlockListController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	@RequestMapping(value = "/module/appointment/appointmentBlockList", method = RequestMethod.GET)
	public void showForm(ModelMap model) throws IOException {
		//default empty Object
		List<AppointmentBlock> appointmentBlockList = new Vector<AppointmentBlock>();
		List<AppointmentType> appointmentTypeList = new Vector<AppointmentType>();
		List<Location> locationList = new Vector<Location>();
		//only fill the Object if the user has authenticated properly
		if (Context.isAuthenticated()) {
			AppointmentService appointmentService = Context.getService(AppointmentService.class);
			appointmentBlockList = appointmentService.getAllAppointmentBlocks();
			appointmentTypeList = new ArrayList<AppointmentType>(appointmentService.getAllAppointmentTypes(false));
			locationList = Context.getLocationService().getAllLocations(false);
		}
		
		model.addAttribute("appointmentBlockList", appointmentBlockList);
		model.addAttribute("appointmentTypeList", appointmentTypeList);
		model.addAttribute("locationList", locationList);
		//Location Hierarchy
		model.addAttribute("json", getHierarchyAsJson());
		model.addAttribute("locationWidgetType", Context.getAdministrationService().getGlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCATION_WIDGET_TYPE, "default"));
		//Setting the tree view to be default
		Context.getAdministrationService().saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_LOCATION_WIDGET_TYPE, "tree"));
	}
	
	/**
	 * Gets JSON formatted for jstree jquery plugin [ { data: ..., children: ...}, ... ]
	 * 
	 * @return
	 * @throws IOException
	 */
	private String getHierarchyAsJson() throws IOException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Location loc : Context.getLocationService().getAllLocations(false)) {
			if (loc.getParentLocation() == null) {
				list.add(toJsonHelper(loc));
			}
		}
		StringWriter w = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(w, list);
		return w.toString();
	}

	/**
	 * { data: "Location's name (tags)", children: [ recursive calls to this method, ... ] }
	 * 
	 * @param loc
	 * @return
	 */
	private Map<String, Object> toJsonHelper(Location loc) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		StringBuilder sb = new StringBuilder(loc.getName());
		if (loc.getTags() != null && loc.getTags().size() > 0) {
			sb.append(" (");
			for (Iterator<LocationTag> i = loc.getTags().iterator(); i.hasNext();) {
				LocationTag t = i.next();
				sb.append(t.getName());
				if (i.hasNext())
					sb.append(", ");
			}
			sb.append(")");
		}
		ret.put("data", sb.toString());
		if (loc.getChildLocations() != null && loc.getChildLocations().size() > 0) {
			List<Map<String, Object>> children = new ArrayList<Map<String, Object>>();
			for (Location child : loc.getChildLocations())
				children.add(toJsonHelper(child));
			ret.put("children", children);
		}
		return ret;
	}
}
