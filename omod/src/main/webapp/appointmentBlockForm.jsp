<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/appointment/Styles/AppointmentBlockStyle.css"/>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointment/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css" />
	
<script type="text/javascript">
	function confirmPurge() {
		if (confirm("<spring:message code='appointment.AppointmentBlock.purgeConfirmMessage' />")) {
			return true;
		} else {
			return false;
		}
	}	
	function updateAppointmentTypes(add){
        var availableAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");
        var from;
        var to;

        if(add==true)
       	{
        	from = availableAppointmentTypes;
        	to = selectedAppointmentTypes;
        }
        else
        	{
        		to = availableAppointmentTypes;
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
	        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");	
	        for(var i=0;i<selectedAppointmentTypes.options.length;i++)
	    	{
				selectedAppointmentTypes.options[i].selected = true;
		    }
	        if(selectedAppointmentTypes.options.length == 0){
	        	document.getElementById("emptyTypes").value = "yes";
	        }
	}
	function updateAvailableAppointmentTypes(){
        var availableAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");
		var i=0;
		var match = false;
		while(i<availableAppointmentTypes.options.length){
        	for(var j=0;j<selectedAppointmentTypes.options.length;j++){
        		//check if the appointment type is already selected
        		if(availableAppointmentTypes[i].value == selectedAppointmentTypes[j].value){
					match = true;
        			//remove from available because the type is already selected
        			availableAppointmentTypes.remove(i);
					break;
        		}
        	}
			if(!match)
				i++;
			match = false;			
    	}
	}
	function updateToDate(object) {
		if (object.value == '') {
			var startDate = document.getElementById('startDate').value;
			if (startDate != '')
				object.value = startDate;
		}
		showDateTimePicker(object);
	}
    $j(document).ready(function() { 
    	updateAvailableAppointmentTypes();
    });
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
				</td>
				<td>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>    
				</td>       
				</spring:bind>
				
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectLocation"/></td></tr>
			<tr>
				<td><spring:bind path="appointmentBlock.location">
              			      <openmrs_tag:locationField formFieldName="location" initialValue="${status.value}"/>
           			         </td>
           			         <td></td>
              			      <td>
              			      <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
             			       </spring:bind>
          				     </td>
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectAppointmentTypes"/></td></tr>
			<tr>
				<td>
					<fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.availableTypes"/></legend>
					<select multiple name="appointmentTypeSelect" ondblclick="updateAppointmentTypes(true)"
						id="appointmentTypeSelect">
							<c:forEach var="appointmentType" items="${appointmentTypeList}">
							<option value="${appointmentType.appointmentTypeId}">${appointmentType.name}</option>
							</c:forEach>

						</select></fieldset>
				</td>
				<td>
				<table>
				<tr><td align="center"><input type="button" class="appointmentBlockButton" id="addButton" value="->" onClick="updateAppointmentTypes(true)"></td></tr>
				<tr><td align="center"><input type="button" class="appointmentBlockButton" id="removeButton" value="<-" onClick="updateAppointmentTypes(false)"></td></tr>
				</table>
				</td>
				<td>

				<spring:bind path="appointmentBlock.types">
				<fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.chosenTypes"/></legend>
				<select multiple name="${status.expression}" ondblclick="updateAppointmentTypes(false)" id="currentAppointmentTypes">
						<c:forEach var="appointmentType" items="${appointmentBlock.types}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
						</c:forEach>
				</select>
				</fieldset>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>

				</td>
			</tr>
			<tr></tr>

			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.selectTimeInterval"/></td></tr>
		
			<tr>
				<td>
					<spring:bind path="appointmentBlock.startDate">
                    <fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.startDate"/></legend>
					<input type="text" name="startDate" id="startDate" size="16" value="${status.value}"
					onfocus="showDateTimePicker(this)" /> <img
					src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('startDate').focus();" />
					</fieldset>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
				<td></td>
				<td>
					<spring:bind path="appointmentBlock.endDate">
                    <fieldset align="center"><legend><spring:message code="appointment.AppointmentBlock.endDate"/></legend>
					<input type="text" name="endDate" id="endDate" size="16" value="${status.value}"
					onfocus="updateToDate(this)" /> <img
					src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('endDate').focus();" />
                    </fieldset>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
			</tr>
<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointment.AppointmentBlock.steps.defineTimeSlotLength"/></td></tr>
			<tr>
				<td><input type="text" name="timeSlotLength" id="timeSlotLength" value="${timeSlotLength}" size="8" />
				<spring:message code="appointment.AppointmentBlock.minutes"/></td>
			</tr>
		</table>
		<br /> <input type="submit" class="appointmentBlockButton" onClick="selectAllTypes()"
			value="<spring:message code="appointment.AppointmentBlock.save"/>"
			name="save">
		
	</fieldset>
		<input type="hidden" name="emptyTypes" id="emptyTypes" value="no" />
		<input type="hidden" name="appointmentBlockObject" id="appointmentBlockObject" value="${appointmentBlock}" />
</form>

<br />


<%@ include file="/WEB-INF/template/footer.jsp"%>