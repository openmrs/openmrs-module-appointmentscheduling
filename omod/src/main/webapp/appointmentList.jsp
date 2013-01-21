<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>


<openmrs:htmlInclude file="/moduleResources/appointment/Scripts/timepicker.js" />
<openmrs:htmlInclude file="/moduleResources/appointment/Scripts/date.format.js" />
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
	var timeout = null;

	$j(document)
			.ready(
					function() {		
						//Hide errors div
						$j('#errorsDiv').hide();
						
						//Init Dates FIltering
						InitDateFilter();

						//Prevent date keyboard input
						$j("#fromDate").keypress(function(event) {event.preventDefault();});
						$j("#toDate").keypress(function(event) {event.preventDefault();});
						
						TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointment/TableTools/media/swf/ZeroClipboard.swf";

						//Datatables.net
						var oTable = $j('#appointmentsTable')
								.dataTable(
										{
											"aoColumns" : [ {
												"bVisible"  : true												
											}, { 
												"bSortable" : true
											}, {
												"iDataSort" : 9
											}, {
												"iDataSort" : 9
											} ,{
												"bSortable" : true
											}, {
												"bSortable" : true
											}, {
												"bSortable" : true
											} ,{
												"bSortable" : true
											} ,{
												"iDataSort" : 10
											}, {
												"bVisible" : false
											}, {
												"bVisible" : false
											}],
											"oLanguage": {
		 										"sZeroRecords": "<spring:message code='appointment.Appointment.list.table.empty' />"
											},
											"aLengthMenu" : [
													[25, 50, -1 ],
													[25, 50, "All" ] ],
											"iDisplayLength" : 25,
											"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix' <'addons'>fl>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'<'statusDiv'>ip<'toolbar' T>>",

											"bLengthChange" : true,
											"bFilter" : true,
											"bInfo" : true,
											"bPaginate" : true,
											"bJQueryUI" : true,

											"fnDrawCallback" : function() {
												//Clear and prepend the schedule appointment button and cancelled checkbox
												$j(".addons").html("");
												$j(".addons")
														.prepend(
																"<table style='margin:10px; float:right; display:inline-block;' >"+
																	"<tr><td>"+
																		"<input type=\"checkbox\" name=\"includeCancelled\" value=\"true\" onchange='this.form.submit();' ${(param.includeCancelled=='true' || param.appointmentStatusSelect=='Cancelled') ? 'checked' : ''}>"+
																			"<spring:message code='appointment.Appointment.list.label.showCancelled' />"+
																	"</td></tr>"+
																	"<tr><td>"+
																		"<input type='button' value='<spring:message code='appointment.Appointment.add'/>' class='saveButton buttonShadow' onclick='addNewAppointment()'/>"+
																	"</tr></td>"+
																"</table>");
												//Clear and prepend the status buttons
												$j(".statusDiv").html("");
												$j('.statusDiv')
														.prepend(
																"<input type='submit' name='startConsultation' id='startConsultButton' class='appointmentButton buttonShadow' value='<spring:message code='appointment.Appointment.list.button.startConsultation'/>' disabled />"+
																"<input type='submit' name='endConsultation' id='endConsultButton' class='appointmentButton buttonShadow' value='<spring:message code='appointment.Appointment.list.button.endConsultation'/>' disabled />"+
																"<input type='submit' name='checkIn' id='checkInButton' class='appointmentButton buttonShadow' style='margin-left:16px; margin-right:16px;' value='<spring:message code='appointment.Appointment.list.button.checkIn'/>' disabled />"+
																"<input type='submit' name='missAppointment' id='missButton' class='appointmentButton buttonShadow' value='<spring:message code='appointment.Appointment.list.button.missAppointment'/>' disabled />"+
																"<input type='submit' name='cancelAppointment' id='cancelButton' class='appointmentButton buttonShadow' value='<spring:message code='appointment.Appointment.list.button.cancelAppointment'/>' disabled />"+
																"<br/>"
																);
											},
										});
						
										//Toggle Checked Row
										$j('#appointmentsTable tbody tr').live(
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
											
							//Default sort: ascending by date
							oTable.fnSort([[2,'asc']]);	
									
							//If the user is using "Simple" version
							if ($j('#locationId').length > 0) {
								var selectLocation = $j('#locationId');
								//Set the Null option text (Default is empty string)
								if (selectLocation[0][0].innerHTML == '')
									selectLocation[0][0].innerHTML = "<spring:message code='appointment.Appointment.create.label.locationNotSpecified'/>";

							}
							
							//Initialize status buttons availability
							initStatusButtons();
							
							//Init timeout time
							var propertyValue = "<openmrs:globalProperty key='appointment.manageAppointmentsFormTimout' />";
							if(propertyValue!=null){
								timeout = parseInt(propertyValue) * 1000;
								timeout = (timeout>0 && timeout<60000) ? 60000 : timeout;
								//Minimum 60 seconds
								if(timeout>=60000){
									window.setInterval(function() {
										if($j("#patientDialog").dialog( "isOpen" )==false){
											document.forms['manageAppointmentsForm'].submit();
										}
									}, timeout);
								}
							}
							
								
							//Dialog
							//Register live to find out who opened the dialog
							$j('a').live('click',function(){
							  $j('#patientDialog').data('opener', this);
							});
							//Configure dialog
							$j('#patientDialog').dialog({
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
										if(navigationURL=="startConsultation"){
											//Simluate StartConsultButton and post the form
											$j('#manageAppointmentsForm').append("<input type='hidden' name='startConsultation' value='' />");
											$j('#manageAppointmentsForm').submit();
										}
										else{
											var appointmentId = $j('input[name="selectAppointment"]:radio:checked')[0].value;
											var patientId = $j('#patientId'+appointmentId)[0].value;
											window.location = navigationURL + patientId;
										}
									}
								},
								open: function(){
									//Clear previous selection
									var selectedOption = $j('input[name="selectDialogAction"]:radio:checked');
									if(selectedOption!=null)
										selectedOption.attr('checked', false);
									//Select first as default
									$j('input[name="selectDialogAction"]:radio:first').attr('checked', true);
									
									//Check whether to show the option to start consultation
									var table = $j('#appointmentsTable')
											.dataTable();
									var nNodes = table.fnGetNodes();
									var selectedRow = null;
									var showStartConsultOption = false;
									for ( var i = 0; i < nNodes.length; i++) {
										if($j('input:radio', nNodes[i]).attr('checked')){
											var appointmentStatus = $j('td:eq(7)', nNodes[i]).text();
											appointmentStatus = appointmentStatus.toLowerCase();
											appointmentStatus = appointmentStatus.replace("-","");
											//Check whether current status can change to "In-Consultation"
											if(statusButtons['startConsultButton'][appointmentStatus])
												showStartConsultOption = true;
											break;
										}
									}
									if(showStartConsultOption==true)
										$j('#startConsultOption').css("display", "table-row");
									else{
										$j('#startConsultOption').css("display", "none");
										$j('#editPatientOption:input:radio').attr("checked",true);
									}
								}
							});
							

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
			var fixedjsTimeFormat = jsTimeFormat.replace('m','M');
			fixedjsTimeFormat = fixedjsTimeFormat.replace('m','M');
			
			var timeFormat = (fixedjsTimeFormat=='hh:MM') ? 'HH:MM' : fixedjsTimeFormat;
			//Handle Italy locale
			timeFormat = (fixedjsTimeFormat=='hh.MM') ? 'HH.MM' : timeFormat;
			
            document.getElementById('fromDate').value = todayStart.format(jsDateFormat+' '+timeFormat);
            document.getElementById('toDate').value = todayEnd.format(jsDateFormat+' '+timeFormat);
		}
	}

	//validate startDate<toDate
	function validateDates(){
		var fromDate = new Date($j('#fromDate')[0].value);
		var toDate = new Date($j('#toDate')[0].value);
		if(toDate!=null && fromDate!=null && toDate<fromDate){
			$j('#errorsDiv').show();
			$j('#errorsDiv').html("<spring:message code='appointment.Appointment.error.InvalidDateInterval' />");
			return false;
		}
		else
			return true;
	}
	
	//Correctly execute click event on patient name (parent event before child event)
	function patientClick(e,event) {
		var currentRow = $j(e).parent().parent();
		currentRow.click();
		$j('#patientDialog').dialog('open');
		event.stopPropagation();
	}
	
