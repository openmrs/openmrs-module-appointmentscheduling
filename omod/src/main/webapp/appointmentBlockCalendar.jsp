<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery-1.9.1.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/fullcalendar.print.css"/>
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
		function updateCalendar(){
			$('#calendarBlocks').fullCalendar( 'destroy' );
			//Initialize the Calendar
			$('#calendarBlocks').fullCalendar({
				header: {
					left: 'prev,next today',
					center: 'title',
					right: 'month,basicWeek,basicDay'
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
					tooltipContent += '<span style=\'color:red;\'>Provider:</span>'+' '+calEvent.provider;
					tooltipContent += '</br><span style=\'color:red;\'>Location:</span>'+' '+calEvent.location;
					tooltipContent += '</br><span style=\'color:red;\'>Type(s):</span>'+' '+calEvent.appointmentTypes;
					tooltipContent += '</br><span style=\'color:red;\'>Time:</span>'+' '+calEvent.startTime+"-"+calEvent.endTime;
					tooltipContent += '</br><span style=\'color:red;\'>Time Slot Length:</span>'+' '+calEvent.timeSlotLength+' min';
					var appointmentBlockTooltip = new Opentip(this);
					appointmentBlockTooltip.setContent(tooltipContent);
				},
				eventMouseout: function(calEvent, jsEvent, view) {
					$(this).css('border-color', '');
				}						
				
			});
			//Update the calendar's events(appointmentBlocks)
			//Clear previous content
			$('#calendarBlocks').fullCalendar( 'removeEvents' );
			//Add the new events(appointmentBlocks)
			var blocksAsJSON = JSON.parse(${calendarContent});
			$('#calendarBlocks').fullCalendar( 'addEventSource', blocksAsJSON );
		}
		
         $j(document).ready(function() { 
			updateCalendar();
        }); 
</script>
<div id='calendarBlocks'></div>
<%@ include file="/WEB-INF/template/footer.jsp" %>