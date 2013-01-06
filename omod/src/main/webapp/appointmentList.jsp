<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<openmrs:htmlInclude file="/moduleResources/appointment/Scripts/timepicker.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Scripts/statusButtons.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/createAppointmentStyle.css" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/appointmentList_jQueryDatatable.css" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css" />

<openmrs:htmlInclude
	file="/moduleResources/appointment/TableTools/media/js/TableTools.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/TableTools/media/ZeroClipboard/ZeroClipBoard.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/TableTools/media/css/TableTools.css" />

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script type="text/javascript">
	$j(document)
			.ready(
					function() {
						//Init Dates FIltering
						//Causes Errors
						//InitDateFilter();

						//Prevent date keyboard input
						$j("#fromDate").keypress(function(event) {event.preventDefault();});
						$j("#toDate").keypress(function(event) {event.preventDefault();});
						
						TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointment/TableTools/media/swf/ZeroClipboard.swf";

						//Datatables.net
						$j('#appointmentsTable')
								.dataTable(
										{
											"aoColumns" : [ {
												"bVisible"  : true												
											}, { 
												"iDataSort" : 8
											}, {
												"bSortable" : true
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
											}, {
												"bVisible" : false
											}],
											"aLengthMenu" : [
													[2, 25, 50, -1 ],
													[2, 25, 50, "All" ] ],
											"iDisplayLength" : 25,
											"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix' <'addons'>fl>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'<'statusDiv'>ip<'toolbar' T>>",

											"bLengthChange" : true,
											"bFilter" : true,
											"bInfo" : true,
											"bPaginate" : true,
											"bJQueryUI" : true,

											"fnDrawCallback" : function() {
												$j(".addons").html("");
												$j(".addons")
														.prepend(
																"<table style='margin:10px; float:right; display:inline-block;' >"+
																	"<tr><td>"+
																		"<input type=\"checkbox\" name=\"includeCancelled\" value=\"true\" onchange='this.form.submit();' ${param.includeCancelled=='true' ? 'checked' : ''}>"+
																			"<spring:message code='appointment.Appointment.list.label.showCancelled' />"+
																	"</td></tr>"+
																	"<tr><td>"+
																		"<input type='button' value='<spring:message code='appointment.Appointment.add'/>' class='saveButton buttonShadow' onclick='addNewAppointment()'/>"+
																	"</tr></td>"+
																"</table>");
												$j(".statusDiv").html("");
												$j('.statusDiv')
														.prepend(
																"<input type='button' id='startConsultButton' class='appointmentButton buttonShadow' value='Start Consultation' disabled />"+
																"<input type='button' id='endConsultButton' class='appointmentButton buttonShadow' value='End Consultation' disabled />"+
																"<input type='button' id='checkInButton' class='appointmentButton buttonShadow' style='margin-left:16px; margin-right:16px;' value='Check-In' disabled />"+
																"<input type='button' id='missButton' class='appointmentButton buttonShadow' value='Miss Appointment' disabled />"+
																"<input type='button' id='cancelButton' class='appointmentButton buttonShadow' value='Cancel Appointment' disabled />"+
																"<br/>"
																);
											},
										});
						
										//Toggle Checked Row
										$j('.dataTables_wrapper tbody tr').live(
												'click',
												function() {
													var table = $j('#appointmentsTable')
															.dataTable();
													var nNodes = table.fnGetNodes();
													for ( var i = 0; i < nNodes.length; i++) {
														$j('input:radio', nNodes[i]).attr(
																'checked', false);
														$j(nNodes[i])
																.removeClass('selectedRow');
														$j(nNodes[i])
																.addClass('notSelectedRow');
													}
													$j('input:radio', this).attr('checked',
															true);
													$j(this).addClass('selectedRow');
													$j(this).removeClass('notSelectedRow');
													var appointmentStatus = $j('td:eq(7)', this).text();
													updateStatusButtonsAvailabilty(appointmentStatus);
										});
							//Initialize status buttons availability
							initStatusButtons();

					});
	//Navigate to appointmentForm.form
	function addNewAppointment(){
		window.location = "appointmentForm.form";
	}
	//Initialize the toDate to the selected fromDate
	function updateToDate(object) {
		if (object.value == '') {
			var fromDate = document.getElementById('fromDate').value.split(" ")[0]+" 23:59";
			if (fromDate != '')
				object.value = fromDate;
		}
		showEndDateTimePicker(object);
	}
	//Init default dates to current day
	function InitDateFilter(){
	 	if(document.getElementById('fromDate').value == "" && document.getElementById('toDate').value == ""){
            var todayStart = new Date();
            todayStart.setHours(0,0,0,0);
            var todayEnd = new Date();
            todayEnd.setHours(23,59,59,999);
            document.getElementById('fromDate').value = getDateTimeFormat(todayStart);
            document.getElementById('toDate').value = getDateTimeFormat(todayEnd);
	}

	//Gets the date format needed
        function getDateTimeFormat(date){
		   	var newFormat = "";
			if((date.getDate()+"").length == 1){
				newFormat += "0";
			}
			newFormat += date.getDate()+"/";
			if(((date.getMonth()+1)+"").length == 1){
				newFormat += "0";
			}
			newFormat += (date.getMonth()+1)+"/"+date.getFullYear();
			newFormat += " "+date.toLocaleTimeString();
			return newFormat;	
         }
}
</script>

