<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/moduleResources/appointment/createAppointmentStyle.css"/>
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>
<script type="text/javascript">
        function updateAppointmentBlockTable()
        {
                        var selectedDate = document.getElementById('dateFilter').value;
	                    var selectedLocation = document.getElementById("locationId");
		                var locationId = selectedLocation.options[selectedLocation.selectedIndex].value;
                        var fromDate = null;
                        var toDate = null;
	           
                        var tableContent = '';
                        if(selectedDate != "")
                        {
                        fromDate = new Date(selectedDate);            
                        toDate = new Date(fromDate.getFullYear(), fromDate.getMonth(), fromDate.getDate()+1);          
                        }
                        document.getElementById('appointmentBlocksTable').innerHTML = tableContent;
                        tableContent="<tr>";
                        tableContent+='<th align="center"><spring:message code="appointment.AppointmentBlock.column.select"/></th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.user"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>';
                        tableContent+="</tr>";
	           document.getElementById('appointmentBlocksTable').innerHTML +=tableContent;
                        DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId,function(appointmentBlocks){
		       tableContent = '';
                                for(var i=0;i<appointmentBlocks.length;i++)
                                {
                                    tableContent += "<tr>";
                                    tableContent += '<td align="center">'+'<input type="radio" name="appointmentBlockCheckBox" value="'+appointmentBlocks[i].id+'"/></td>';
                                    tableContent += '<td align="center">'+appointmentBlocks[i].location+"</td>";      
                                    tableContent += '<td align="center">'+appointmentBlocks[i].providor+"</td>";  
                                    tableContent += '<td align="center">'+appointmentBlocks[i].types+"</td>";    
		       					    tableContent += '<td align="center">'+appointmentBlocks[i].startDate+'</td>';
    		     			        tableContent += '<td align="center">'+appointmentBlocks[i].endDate+'</td>';
                                    tableContent += "</tr>";  
		         }                   
			document.getElementById('appointmentBlocksTable').innerHTML +=tableContent;
                        });
                        
        }
       
        //Showing the jQuery data table when the page loaded.
         $j(document).ready(function() {
                updateAppointmentBlockTable();
        });
 
</script>
<h2><spring:message code="appointment.AppointmentBlock.manage.title"/></h2>
<br/><br/>
 
<fieldset style="clear: both">
        <legend><spring:message code="appointment.AppointmentBlock.legend.properties"/></legend>
        <div style="margin: 0.5em 0;">
                        <table>
                                        <tr>
                                                <td><spring:message code="appointment.AppointmentBlock.pickDate"/>: </td>
                                                <td><input type="text" name="Date" id="dateFilter" size="16" value="" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('dateFilter').focus();"/></td>
                                        </tr>
                                        <tr>
                                            <td><spring:message code="appointment.AppointmentBlock.column.location"/>: </td>
				<td><openmrs:fieldGen type="org.openmrs.Location" formFieldName="locationId" val="${selectedLocation}" /></td>
                                        </tr>
                                        <tr>
                                                <td><input type="button" value="Apply" onClick="updateAppointmentBlockTable()"></td>
                                        </tr>
                                </table>
        </div>
</fieldset>
 
<br/>
<b class="boxHeader"><spring:message code="appointment.AppointmentBlock.list.title"/></b>
<form method="post" class="box">
        <table id="appointmentBlocksTable">
 
 
        </table>
</form>
<table align="center">
        <tr><td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.add"/></a></td>
        <td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.edit"/></a></td>
        <td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.delete"/></a></td>
        </tr>
</table>
 
 
 
<%@ include file="/WEB-INF/template/footer.jsp" %>