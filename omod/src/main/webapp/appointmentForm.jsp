<%@ include file="/WEB-INF/template/include.jsp" %>

<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />

<script type="text/javascript">
   function forceMaxLength(object, maxLength) {
      if( object.value.length >= maxLength) {
         object.value = object.value.substring(0, maxLength); 
      }
   }
</script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>
<script type="text/javascript">
   function updatePatient(formFieldId, patientObj, isPageLoad) {
	if(!isPageLoad){
		addPatientLink(patientObj);
		DWRAppointmentService.getPatientDescription(patientObj.patientId, function(details){
			if(!details){
				document.getElementById('patientDataCell').innerHTML = "";
				return;
			}
			var phone = "<spring:message code='appointment.Appointment.create.patientNoPhoneNumber'/>";
			var dateMissedLastAppointment = "<spring:message code='appointment.Appointment.create.patientNotMissedLastAppointment'/>";
			
			if(details.phoneNumber)
				phone = details.phoneNumber;
			if(details.dateMissedLastAppointment)
				dateMissedLastAppointment = details.dateMissedLastAppointment;
			
			var detailsText = "<spring:message code='appointment.Appointment.create.patientPhoneNumber'/>"+phone+"<br/><spring:message code='appointment.Appointment.create.patientMissedMeeting'/>"+dateMissedLastAppointment;
			document.getElementById('patientDataCell').innerHTML = detailsText;
		});
	}
   }
   
   function addPatientLink(patientObj){
	   if(patientObj!=null){
		   var message = "<spring:message code='appointment.Appointment.create.link.viewPatient'/>";
		   var link = "<a href='${pageContext.request.contextPath}/patientDashboard.form?patientId="+patientObj.patientId+"'>";
		   document.getElementById('patientLinkCell').innerHTML = link+message+"</a>";
	   }
	   else
		   document.getElementById('patientLinkCell').innerHTML = "";
   }
</script>

<h2><spring:message code="appointment.Appointment.create.title"/></h2>

<table>
	<tr>
		<td><spring:message code="appointment.Appointment.create.label.findPatient"/></td>
		
		<td>
			<spring:bind path="appointment.patient"><openmrs_tag:patientField formFieldName="patientId" callback="updatePatient"/></spring:bind>
		</td>
		<td id="patientLinkCell"></td>
	</tr>
	<tr>
		<td></td>
		<td colspan="2" id="patientDataCell">
			
		</td>
	</tr>

	<tr>
		<td><spring:message code="appointment.Appointment.create.label.appointmentType"/></td>
		<td>
			<select name="appointmentTypeSelect">
				<c:forEach var="appointmentType" items="${appointmentTypeList}">
					<option value="${appointmentType.appointmentTypeId}">${appointmentType.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td><spring:message code="appointment.Appointment.create.label.clinician"/></td>
		<td>
			<select name="providerSelect">
				<option value="0">Not Specified</option>
				<c:forEach var="provider" items="${providerList}">
					<option value="${appointmentType.providerId}">${provider.name}</option>
				</c:forEach>
			</select>
		</td>
	</tr>
	<tr>
		<td><spring:message code="appointment.Appointment.create.label.betweenDates"/></td>
		<td><input type="text" name="Date" id="fromDate" size="15" value="" onfocus="showDateTimePicker(this)"/> and <input type="text" name="Date" id="toDate" size="15" value="" onfocus="showDateTimePicker(this)"/></td>
	</tr>
	<tr>
		<td/><td><input type="button" value="<spring:message code="appointment.Appointment.create.findTime"/>" name="cancel"></td>
	</tr>
	<tr>
		<td><spring:message code="appointment.Appointment.create.label.availbleTimes"/></td>
		<td>List of Times</td>
	</tr>
	<tr>
		<td><spring:message code="appointment.Appointment.create.label.reason"/></td>
		<td><textarea name="description" rows="3" cols="40" onkeypress="return forceMsaxLength(this, 1024);"></textarea></td>
	</tr>
	<tr><td><input type="submit" value="<spring:message code="appointment.Appointment.create.save"/>" name="save"></td>
	<td><input type="reset" value="<spring:message code="appointment.Appointment.create.cancel"/>" name="cancel"></td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>