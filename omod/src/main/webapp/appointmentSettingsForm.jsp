<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/Styles/appointmentSettingsFormStyle.css" />

<script type="text/javascript"
	src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript"
	src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript"
	src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
	//Used to disable/enable the refresh interval input
	function updateRefreshRadio() {
		var radioButtons = document.getElementsByName('refreshRadio');
		var selectedValue = null;

		for ( var i = 0; i < radioButtons.length; i++) {
			if (radioButtons[i].checked) {
				selectedValue = radioButtons[i].value;
				break;
			}
		}

		if (selectedValue < 0)
			document.getElementById('refreshDelayInput').disabled = true;
		else
			document.getElementById('refreshDelayInput').disabled = false;
	}

</script>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<openmrs:require privilege="Manage Appointments Settings" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentSettingsForm.form" />

<h2>
	<spring:message code="appointmentscheduling.Appointment.settings.title" />
</h2>

<form:form method="post" id="appointmentSettingsForm">

	<br />
	<br />
	<b class="boxHeader"><spring:message
			code="appointmentscheduling.Appointment.settings.fieldset.gp" /></b>
	<fieldset>

		<table>
			<!-- Default Visit Type GP -->
			<tr>
				<td>
					<div
						onmouseover="document.getElementById('tt1').style.display='block'"
						onmouseout="document.getElementById('tt1').style.display='none'"
						class="questionMark">
						<img
							src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
							onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
							onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
							alt='' />
						<div id="tt1" class="toolTip">
							<spring:message
								code="appointmentscheduling.Appointment.gp.desc.visitType" />
						</div>
				</td>

				<td><spring:message
						code="appointmentscheduling.Appointment.settings.label.visitType" />


					</div></td>
				<td><select name="visitTypeSelect" id="visitTypeSelect">

						<c:forEach var="visitType" items="${visitTypeList}">
							<option value="${visitType.visitTypeId}"
								${visitType.visitTypeId==selectedVisitType.visitTypeId ? 'selected' : ''}><c:out value="${visitType.name}"/></option>
						</c:forEach>
				</select></td>
			</tr>

			<!-- Phone Number Attribute GP -->
			<tr>
				<td>
					<div
						onmouseover="document.getElementById('tt2').style.display='block'"
						onmouseout="document.getElementById('tt2').style.display='none'"
						class="questionMark">
						<img
							src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
							onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
							onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
							alt='' />
						<div id="tt2" class="toolTip">
							<spring:message
								code="appointmentscheduling.Appointment.gp.desc.phoneAttribute" />
						</div>
					</div>
				</td>

				<td><spring:message
						code="appointmentscheduling.Appointment.settings.label.phoneNumber" />

				</td>
				<td><select name="personAttributeTypeSelect"
					id="personAttributeTypeSelect">
						<c:forEach var="personAttributeType"
							items="${personAttributeList}">
							<option value="${personAttributeType.personAttributeTypeId}"
								${personAttributeType.personAttributeTypeId==selectedPersonAttributeType.personAttributeTypeId ? 'selected' : ''}><c:out value="${personAttributeType.name}"/></option>
						</c:forEach>
				</select></td>
			</tr>

			<!-- Manage Appointments Form Refresh Interval GP -->
			<tr>
				<td>
					<div
						onmouseover="document.getElementById('tt3').style.display='block'"
						onmouseout="document.getElementById('tt3').style.display='none'"
						class="questionMark">
						<img
							src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
							onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
							onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
							alt='' />
						<div id="tt3" class="toolTip">
							<spring:message
								code="appointmentscheduling.Appointment.gp.desc.manageAppointmentsRefresh" />
						</div>
					</div>
				</td>
				<td><spring:message
						code="appointmentscheduling.Appointment.settings.label.refreshAppointmentsList" />


				</td>
				<td><input type="radio" name="refreshRadio" value="-1"
					${param.refreshRadio<0 || (param.refreshRadio==null && refreshInterval<0) ? 'checked' : ''}
					onchange="updateRefreshRadio();"> <spring:message
						code="appointmentscheduling.Appointment.settings.label.disable" />
					</input> <br /> <input type="radio" name="refreshRadio" value="1"
					onchange="updateRefreshRadio();"
					${param.refreshRadio>0 || (param.refreshRadio==null && refreshInterval>0) ? 'checked' : ''}>
					<spring:message
						code="appointmentscheduling.Appointment.settings.label.every" />

					<input type='text' id='refreshDelayInput' name="refreshDelayInput"
					value='${ refreshInterval>0 ? refreshInterval : '
					'}' ${param.refreshRadio>0 || (param.refreshRadio==null && refreshInterval>0) ? '' : 'disabled'} />

					<spring:message code="appointmentscheduling.Appointment.seconds" />
					</input></td>
			</tr>
			<tr>
				<td colspan="2"></td>
			</tr>

			<!-- Default Time Slot Duration GP -->
			<tr>
				<td>
					<div
						onmouseover="document.getElementById('tt4').style.display='block'"
						onmouseout="document.getElementById('tt4').style.display='none'"
						class="questionMark">
						<img
							src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
							onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
							onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
							alt='' />
						<div id="tt4" class="toolTip">
							<spring:message
								code="appointmentscheduling.Appointment.gp.desc.defaultTimeSlotDuration" />
						</div>
					</div>
				</td>

				<td><spring:message
						code="appointmentscheduling.Appointment.settings.label.slotDuration" />


				</td>
				<td><input type='text' id='slotDurationInput'
					name="slotDurationInput" value='${slotDuration}' /> <spring:message
						code="appointmentscheduling.Appointment.minutes" /></td>
			</tr>

			<!--Hide End Visit Buttons GP -->
			<tr>
				<td>
					<div
						onmouseover="document.getElementById('tt5').style.display='block'"
						onmouseout="document.getElementById('tt5').style.display='none'"
						class="questionMark">
						<img
							src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
							onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
							onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
							alt='' />
						<div id="tt5" class="toolTip">
							<spring:message
								code="appointmentscheduling.Appointment.gp.desc.hideEndVisit" />
						</div>
					</div>
				</td>
				
				<td>
					<spring:message code="appointmentscheduling.Appointment.settings.label.hideEndVisit" />
				</td>
				
				<td>
					<input type="radio" name="hideEndVisit" value="false" ${(param.hideEndVisit==null && hideEndVisit=='false') || param.hideEndVisit=='false' ? 'checked' : ''}>
						<spring:message code="appointmentscheduling.Appointment.settings.label.disable" />
					</input>
					<br/>
					<input type="radio" name="hideEndVisit" value="true" ${(param.hideEndVisit==null && hideEndVisit=='true') || param.hideEndVisit=='true' ? 'checked' : ''}>
						<spring:message code="appointmentscheduling.Appointment.settings.label.enable" />
					</input>
				</td>
			</tr>

			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td><input type='submit' name='save'
					value='<spring:message code="general.save"/>' /></td>
			</tr>
		</table>
	</fieldset>

	<br />
	<br />
	<b class="boxHeader"><spring:message
			code="appointmentscheduling.Appointment.settings.fieldset.additionalSettings" /></b>
	<fieldset>
		<table>

            <!--Clean Open Appointments Buttons GP -->
            <tr>
                <td>
                    <div
                        onmouseover="document.getElementById('tt6').style.display='block'"
                        onmouseout="document.getElementById('tt6').style.display='none'"
                        class="questionMark">
                        <img
                            src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'
                            onmouseover="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_hover.png'"
                            onmouseout="this.src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/qMark_normal.png'"
                            alt='' />
                        <div id="tt6" class="toolTip">
                            <spring:message
                                code="appointmentscheduling.Appointment.gp.desc.cleanOpenAppointmentScheduler" />
                        </div>
                    </div>
                </td>

                 <td>
                    <spring:message code="appointmentscheduling.Appointment.settings.label.cleanOpenAppointmentScheduler" />
                </td>

                 <td>
                    <input type="radio" name="cleanOpenAppointmentScheduler" value="false" ${(param.cleanOpenAppointmentScheduler==null && cleanOpenAppointmentScheduler=='false') || param.cleanOpenAppointmentScheduler=='false' ? 'checked' : ''}>
                        <spring:message code="appointmentscheduling.Appointment.settings.label.disable" />
                    </input>
                    <br/>
                    <input type="radio" name="cleanOpenAppointmentScheduler" value="true" ${(param.cleanOpenAppointmentScheduler==null && cleanOpenAppointmentScheduler=='true') || param.cleanOpenAppointmentScheduler=='true' ? 'checked' : ''}>
                        <spring:message code="appointmentscheduling.Appointment.settings.label.enable" />
                    </input>
                </td>
			</tr>
			
			<tr>
				<td><br /></td>
			</tr>
			<tr>
				<td></td>
				<td></td>
				<td><input type='submit' name='save'
					value='<spring:message code="general.save"/>' /></td>
			</tr>
		</table>
	</fieldset>
</form:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>