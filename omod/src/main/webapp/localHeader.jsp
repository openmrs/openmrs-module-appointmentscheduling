<ul id="menu">
	<openmrs:hasPrivilege privilege='View Appointment Types'>
	<li <c:if test='<%= request.getRequestURI().contains("appointmentscheduling/appointmentTypeList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentTypeList.list">
			<spring:message code="appointmentscheduling.AppointmentType.manage"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege='View Provider Schedules'>
	<li <c:if test='<%= request.getRequestURI().contains("appointmentscheduling/appointmentBlockCalendar.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentBlockCalendar.list">
			<spring:message code="appointmentscheduling.AppointmentBlock.manage.title"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege='View Appointments'>
	<li <c:if test='<%= request.getRequestURI().contains("appointmentscheduling/appointmentList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentList.list">
			<spring:message code="appointmentscheduling.Appointment.list.manage.title"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege='View Appointments Statistics'>
	<li <c:if test='<%= request.getRequestURI().contains("appointmentscheduling/appointmentStatisticsForm.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointmentscheduling/appointmentStatisticsForm.form">
			<spring:message code="appointmentscheduling.Appointment.statistics.title"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
</ul>