<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.print.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery-1.9.1.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/fullcalendar.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/opentip-jquery-excanvas.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/opentip.css"/>
<openmrs:require privilege="View Appointment Blocks" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentBlockList.list" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
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
	function updateAppointmentBlockCalendar(fromDate,toDate) //updates the calendar if changes occur
	{
		var calendarContent;	
		var locationId = null; //getSelectedLocationId(); //will be implemented later.
		//DWR call for getting the appointment blocks that have the selected properties
		DWRAppointmentService.getAppointmentBlocksForCalendar(fromDate.getTime(),toDate.getTime(),locationId,function(appointmentBlocks){
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
				updateCalendar(calendarContent);
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

			select: function(start, end, allDay) {
			var title = prompt('Event Title:');
			if (title) {
				calendar.fullCalendar('renderEvent',
					{
						title: title,
						start: start,
						end: end,
						allDay: allDay
					},
					true // make the event "stick"
				);
			}
			calendar.fullCalendar('unselect');
			},
			theme: true,
			editable: false,
			eventClick: function(calEvent, jsEvent, view) {
				var selectedAppointmentBlockId = calEvent.id;
				var appointmentBlockEditURL = getModuleURL()+'appointmentBlockForm.form?appointmentBlockId='+selectedAppointmentBlockId;
				window.open(appointmentBlockEditURL);
				
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
			dayClick:function( date, allDay, jsEvent, view ) {
				if(allDay==true){ //If the user has clicked on a slot in the agendaWeek or agendaDay views.
				}
				else{ //otherwise (Month view)
				}
				document.getElementById('action').value = "addNewAppointmentBlock";
				var d = new Date(date);
				document.getElementById('date').value = d.getTime();
				document.forms['appointmentBlockCalendarForm'].submit();

			},
			viewDisplay: function(view) {
				updateAppointmentBlockCalendar(view.visStart,view.visEnd);
			}			
		});	
	}
	
	function updateCalendar(calendarContent){
		//Clear previous content
		$('#calendarBlocks').fullCalendar( 'removeEvents' );
		//Add the new events(appointmentBlocks)
		$('#calendarBlocks').fullCalendar( 'addEventSource', calendarContent);
	}

	 $j(document).ready(function() { 
		var calendar = InitializeCalendar();
		var calendarView = calendar.fullCalendar('getView');
		updateAppointmentBlockCalendar(calendarView.visStart,calendarView.visEnd);
	}); 
</script>
 <form method="post" name="appointmentBlockCalendarForm">
	<input type="hidden" name="date" id="date" value="${date}" />
	<input type="hidden" name="action" id="action" value="${action}" />
 </form>
<div id='calendarBlocks'></div>
<%@ include file="/WEB-INF/template/footer.jsp" %>