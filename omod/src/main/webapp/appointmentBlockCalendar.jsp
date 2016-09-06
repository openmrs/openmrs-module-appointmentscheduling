<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/AppointmentBlockStyle.css" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.print.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery-1.9.1.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/fullcalendar.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/opentip-jquery-excanvas.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/opentip.css"/>
<openmrs:require privilege="View Provider Schedules" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentBlockCalendar.list" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
	var calendar;
	function getModuleURL(){
		var pathArray = window.location.pathname.split( '/' );
		var url = window.location.protocol+"//"+window.location.host+"/"+pathArray[1]+"/"+pathArray[2]+"/"+pathArray[3]+"/";
		return url;
	}
	function getSelectedLocationId(){
		var selectedLocation = document.getElementById("locationId");
		var modelAttributeSelectedLocation = "${chosenLocation}";
		var locationId = null;
		if(selectedLocation == null){
			locationId = $j("[name='locationId']")[0].value;
		}
		else{
			locationId = selectedLocation.options[selectedLocation.selectedIndex].value;
		}
		//location not selected
		if(locationId ==""){
			//If the location widget is not loaded yet
			if(modelAttributeSelectedLocation != null){
				locationId = "${chosenLocation.id}";
			}
			else locationId = null;				
		}
		return locationId;
	}
	function getSelectedProvider(){
		var providerId = null;
		var selectedProvider = document.getElementById("providerSelect");
		if(selectedProvider != null){
			providerId = selectedProvider.options[selectedProvider.selectedIndex].value;
		}
		document.getElementById("chosenProvider").value = providerId;
		return providerId;
	}
	function getSelectedAppointmentType(){
		var appointmentTypeId = null;
		var selectedAppointmentType = document.getElementById("appointmentTypeSelect");
		if(selectedAppointmentType != null){
			appointmentTypeId = selectedAppointmentType.options[selectedAppointmentType.selectedIndex].value;
		}
		document.getElementById("chosenType").value = appointmentTypeId;
		return appointmentTypeId;
	}
	function viewChange(){
		//Check if change to table view is needed
		var selectedView = document.getElementById("viewSelect");
		return (selectedView.options[selectedView.selectedIndex].value == "tableView");
	}
	function updateAppointmentBlockCalendar(fromDate,toDate) //updates the calendar if changes occur
	{
		var calendarContent;	
		var locationId = getSelectedLocationId(); 
		var providerId = getSelectedProvider();
		var appointmentTypeId = getSelectedAppointmentType();
		if(viewChange()){
			changeToTableView();
		}
		//DWR call for getting the appointment blocks that have the selected properties
		DWRAppointmentService.getAppointmentBlocksForCalendar(fromDate.getTime(),toDate.getTime(),locationId, providerId, appointmentTypeId,function(appointmentBlocks){
			var tableContent = '';
			var count = 0;
			calendarContent = [];
			for(var i=0;i<appointmentBlocks.length;i++)
			{
				count++;
				//Linking the appointment types in a string.
				var appointmentTypes = "";
				var appointmentTypesArray = appointmentBlocks[i].types;
				for(var j=0;j<appointmentTypesArray.length;j++)
				{
					appointmentTypes += appointmentTypesArray[j];
					if(j<(appointmentTypesArray.length - 1)){
						appointmentTypes += ", ";
					}
				}
				//Json object update
				var appointmentBlockEvent = {};
				//Calendar event properties
				appointmentBlockEvent.id = appointmentBlocks[i].appointmentBlockId;
				appointmentBlockEvent.title = appointmentBlocks[i].provider+" ("+appointmentBlocks[i].location+")";
				appointmentBlockEvent.start = appointmentBlocks[i].startDate.toString();
				appointmentBlockEvent.end = appointmentBlocks[i].endDate.toString();
				appointmentBlockEvent.allDay = false;
				//Additional appointment block properties
				appointmentBlockEvent.provider = appointmentBlocks[i].provider;
				appointmentBlockEvent.location = appointmentBlocks[i].location;
				appointmentBlockEvent.appointmentTypes = appointmentTypes;
				appointmentBlockEvent.startTime=appointmentBlocks[i].startTime;
				appointmentBlockEvent.endTime=appointmentBlocks[i].endTime;
				appointmentBlockEvent.timeSlotLength=appointmentBlocks[i].timeSlotLength;
				calendarContent.push(appointmentBlockEvent);
			}
			if(count == appointmentBlocks.length){
				if(appointmentBlocks.length == 0){ //if there was no appointment blocks, clear table and destroy
				 }				
					//update calendar content using the received data	
					//Clear previous content
					$('#calendarBlocks').fullCalendar( 'removeEvents' );
					//Add the new events(appointmentBlocks)
					$('#calendarBlocks').fullCalendar( 'addEventSource', calendarContent);
				}	
		});
	}	
	function InitializeCalendar(){
		return $('#calendarBlocks').fullCalendar({
			header: {
				left: 'prev,next today',
				center: 'title',
				right: 'month,agendaWeek,agendaDay'
			},
			selectable: true,
			selectHelper: true,
			select: function(start, end, allDay) {
				document.getElementById('action').value = "addNewAppointmentBlock";
				var startDate = new Date(start);
				var endDate = new Date(end);
				var currentDateTime = new Date();
				if(startDate.getTime()==endDate.getTime()){ //Month view
					currentDateTime.setHours(0,0,0,0);
				}
				
				if(startDate.getTime()<currentDateTime.getTime()){ //Can't save blocks in the past
					var dialogContent = "<spring:message code='appointmentscheduling.AppointmentBlock.calendar.scheduleError'/>";
					document.getElementById("notifyDialogText").innerHTML = dialogContent;
					$j('#notifyDialog').dialog('open');
					event.stopPropagation();
				}
				else{
					document.getElementById('fromDate').value = startDate.getTime();
					document.getElementById('toDate').value = endDate.getTime();
					document.forms['appointmentBlockCalendarForm'].submit();
					calendar.fullCalendar('unselect');
				}
			},
			editable: false,
			theme: true,
			eventClick: function(calEvent, jsEvent, view) {
				var selectedAppointmentBlockId = calEvent.id;
				document.getElementById('action').value = "editAppointmentBlock";
				document.getElementById('fromDate').value = 0;
				document.getElementById('toDate').value = 0;
				document.getElementById('appointmentBlockId').value = selectedAppointmentBlockId;
				document.forms['appointmentBlockCalendarForm'].submit();
				
			},
			eventMouseover: function(calEvent, jsEvent, view) {
				$(this).css('border-color', 'red');
				var tooltipContent = '';
				tooltipContent += '<span style=\'color:red;\'><spring:message code="appointmentscheduling.AppointmentBlock.column.provider"/>:</span>'+' '+calEvent.provider;
				tooltipContent += '</br><span style=\'color:red;\'><spring:message code="appointmentscheduling.AppointmentBlock.column.location"/>:</span>'+' '+calEvent.location;
				tooltipContent += '</br><span style=\'color:red;\'><spring:message code="appointmentscheduling.AppointmentBlock.types"/>:</span>'+' '+calEvent.appointmentTypes;
				tooltipContent += '</br><span style=\'color:red;\'><spring:message code="appointmentscheduling.AppointmentBlock.column.time"/>:</span>'+' '+calEvent.startTime+"-"+calEvent.endTime;
				tooltipContent += '</br><span style=\'color:red;\'><spring:message code="appointmentscheduling.AppointmentBlock.slotLength"/>:</span>'+' '+calEvent.timeSlotLength;
				var appointmentBlockTooltip = new Opentip(this);
				appointmentBlockTooltip.setContent(tooltipContent);
			},
			eventMouseout: function(calEvent, jsEvent, view) {
				$(this).css('border-color', '');
			},

			viewDisplay: function(view) {
				updateAppointmentBlockCalendar(view.visStart,view.visEnd);
			}			
		});	
	}
	
	function initializeDialog(){
		//Dialog without buttons 
		$j('#notifyDialog').dialog({
			autoOpen: false,
			height: 150,
			width: 350,
			modal: true,
			resizable: false
		});	
	}
	
	function changeToTableView(){ //A function that updates the action to change the view to table view and submits the form
		//change action to table view 
		document.getElementById('action').value = "changeToTableView";
		//POST back in order to redirect to the table view via the controller.
		document.forms['appointmentBlockCalendarForm'].submit();
	}
	
	function refreshCalendar(){
		var calendarView = calendar.fullCalendar('getView');
		updateAppointmentBlockCalendar(calendarView.visStart,calendarView.visEnd);
	}
	function locationInitialize(){
		//If the user is using "Simple" version
		if ($j('#locationId').length > 0) {
			var selectLocation = $j('#locationId');
			//Set the 'All locations' option text (Default is empty string)
			if (selectLocation[0][0].innerHTML == '')
				selectLocation[0][0].innerHTML = "(<spring:message code='appointmentscheduling.AppointmentBlock.filters.locationNotSpecified'/>)";

		}
	}
	$j(document).ready(function() { 
		//Initialize dialog
		initializeDialog();
		//Initialize selected view as table view
		document.getElementById("viewSelect").selectedIndex = 1;
		calendar = InitializeCalendar();
		refreshCalendar();
		//Initialize location
		locationInitialize();
	}); 
