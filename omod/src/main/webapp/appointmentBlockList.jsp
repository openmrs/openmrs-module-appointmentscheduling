<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />
 
<h2><spring:message code="appointment.AppointmentBlock.manage.title"/></h2>
<br/><br/>

<fieldset style="clear: both"> 
     	<legend><spring:message code="appointment.AppointmentBlock.legend.properties"/></legend>
        <div style="margin: 0.5em 0;">
       			<table>
					<tr>
						<td><spring:message code="appointment.AppointmentBlock.pickDate"/>: </td>
						<td><input type="text" name="appointmentBlockDateFilter" id="dateFilter" size="11" onfocus="showCalendar(this,60)"/></td>
					</tr>
					<tr>
					    <td><spring:message code="appointment.AppointmentBlock.column.location"/>: </td>
					  	<td><openmrs:fieldGen type="org.openmrs.Location" formFieldName="locationId" val="${selectedLocation}"/></td>
					</tr>
				</table>
        </div>
</fieldset>

<br/>
<b class="boxHeader"><spring:message code="appointment.AppointmentBlock.list.title"/></b>
<form method="post" class="box">
	<table>
		<tr>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.select"/></th>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.user"/> </th>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>
			<th align="center"> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>
		</tr>
		<c:forEach var="appointmentBlock" items="${appointmentBlockList}">
			<tr>
				<td align="center"><input type="radio" name="appointmentBlockCheckBox" value=${appointmentBlock.appointmentBlockId }></td>
				<td valign="top">
					<a href="appointmentForm.form?appointmentBlockId=${appointmentBlock.appointmentBlockId}">
						<c:choose>
							<c:when test="${appointmentBlock.voided == true}">
								<del>${appointmentBlock.location}</del>
							</c:when>
							<c:otherwise>
								${appointmentBlock.location.name}
							</c:otherwise>
						</c:choose>
					</a>
				</td>
				<td align="center" valign="top">${appointmentBlock.provider}</td>
				<td align="center">|
						<c:forEach var="appointmentType" items="${appointmentTypeList}">						
								${appointmentType.name}|	 	
						</c:forEach>
				</td>
				<td align="center" valign="top">${appointmentBlock.startDate}</td>
				<td align="center" valign="top">${appointmentBlock.endDate}</td>
			</tr>
		</c:forEach>
	</table>
</form>
<table align="center">
	<tr><td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.add"/></a></td>
	<td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.edit"/></a></td>
	<td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.delete"/></a></td>
	</tr>
</table>



<%@ include file="/WEB-INF/template/footer.jsp" %>