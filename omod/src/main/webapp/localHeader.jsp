<ul id="menu">
	<openmrs:hasPrivilege privilege='View Appointment Types'>
	<li <c:if test='<%= request.getRequestURI().contains("appointment/appointmentTypeList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointment/appointmentTypeList.list">
			<spring:message code="appointment.AppointmentType.manage"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege='View Appointment Blocks'>
	<li <c:if test='<%= request.getRequestURI().contains("appointment/appointmentBlockList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointment/appointmentBlockList.list">
			<spring:message code="appointment.AppointmentBlock.manage.title"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
	<openmrs:hasPrivilege privilege='View Appointments'>
	<li <c:if test='<%= request.getRequestURI().contains("appointment/appointmentList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointment/appointmentList.list">
			<spring:message code="appointment.Appointment.list.manage.title"/>
		</a>
	</li>
	</openmrs:hasPrivilege>
</ul>