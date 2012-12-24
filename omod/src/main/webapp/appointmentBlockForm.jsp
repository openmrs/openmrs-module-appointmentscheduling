<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/appointment/createAppointmentStyle.css"/>
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
	function updateAppointmentTypes(add){
	
        var allAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedAppointmentTypes = document.getElementById("currentApoointmentTypes");
        var from;
        var to;

        if(add==true)
       	{
        	from = allAppointmentTypes;
        	to = selectedAppointmentTypes;
        }
        else
        	{
        		to = allAppointmentTypes;
        		from = selectedAppointmentTypes;
        	}
        
        for(var i=0;i<from.options.length;i++)
        	{
        		if(from.options[i].selected == true)
        			{
        				to[to.options.length] = new Option(from.options[i].text,from.options[i].value);
        				from.remove(i);
        			}
        	
        	}
	}
	function selectAllTypes()
	{
	        var selectedAppointmentTypes = document.getElementById("currentApoointmentTypes");	
	        for(var i=0;i<selectedAppointmentTypes.options.length;i++)
	    	{
			selectedAppointmentTypes.options[i].selected = true;
		}
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
		<table id="appointmentBlockFormTable">
			<tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectClinician"/></td></tr>
				<td><spring:bind path="appointmentBlock.provider">
					<select name="${status.expression}" id="providerSelect">
					<c:forEach items="${providerList}" var="provider">
						<option value="${provider.providerId}" <c:if test="${provider.providerId == status.value}">selected="selected"</c:if>>${provider.name}</option>
					</c:forEach>
					</select>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>           
				</spring:bind>
				</td>
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectLocation"/></td></tr>
			<tr>
				<td><spring:bind path="appointmentBlock.location">
              			      <openmrs_tag:locationField formFieldName="location" initialValue="${status.value}"/>
              			      <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
             			       </spring:bind>
          				     </td>
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectAppointmentTypes"/></td></tr>
			<tr>
				<td><fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.availableTypes"/></legend>
				<select multiple name="appointmentTypeSelect"
					id="appointmentTypeSelect">
						<c:forEach var="appointmentType" items="${appointmentTypeList}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
						</c:forEach>
					</select></fieldset>
				</td>
				<td>
				<table>
				<tr><td align="center"><input type="button" id="addButton" value="->" onClick="updateAppointmentTypes(true)"></td></tr>
				<tr><td align="center"><input type="button" id="removeButton" value="<-" onClick="updateAppointmentTypes(false)"></td></tr>	
				</table>
				</td>
				<td>
				<fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.chosenTypes"/></legend>
				<spring:bind path="appointmentBlock.types">
				<select multiple name="${status.expression}" id="currentApoointmentTypes">
						<c:forEach var="appointmentType" items="${appointmentBlock.types}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
						</c:forEach>
				</select>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>
				</fieldset>
				</td>
			</tr>
			<tr></tr>

			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectTimeInterval"/></td></tr>
		
			<tr>
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
				<td></td>
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
<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.defineTimeSlotLength"/></td></tr>
			<tr>
				<td><input type="text" name="timeSlotLength" id="timeSlotLength" value="${timeSlotLength}" size="24" /></td>
			</tr>
			<c:if test="${!(appointmentBlock.creator == null)}">
			<tr>
				<td><spring:message code="general.createdBy" /></td>
				<td><openmrs:format user="${ appointmentBlock.creator }" /></td>
			</tr>
			</c:if>
		</table>
		<br /> <input type="submit" class="appointmentButton" onClick="selectAllTypes()"
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
			<br /> <input type="submit" class="appointmentButton" 
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
					code="appointment.AppointmentBlock.unvoidAppointmentBlock" />
			</h4>
			<input type="submit" class="appointmentButton" 
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
			<input type="submit" class="appointmentButton" 
				value='<spring:message code="appointment.AppointmentBlock.purgeAppointmentBlock"/>'
				name="purge" />
		</fieldset>
	</form>
</c:if>

<%@ include file="/WEB-INF/template/footer.jsp"%>