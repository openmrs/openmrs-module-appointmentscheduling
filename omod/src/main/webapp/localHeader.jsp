<ul id="menu">
	<li class="first">
		<a href="${pageContext.request.contextPath}/admin"><spring:message code="admin.title.short"/></a>
	</li>
	<li <c:if test='<%= request.getRequestURI().contains("appointment/appointmentTypeList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointment/appointmentTypeList.list">
			<spring:message code="appointment.AppointmentType.manage"/>
		</a>
	</li>
	<li <c:if test='<%= request.getRequestURI().contains("appointment/appointmentBlockList.jsp") %>'>class="active"</c:if>>
		<a href="${pageContext.request.contextPath}/module/appointment/appointmentBlockList.list">
			<spring:message code="appointment.AppointmentBlock.manage.title"/>
		</a>
	</li>
</ul>