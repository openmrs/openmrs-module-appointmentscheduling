<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/createAppointmentStyle.css" />

<script type="text/javascript">
	function confirmPurge() {
		if (confirm("<spring:message code='appointment.AppointmentBlock.purgeConfirmMessage' />")) {
			return true;
		} else {
			return false;
		}
	}	
	function updateAppointmentTypes(){
        var allAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedTypeToAdd = allAppointmentTypes.options[allAppointmentTypes.selectedIndex];
        var currentAppointmentTypes = document.getElementById("currentApoointmentTypes");
        currentAppointmentTypes.options[currentAppointmentTypes.options.length] = new Option(selectedTypeToAdd.text, selectedTypeToAdd.value);		
	}
</script>

<script type="text/javascript">
	function forceMaxLength(object, maxLength) {
		if (object.value.length >= maxLength) {
			object.value = object.value.substring(0, maxLength);
		}
	}
</script>
<h2>
	<spring:message code="appointment.AppointmentBlock.manage.title" />
</h2>

<spring:hasBindErrors name="appointmentBlock">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<form method="post">
	<fieldset>
		<table>
			<tr>
				<td><spring:message code="appointment.AppointmentBlock.clinician" /></td>
				<td><spring:bind path="appointmentBlock.provider">
					<select name="${status.expression}" id="providerSelect">
					<c:forEach items="${providerList}" var="provider">
						<option value="${provider.providerId}" <c:if test="${provider.providerId == status.value}">selected="selected"</c:if>>
							${provider.name}
						</option>
					</c:forEach>
					</select>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>           
				</spring:bind>
				</td>
			</tr>
			<tr>
				<td><spring:message code="appointment.AppointmentBlock.location" /></td>
				<td><spring:bind path="appointmentBlock.location">
              			      <openmrs_tag:locationField formFieldName="location" initialValue="${status.value}"/>
              			      <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
             			       </spring:bind>
          				     </td>
			</tr>
		
			<tr>
				<td><spring:message code="appointment.AppointmentBlock.appointmentType" /></td>
				<td><select name="appointmentTypeSelect"
					id="appointmentTypeSelect">
						<c:forEach var="appointmentType" items="${appointmentTypeList}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
						</c:forEach>
					</select>
				</td>
				<td>
				<spring:bind path="appointmentBlock.types">
				<select name="${status.expression}" id="currentApoointmentTypes">
						<c:forEach var="appointmentType" items="${appointmentBlock.types}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
						</c:forEach>
				</select>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
				</td>
				<td><input type="button" value="Add Appointment Type" onClick="updateAppointmentTypes()"></td>
			</tr>
			
		
			<tr>
				<td><spring:message code="appointment.AppointmentBlock.timeInterval" /></td>
				<td> 
					<spring:bind path="appointmentBlock.startDate">
					<input type="text" name="startDate" id="startDate" size="16" value="${status.value}"
					onfocus="showDateTimePicker(this)" /> <img
					src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('startDate').focus();" />
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
				<td>
					<spring:bind path="appointmentBlock.endDate">
					<input type="text" name="endDate" id="endDate" size="16" value="${status.value}"
					onfocus="showDateTimePicker(this)" /> <img
					src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('endDate').focus();" />
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
			</tr>

			<c:if test="${!(appointmentBlock.creator == null)}">
			<tr>
				<td><spring:message code="general.createdBy" /></td>
				<td><openmrs:format user="${ appointmentBlock.creator }" /></td>
			</tr>
			</c:if>
		</table>
		<br /> <input type="submit"
			value="<spring:message code="appointment.AppointmentBlock.save"/>"
			name="save">

	</fieldset>
</form>

<br />

<c:if
	test="${not appointmentBlock.voided && not empty appointmentBlock.appointmentBlockId}">
	<form method="post">
		<fieldset>
			<h4>
				<spring:message
					code="appointment.AppointmentBlock.voidAppointmentBlock" />
			</h4>

			<b><spring:message code="general.reason" /></b> <input type="text"
				value="" size="40" name="voidReason" />
			<spring:hasBindErrors name="appointmentBlock">
				<c:forEach items="${errors.allErrors}" var="error">
					<c:if test="${error.code == 'voidReason'}">
						<span class="error"><spring:message
								code="${error.defaultMessage}" text="${error.defaultMessage}" /></span>
					</c:if>
				</c:forEach>
			</spring:hasBindErrors>
			<br /> <input type="submit"
				value='<spring:message code="appointment.AppointmentBlock.voidAppointmentBlock"/>'
				name="void" />
		</fieldset>
	</form>
</c:if>

<c:if
	test="${appointmentBlock.voided && not empty appointmentBlock.appointmentBlockId}">
	<form method="post">
		<fieldset>
			<h4>
				<spring:message
					code="appointment.AppointmentBlock.unretireAppointmentBlock" />
			</h4>
			<input type="submit"
				value='<spring:message code="appointment.AppointmentBlock.unvoidAppointmentBlock"/>'
				name="unvoid" />
		</fieldset>
	</form>
</c:if>

<br />

<c:if test="${not empty appointmentBlock.appointmentBlockId}">
	<form id="purge" method="post" onsubmit="return confirmPurge()">
		<fieldset>
			<h4>
				<spring:message
					code="appointment.AppointmentBlock.purgeAppointmentBlock" />
			</h4>
			<input type="submit"
				value='<spring:message code="appointment.AppointmentBlock.purgeAppointmentBlock"/>'
				name="purge" />
		</fieldset>
	</form>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>