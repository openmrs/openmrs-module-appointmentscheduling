<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/appointmentPortlet_jQueryDatatable.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/appointmentPortletStyle.css" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">		
		//Navigate to appointmentForm.form
		function addNewAppointment(){
			var patientId = document.getElementById("patientId").value;
			window.location = "module/appointmentscheduling/appointmentForm.form?patientId="+patientId;
		}		
		//On the page load updates necessary stuff
         $j(document).ready(function() {      	 
                //data table initialization
                var oTable = $j('#AppointmentsTable').dataTable({
                 "aLengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
					"aoColumns" : [ {
						"bVisible"  : true												
					}, { 
						"iDataSort" : 7
					}, {
						"bSortable" : true
					} ,{
						"bSortable" : true
					}, {
						"bSortable" : true
					}, {
						"bSortable" : true
					} ,{
						"bSortable" : true
					} ,{
						"bVisible" : false
					}],
                 "iDisplayLength": 25,
				"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix' <'addons'>fl>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'ip>",
				"bLengthChange" : true,
				"bFilter" : false,
				"bInfo" : true,
				"bPaginate" : true,
				"bJQueryUI" : true,
				"fnDrawCallback" : function() {
					//Clear and prepend the schedule appointment button
					$j(".addons").html("");
					$j(".addons").prepend(
									"<table style='margin:10px; float:right; display:inline-block;' >"+
										"<tr><td>"+
											"<openmrs:hasPrivilege privilege='Schedule Appointments'><input type='button' value='<spring:message code='appointmentscheduling.Appointment.add'/>' class='saveButton buttonShadow' onclick='ScheduleAppointmentDialogOpen();'/></openmrs:hasPrivilege>"+
										"</tr></td>"+
									"</table>");
				}
                });
				//Default sort: ascending by date
				oTable.fnSort([[1,'asc']]);	
                
                //Scheduling Dialog
				$j('#scheduleDialog').dialog({
					autoOpen: false,
					height: 250,
					width: 300,
					modal: true,
					resizable: false,
					buttons: {
						"<spring:message code='general.cancel' />": function() {
							$j(this).dialog("close");
						},
						"<spring:message code='general.submit' />": function() {
							var navigationURL = $j('input[name="selectDialogAction"]:radio:checked')[0].value;
							window.location = navigationURL;
						}
					},
					open: function(){
						//Select first as default
						document.getElementById("scheduleNewAppointmentOption").checked = true;
					}
				});
        });
		
 		function ScheduleAppointmentDialogOpen(){
			$j('#scheduleDialog').dialog('open');
		}

</script>
<input id="patientId" type="hidden" value="${model.patientId}" />
<div id="portlet${model.portletUUID}">
	
	<div id="appointmentPortlet">
			<div>
		        <table id="AppointmentsTable" cellspacing="0">
		                <thead>
		                    <tr>
		                        <th align="center" style="display:none;">select</th>
		                        <th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.date'/></th>
	                            <th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.time'/></th>
	                            <th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.clinician'/></th>
                        		<th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.location'/></th>
                     			<th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.type'/></th>
       							<th align="center"><spring:message code='appointmentscheduling.Appointment.list.column.status'/></th>
		                        <th align="center">Hidden sortable dates</th>
		                    </tr>
		                </thead>
								<tbody>
									<c:forEach var="appointment" items="${model.appointmentList}">
										<tr ${appointment.appointmentId==param.selectAppointment ? 'class="selectedRow"' : 'class="notSelectedRow"'} title="${appointment.reason}">
											<td style="display:none;">
												<input type="radio" value="${appointment.appointmentId}" ${param.selectAppointment==appointment.appointmentId ? 'checked' : ''} name="selectAppointment" />
											</td>
											<td><fmt:formatDate type="date" pattern="dd/MM/yyyy" value="${appointment.timeSlot.startDate}"/></td>
											<td><fmt:formatDate type="time" pattern="HH:mm"
																value="${appointment.timeSlot.startDate}" /> - <fmt:formatDate type="time"
																pattern="HH:mm" value="${appointment.timeSlot.endDate}" /></td>
											<td>${appointment.timeSlot.appointmentBlock.provider.name}</td>
											<td>${appointment.timeSlot.appointmentBlock.location.name}</td>
											<td>${appointment.appointmentType.name}</td>
											<td>${appointment.status.name}</td>
											<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"
																pattern="yyyyMMddHHmm" /></td>
										</tr>
									</c:forEach>
							</tbody>
		        </table>
			</div>
	</div>
</div>
<div id="scheduleDialog" >
	<table id='scheduleDialogOptions' class="dialogTable">
		<tr><td><h2><spring:message code='appointmentscheduling.Appointment.list.label.selectAnAction'/></h2></td></tr>
				<tr><td><input id="scheduleNewAppointmentOption" type="radio" name="selectDialogAction" value="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentForm.form?patientId=${param.patientId}&origin=dashboard" checked ><spring:message code='appointmentscheduling.Appointment.create.link.scheduleAppointment' /></input></td></tr>
				<tr><td><br/></td></tr>
				<tr><td><input type="radio" name="selectDialogAction" value="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentForm.form?flow=walkin&patientId=${param.patientId}&origin=dashboard"><spring:message code='appointmentscheduling.Appointment.create.link.walkinAppointment' /></input></td></tr>
		</table>
</div>