</script>

<h2>
	<spring:message code="appointment.Appointment.list.manage.title" />
</h2>
<form:form method="post" modelAttribute="selectedAppointment" id="manageAppointmentsForm" onsubmit='return validateDates()'>

<br />
<br />
<b class="boxHeader"><spring:message
		code="appointment.Appointment.list.filterTitle" /></b>
<div class="error" id="errorsDiv" ></div>
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
							${provider.providerId==providerSelect.providerId ? 'selected' : ''}>${provider.name}</option>
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
				code="appointment.Appointment.list.label.appointmentStatus" /></td>
		<td><select name="appointmentStatusSelect" id="appointmentStatusSelect">
				<option value="" ${null==param.appointmentStatusSelect ? 'selected' : ''}>
					<spring:message
						code="appointment.Appointment.create.label.clinicianNotSpecified" />
				</option>
				<c:forEach var="status" items="${appointmentStatusList}" >
					<option value="${status}" ${status==param.appointmentStatusSelect ? 'selected' : ''}>${status.name}</option>
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
				<th style="display:none;">Select</th>
				<th><spring:message code='appointment.Appointment.list.column.patient'/></th>
				<th><spring:message code='appointment.Appointment.list.column.date'/></th>
				<th><spring:message code='appointment.Appointment.list.column.time'/></th>
				<th><spring:message code='appointment.Appointment.list.column.clinician'/></th>
				<th><spring:message code='appointment.Appointment.list.column.location'/></th>
				<th><spring:message code='appointment.Appointment.list.column.type'/></th>
				<th><spring:message code='appointment.Appointment.list.column.status'/></th>
				<th><spring:message code='appointment.Appointment.list.column.waitingTime'/></th>
				<th>Hidden sortable dates</th>
				<th>Hidden sortable waiting time</th>
			</tr>
		</thead>
		<tbody>
			<!-- "Ghost" line to resolve first row auto selection bug, c:if in order to prevent from not showing empty table message -->
			<c:if test="${fn:length(appointmentList)>0}" >
				<tr style="display:none;"><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td></tr>
			</c:if>
			<c:forEach var="appointment" items="${appointmentList}">
				<tr ${appointment.appointmentId==param.selectAppointment ? 'class="selectedRow"' : 'class="notSelectedRow"'}>
					<td style="display:none;">
						<input type="radio" value="${appointment.appointmentId}" ${param.selectAppointment==appointment.appointmentId ? 'checked' : ''} name="selectAppointment" />
					</td>
					<td>
						<a href="javascript:void(0)" onclick="patientClick(this, event)">
							<c:forEach var="name" items="${appointment.patient.names}" end="0">
	     						<c:out value="${name}" />
							</c:forEach> 
							<c:forEach var="identifier" items="${appointment.patient.identifiers}" >
								<c:if test="${identifier.preferred}">(${identifier})</c:if>
							</c:forEach>
						</a>
						<input type="hidden" id="patientId${appointment.appointmentId}" value="${appointment.patient.patientId}" />
					</td>
					<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"/></td>
					<td><fmt:formatDate type="time" pattern="HH:mm"
										value="${appointment.timeSlot.startDate}" /> - <fmt:formatDate type="time"
										pattern="HH:mm" value="${appointment.timeSlot.endDate}" /></td>
					<td>${appointment.timeSlot.appointmentBlock.provider.name}</td>
					<td>${appointment.timeSlot.appointmentBlock.location.name}</td>
					<td>${appointment.appointmentType.name}</td>
					<td>${appointment.status.name}</td>
					<td>${waitingTimes[appointment.appointmentId]}</td>
					<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"
										pattern="yyyyMMddHHmm" /></td>
					<td>${sortableWaitingTimes[appointment.appointmentId]}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form:form>

<div id="patientDialog" >
	<table id='patientDialogOptions' class="dialogTable">
		<tr><td><h2><spring:message code='appointment.Appointment.list.label.selectAnAction'/></h2></td></tr>
		<tr><td><div id="startConsultOption" style="display:none;" ><input type='radio' name='selectDialogAction'value='startConsultation'><spring:message code='appointment.Appointment.list.button.startConsultation' /></div></td></tr>
		<tr><td><input type="radio" id="editPatientOption" name="selectDialogAction" value="${pageContext.request.contextPath}/admin/patients/shortPatientForm.form?patientId="><spring:message code='appointment.Appointment.create.link.editPatient' /></input></td></tr>
		<tr><td><input type="radio" id="viewPatientOption" name="selectDialogAction" value="${pageContext.request.contextPath}/patientDashboard.form?patientId="><spring:message code='appointment.Appointment.create.link.viewPatient' /></input></td></tr>
		</table>
</div>

<%@ include file="/WEB-INF/template/footer.jsp"%>