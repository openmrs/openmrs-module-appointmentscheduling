<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/appointment/Scripts/jquery.dataTables.js" />
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
						TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointment/TableTools/media/swf/ZeroClipboard.swf";

						//Datatables.net
						$j('#appointmentsTable')
								.dataTable(
										{
											"aoColumns" : [ {
												"iDataSort" : 7
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
													[25, 50, -1 ],
													[25, 50, "All" ] ],
											"iDisplayLength" : 25,
											"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix'l<'addons'>>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'ip<'toolbar' T>>",

											"bLengthChange" : true,
											"bFilter" : false,
											"bInfo" : true,
											"bPaginate" : true,
											"bJQueryUI" : true,

											"fnDrawCallback" : function() {
												$j(".addons").html("");
												$j(".addons")
														.prepend(
																"<input type='button' value='<spring:message code='appointment.Appointment.add'/>' class='saveButton' style='margin:10px; float:right; display:block;' onclick='addNewAppointment()'/>");
											},
										});

					});
	function addNewAppointment(){
		window.location = "appointmentForm.form";
	}
</script>

<h2>
	<spring:message code="appointment.Appointment.list.manage.title" />
</h2>

<br />
<br />

<b class="boxHeader"><spring:message
		code="appointment.Appointment.list.title" /></b>
<form method="post" class="box">
	<table id="appointmentsTable">
		<thead>
			<tr>
				<th><spring:message code='appointment.Appointment.list.column.date'/></th>
				<th><spring:message code='appointment.Appointment.list.column.time'/></th>
				<th><spring:message code='appointment.Appointment.list.column.patient'/></th>
				<th><spring:message code='appointment.Appointment.list.column.clinician'/></th>
				<th><spring:message code='appointment.Appointment.list.column.location'/></th>
				<th><spring:message code='appointment.Appointment.list.column.type'/></th>
				<th><spring:message code='appointment.Appointment.list.column.status'/></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="appointment" items="${appointmentList}">
				<tr>
					<td><fmt:formatDate type="date" value="${appointment.timeSlot.startDate}"/></td>
					<td><fmt:formatDate type="time" pattern="HH:mm"
										value="${appointment.timeSlot.startDate}" /> - <fmt:formatDate type="time"
										pattern="HH:mm" value="${appointment.timeSlot.endDate}" /></td>
					<td>
						<c:forEach var="name" items="${appointment.patient.names}" end="0">
     						<c:out value="${name}" />
						</c:forEach> 
						<c:forEach var="identifier" items="${appointment.patient.identifiers}" >
							<c:if test="${identifier.preferred}">(${identifier})</c:if>
						</c:forEach>
					</td>
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
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>