<h2>
	<spring:message code="appointment.Appointment.list.manage.title" />
</h2>

<form:form method="post" modelAttribute="selectedAppointment" id="manageAppointmentsForm">
<br />
<br />
<b class="boxHeader"><spring:message
		code="appointment.Appointment.list.filterTitle" /></b>
<fieldset>
<table>
<tr>
	<tr>
		<td class="formLabel"><spring:message
				code="appointment.Appointment.list.label.betweenDates" /></td>
		<td><input type="text" name="fromDate" id="fromDate" size="18"
			value="${param.fromDate}" onfocus="showDateTimePicker(this)" /> <img
			src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png"
			class="calendarIcon" alt=""
			onClick="document.getElementById('fromDate').focus();" /> and <input
			type="text" name="toDate" id="toDate" size="18"
			value="${param.toDate}" onfocus="updateToDate(this)" /> <img
			src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png"
			class="calendarIcon" alt=""
			onClick="document.getElementById('toDate').focus();" /></td>
			<td>
				<!-- TODO handle toDate<fromDate -->
			</td>
	</tr>
	<tr>
		<td class="formLabel"><spring:message
				code="appointment.Appointment.list.label.location" /></td>
		<td><openmrs_tag:locationField formFieldName="locationId"
			initialValue="${selectedLocation}" optionHeader="[blank]" /></td>
	</tr>
	<tr>
			<td class="formLabel"><spring:message
					code="appointment.Appointment.list.label.clinician" /></td>
			<td><select name="providerSelect" id="providerSelect">
					<option value="" ${null==param.providerSelect ? 'selected' : ''}>
						<spring:message
							code="appointment.Appointment.create.label.clinicianNotSpecified" />
					</option>
					<c:forEach var="provider" items="${providerList}">
						<option value="${provider.providerId}"
							${provider.providerId==param.providerSelect ? 'selected' : ''}>${provider.name}</option>
					</c:forEach>
			</select></td>
		</tr>
	</tr>
	<tr>
		<td class="formLabel"><spring:message
				code="appointment.Appointment.create.label.appointmentType" /></td>
		<td>
				<select name="appointmentTypeSelect" id="appointmentTypeSelect">
					<option value="" ${null==param.appointmentTypeSelect ? 'selected' : ''}>
						<spring:message
							code="appointment.Appointment.create.label.clinicianNotSpecified" />
					</option>
					<c:forEach var="appointmentType" items="${appointmentTypeList}" >
						<option value="${appointmentType.appointmentTypeId}" ${appointmentType.appointmentTypeId==param.appointmentTypeSelect ? 'selected' : ''}>${appointmentType.name}</option>
					</c:forEach>
				</select>
		</td>
	</tr>
	<tr>
		<td class="formLabel"><spring:message
				code="appointment.Appointment.create.label.appointmentStatus" /></td>
		<td><select name="appointmentStatusSelect" id="appointmentStatusSelect">
				<option value="" ${null==param.appointmentStatusSelect ? 'selected' : ''}>
					<spring:message
						code="appointment.Appointment.create.label.clinicianNotSpecified" />
				</option>
				<c:forEach var="status" items="${appointmentStatusList}" >
					<option value="${status}" ${status==param.appointmentStatusSelect ? 'selected' : ''}>${status}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr><td colspan="3"><input type="submit" value="<spring:message code='appointment.Appointment.list.button.applyFilters'/>"/></td></tr>
</table>
</fieldset>
<br/>

<b class="boxHeader"><spring:message
		code="appointment.Appointment.list.title" /></b>

	<table id="appointmentsTable">
		<thead>
			<tr>
				<th>Select</th>
				<th><spring:message code='appointment.Appointment.list.column.patient'/></th>
				<th><spring:message code='appointment.Appointment.list.column.date'/></th>
				<th><spring:message code='appointment.Appointment.list.column.time'/></th>
				<th><spring:message code='appointment.Appointment.list.column.clinician'/></th>
				<th><spring:message code='appointment.Appointment.list.column.location'/></th>
				<th><spring:message code='appointment.Appointment.list.column.type'/></th>
				<th><spring:message code='appointment.Appointment.list.column.status'/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="appointment" items="${appointmentList}">
				<tr ${appointment==param.selectAppointment ? 'class="selectedRow"' : 'class="notSelectedRow"'}>
					<td>
						<input type="radio" value="${appointment}" ${param.selectAppointment==appointment ? 'checked' : ''} name="selectAppointment" />
					</td>
					<td>
						<c:forEach var="name" items="${appointment.patient.names}" end="0">
     						<c:out value="${name}" />
						</c:forEach> 
						<c:forEach var="identifier" items="${appointment.patient.identifiers}" >
							<c:if test="${identifier.preferred}">(${identifier})</c:if>
						</c:forEach>
					</td>
					<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"/></td>
					<td><fmt:formatDate type="time" pattern="HH:mm"
										value="${appointment.timeSlot.startDate}" /> - <fmt:formatDate type="time"
										pattern="HH:mm" value="${appointment.timeSlot.endDate}" /></td>
					<td>${appointment.timeSlot.appointmentBlock.provider.name}</td>
					<td>${appointment.timeSlot.appointmentBlock.location.name}</td>
					<td>${appointment.appointmentType.name}</td>
					<td>${appointment.status}</td>
					<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"
										pattern="yyyyMMddHHmm" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>