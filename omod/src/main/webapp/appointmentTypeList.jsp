<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2><spring:message code="appointment.AppointmentType.manage.title"/></h2>

<a href="appointmentTypeForm.form"><spring:message code="appointment.AppointmentType.add"/></a>

<br /><br />

<b class="boxHeader"><spring:message code="appointment.AppointmentType.list.title"/></b>
<form method="post" class="box">
	<table>
		<tr>
			<th> <spring:message code="general.name"/> </th>
			<th> <spring:message code="general.description"/> </th>
			<th> <spring:message code="appointment.AppointmentType.duration"/> </th>
		</tr>
		<c:forEach var="appointmentType" items="${appointmentTypeList}">
			<tr>
				<td valign="top">
					<a href="appointmentTypeForm.form?appointmentTypeId=${appointmentType.appointmentTypeId}">
						<c:choose>
							<c:when test="${appointmentType.retired == true}">
								<del>${appointmentType.name}</del>
							</c:when>
							<c:otherwise>
								${appointmentType.name}
							</c:otherwise>
						</c:choose>
					</a>
				</td>
				<td valign="top">${appointmentType.description}</td>
				<td valign="top">${appointmentType.textDuration}</td>
			</tr>
		</c:forEach>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>