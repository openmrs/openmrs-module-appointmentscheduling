<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ include file="localHeader.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/appointment/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/createAppointmentStyle.css" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/appointmentTypeList_jQueryDatatable.css" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css" />

<script type="text/javascript">
	$j(document)
			.ready(
					function() {
						//Datatables.net
						$j('#appointmentTypesTable')
								.dataTable(
										{
											"aoColumns" : [ {
												"bSortable" : true
											}, {
												"bSortable" : false
											}, {
												"bSortable" : true
											} ],
											"aLengthMenu" : [
													[ 5, 10, 25, 50, -1 ],
													[ 5, 10, 25, 50, "All" ] ],
											"iDisplayLength" : 5,
											"bLengthChange" : true,
											"bFilter" : false,
											"bInfo" : true,
											"bPaginate" : true,
											"bJQueryUI" : true,

											"fnDrawCallback" : function() {
												$j("#appointmentTypesTable_length")
														.prepend(
																"<input type='button' value='<spring:message code='appointment.AppointmentType.add'/>' class='saveButton' style='margin:10px; float:right; display:block;' onclick='addNewAppointmentType()'/>");
											},
										});

					});
	function addNewAppointmentType(){
		window.location = "appointmentTypeForm.form;";
	}
</script>

<h2>
	<spring:message code="appointment.AppointmentType.manage.title" />
</h2>

<br />
<br />

<b class="boxHeader"><spring:message
		code="appointment.AppointmentType.list.title" /></b>
<form method="post" class="box">
	<table id="appointmentTypesTable">
		<thead>
			<tr>
				<th><spring:message code="general.name" /></th>
				<th><spring:message code="general.description" /></th>
				<th><spring:message code="appointment.AppointmentType.duration" />
				</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="appointmentType" items="${appointmentTypeList}">
				<tr>
					<td valign="top"><a
						href="appointmentTypeForm.form?appointmentTypeId=${appointmentType.appointmentTypeId}">
							<c:choose>
								<c:when test="${appointmentType.retired == true}">
									<del>${appointmentType.name}</del>
								</c:when>
								<c:otherwise>
								${appointmentType.name}
							</c:otherwise>
							</c:choose>
					</a></td>
					<td valign="top">${appointmentType.description}</td>
					<td valign="top">${appointmentType.duration}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>