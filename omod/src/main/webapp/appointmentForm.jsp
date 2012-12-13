<%@ include file="/WEB-INF/template/include.jsp" %>
 
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/moduleResources/appointment/createAppointmentStyle.css"/>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        if(patientObj!=null){
                addPatientLink(patientObj);
                DWRAppointmentService.getPatientDescription(patientObj.patientId, function(details){
                        if(!details){
                                document.getElementById('patientDataCell').innerHTML = "";
                                return;
                        }
                        var phone = "<spring:message code='appointment.Appointment.create.patientNoPhoneNumber'/>";
                        var dateMissedLastAppointment = "<spring:message code='appointment.Appointment.create.patientNotMissedLastAppointment'/>";
                        var patientId = details.patientId;
                        if(details.phoneNumber)
                                phone = details.phoneNumber;
                        if(details.dateMissedLastAppointment)
                                dateMissedLastAppointment = details.dateMissedLastAppointment;
                       
                        var detailsText ="<spring:message code='appointment.Appointment.create.patientId'/>"+patientId+"<br/><spring:message code='appointment.Appointment.create.patientPhoneNumber'/>"+phone+"<br/><spring:message code='appointment.Appointment.create.patientMissedMeeting'/>"+dateMissedLastAppointment;
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
 
<h2 id="headline"><spring:message code="appointment.Appointment.create.title"/></h2>
<spring:hasBindErrors name="appointment">
        <spring:message code="fix.error"/>
        <br />
</spring:hasBindErrors>
<form:form modelAttribute="appointment" method="post">
<fieldset>
<table id="createAppointmentTable">
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.findPatient"/></td>
               
                <td>
                        <spring:bind path="appointment.patient">
                                <openmrs_tag:patientField formFieldName="patient" callback="updatePatient" initialValue="${status.value}"/>
                                <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
                        </spring:bind>
                </td>
                <td id="patientLinkCell"></td>
        </tr>
        <tr>
                <td></td>
                <td colspan="2" id="patientDataCell">
                       <script type="text/javascript" id="patientDataTemplate">
                       		<spring:message code='appointment.Appointment.create.patientId'/>${patientId}<br/>
                       		<spring:message code='appointment.Appointment.create.patientPhoneNumber'/>${phoneNumber}<br/>
                       		<spring:message code='appointment.Appointment.create.patientMissedMeeting'/>${dateMissedLastAppointment};
                       </script>
                </td>
        </tr>
       
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.appointmentType"/></td>
                <td>
                        <select name="appointmentTypeSelect" id="appointmentTypeSelect">
                                <c:forEach var="appointmentType" items="${appointmentTypeList}">
                                        <option value="${appointmentType.appointmentTypeId}" ${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}>${appointmentType.name}</option>
                                </c:forEach>
                        </select>
                </td>
        </tr>
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.clinician"/></td>
                <td>
                        <select name="providerSelect" id="providerSelect">
                                <option value="" ${null==param.providerSelect ? 'selected' : ''}><spring:message code="appointment.Appointment.create.label.clinicianNotSpecified"/></option>
                                <c:forEach var="provider" items="${providerList}">
                                        <option value="${provider.providerId}"  ${provider.providerId==param.providerSelect ? 'selected' : ''} >${provider.name}</option>
                                </c:forEach>
                        </select>
                </td>
        </tr>
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.betweenDates"/></td>
                <td>
                	<input type="text" name="fromDate" id="fromDate" size="16" value="${param.fromDate}" onfocus="showDateTimePicker(this)"/>
                	<img src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('fromDate').focus();"/>
                	 and 
                	 <input type="text" name="toDate" id="toDate" size="16" value="${param.toDate}" onfocus="showDateTimePicker(this)"/>
                	 <img src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('toDate').focus();"/>
               	 </td>
        </tr>
        <tr>
                <td/><td><input type="submit" name="findAvailableTime" class="appointmentButton" value="<spring:message code="appointment.Appointment.create.findTime"/>"></td>
		<td>
			<spring:bind path="appointment.timeSlot">
				 <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
			</spring:bind>
		</td>
        </tr>
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.availableTimes"/></td>
                <td>			
                	<div id="timeSlotsDIV">
                                <table id="availbleTimesTable" cellspacing="0">
                                        <tr class="tableHeader">
                                                <th><spring:message code="appointment.Appointment.create.header.selectedOption"/></th>
                                                <th><spring:message code="appointment.Appointment.create.header.clinician"/></th>
                                                <th><spring:message code="appointment.Appointment.create.header.appointmentType"/></th>
                                                <th><spring:message code="appointment.Appointment.create.header.date"/></th>
                                                <th><spring:message code="appointment.Appointment.create.header.timeSlot"/></th>
                                        </tr>
										<% int count=0; %>
                                        <c:forEach var="slot" items="${availableTimes}">
                                                <tr style=<%= count++ % 2==0 ? "background-color:#ffffff":"background-color:#D8D8D8" %>;>
                                                        <td>
                                                                <spring:bind path="appointment.timeSlot">
                                                                        <input type="radio" name="${status.expression}"  value="${slot.timeSlotId}"  ${slot.timeSlotId==appointment.timeSlot.timeSlotId ? 'checked' : ''} />
                                                                </spring:bind>
                                                        </td>
                                                       
                                                        <td>${slot.appointmentBlock.provider.name}</td>
                                                        <td>
                                                                <c:forEach var="appointmentType" items="${slot.appointmentBlock.types}">
                                                                ${appointmentType.name},
                                                                </c:forEach>
                                                        </td>
                                                        <td><fmt:formatDate type="date" value="${slot.startDate}" /></td>
                                                        <td><fmt:formatDate type="time" pattern="hh:mm a" value="${slot.startDate}" /> - <fmt:formatDate type="time" pattern="hh:mm a" value="${slot.endDate}" /></td>
                                                </tr>
                                        </c:forEach>
                                </table>
                		</div>
                </td>
        </tr>
        <tr>
                <td><spring:message code="appointment.Appointment.create.label.reason"/></td>
                <spring:bind path="appointment.reason">
                        <td><textarea name="reason" rows="3" cols="50" style="resize:none" onkeypress="return forceMsaxLength(this, 1024);">${status.value}</textarea></td>
                </spring:bind>
        </tr>
        <tr><td><input type="submit"  class="appointmentButton" value="<spring:message code="appointment.Appointment.create.save"/>" name="save"></td>
        <td><input type="reset"  class="appointmentButton" value="<spring:message code="appointment.Appointment.create.cancel"/>" name="cancel"></td>
        </tr>
</table>
</fieldset>
</form:form>
<%@ include file="/WEB-INF/template/footer.jsp" %>