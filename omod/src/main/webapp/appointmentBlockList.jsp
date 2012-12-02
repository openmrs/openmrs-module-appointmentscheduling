<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
 <openmrs:htmlInclude file="/scripts/calendar/calendar.js" />

 
<h2><spring:message code="appointment.AppointmentBlock.manage.title"/></h2>

<br /><br />
<table>
	<tr>
		<td><input type="text" name="Date" id="dateFilter" size="11" value="" onfocus="showCalendar(this,60)"/></td>
	</tr>
</table>
<br/>

<b class="boxHeader"><spring:message code="appointment.AppointmentBlock.list.title"/></b>
<form method="post" class="box">
	<table>
		<tr>
			<th> <spring:message code="appointment.AppointmentBlock.column.check"/></th>
			<th> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>
			<th> <spring:message code="appointment.AppointmentBlock.column.user"/> </th>
			<th> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>
			<th> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>
			<th> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>
		</tr>
		<c:forEach var="appointmentBlock" items="${appointmentBlockList}">
			<tr>
				<td><spring:message code="appointment.AppointmentBlock.column.checkbox"/></td>
				<td valign="top">
					<a href="appointmentBlockForm.form?appointmentBlockId=${appointmentBlock.appointmentBlockId}">
						<c:choose>
							<c:when test="${appointmentBlock.voided == true}">
								<del>${appointmentBlock.location}</del>
							</c:when>
							<c:otherwise>
								${appointmentBlock.location}
							</c:otherwise>
						</c:choose>
					</a>
				</td>
				<td valign="top">${appointmentBlock.provider}</td>
				<td>
					<c:forEach var="appointmentType" items="${appointmentTypeList}">
						${appointmentType.name} 					
					</c:forEach>
				</td>
				<td valign="top">${appointmentBlock.startDate}</td>
				<td valign="top">${appointmentBlock.endDate}</td>
			</tr>
		</c:forEach>
	</table>
</form>

<br/> <br/>
<a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.add"/></a>
<a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.edit"/></a>
<a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.delete"/></a>

<%@ include file="/WEB-INF/template/footer.jsp" %>