</script>
<h2><spring:message code="appointmentscheduling.AppointmentBlock.manage.title"/></h2>
<br/><br/>
<form method="post" name="appointmentBlockCalendarForm">
<fieldset id="propertiesFieldset" style="clear: both">
        <legend><spring:message code="appointmentscheduling.AppointmentBlock.legend.properties"/></legend>
        <div style="margin: 0.5em 0;">
                <table>
                        <tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.column.location"/>: </td>
                            <td><openmrs_tag:locationField formFieldName="locationId" initialValue="${chosenLocation}" optionHeader="[blank]"/></td>
                        </tr>
                        <tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.provider"/>: </td>
   							<td>
	   							<select name="providerSelect" id="providerSelect">
									<option value="" ${null==chosenProvider ? 'selected' : ''}>
										(<spring:message
											code="appointmentscheduling.AppointmentBlock.filters.providerNotSpecified" />)
									</option>
									<c:forEach var="provider" items="${providerList}">
										<option value="${provider.providerId}"
											${provider.providerId==chosenProvider ? 'selected' : ''}><c:out value="${provider.name}"/></option>
									</c:forEach>
								</select>
							</td>
                        </tr>
                        <tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.type"/>: </td>
   							<td>
	   							<select name="appointmentTypeSelect" id="appointmentTypeSelect">
									<option value="" ${null==chosenType ? 'selected' : ''}>
										(<spring:message
											code="appointmentscheduling.AppointmentBlock.filters.typeNotSpecified" />)
									</option>
									<c:forEach var="type" items="${appointmentTypeList}">
										<option value="${type.appointmentTypeId}"
											${type.appointmentTypeId==chosenType ? 'selected' : ''}><c:out value="${type.name}"/></option>
									</c:forEach>
								</select>
							</td>
                        </tr>
						<tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.view"/>: </td>
   							<td>
	   							<select name="viewSelect" id="viewSelect">
									<option value="tableView"><spring:message code="appointmentscheduling.AppointmentBlock.tableView"/></option>
									<option value="calendarView"><spring:message code="appointmentscheduling.AppointmentBlock.calendarView"/></option>
								</select>
							</td>
                        </tr>
                        <tr>
                            <td><input type="button" class="appointmentBlockButton" value=<spring:message code="appointmentscheduling.AppointmentBlock.apply"/> onClick="refreshCalendar()"></td>
                        </tr>
                </table>
        </div>
</fieldset>
<br/>
	<input type="hidden" name="fromDate" id="fromDate" value="${fromDate}" />
	<input type="hidden" name="toDate" id="toDate" value="${toDate}" />
	<input type="hidden" name="appointmentBlockId" id="appointmentBlockId" value="${appointmentBlockId}" />
	<input type="hidden" name="action" id="action" value="<c:out value="${action}"/>" />
	<input type="hidden" name="chosenProvider" id="chosenProvider" value="<c:out value="${chosenProvider}"/>" />
	<input type="hidden" name="chosenType" id="chosenType" value="<c:out value="${chosenType}"/>" />
 </form>
<div id='calendarBlocks'></div>
 <div id="notifyDialog" title='<spring:message code="appointmentscheduling.AppointmentBlock.deleteDialog.title"/>'>
	<table id='notifyDialogTable' class="dialogTable">
		<tr><td><div id="notifyDialogText"></div></td></tr>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>