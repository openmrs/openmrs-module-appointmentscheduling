<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/calendar/calendar.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/css/dataTables.css" />
<openmrs:htmlInclude file="/scripts/jquery/dataTables/js/jquery.dataTables.min.js" />
 
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>
<script type="text/javascript">
 
        function updateAppointmentBlockTable()
        {
                        var selectedDate = document.getElementById('dateFilter').value;
                        var location = document.getElementById("locationId");
                        var fromDate = null;
                        var toDate = null;
                        var locationId =2;
                        alert(location);
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
                        DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId,function(appointmentBlocks){
                                for(var i=0;i<appointmentBlocks.length;i++)
                                {
                                    tableContent += "<tr>";
                                    tableContent  += '<td align="center">'+'<input type="radio" name="appointmentBlockCheckBox" value=${appointmentBlock.appointmentBlockId }>'+"</td>";
                                    tableContent += '<td align="center">'+appointmentBlocks[i].location+"</td>";      
                                    tableContent += '<td align="center">'+appointmentBlocks[i].providor+"</td>";  
                                    tableContent += '<td align="center">'+appointmentBlocks[i].types+"</td>";      
                                    tableContent += '<td align="center">'+appointmentBlocks[i].startDate+"</td>";
                                    tableContent += '<td align="center">'+appointmentBlocks[i].endDate+"</td>";
                                    tableContent += "</tr>";      
                                }
                               
                        });
                        document.getElementById('appointmentBlocksTable').innerHTML +=tableContent;
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
                                                <td><input type="text" name="appointmentBlockDateFilter" id="dateFilter" size="11" onfocus="showCalendar(this,60)"></td>
                                        </tr>
                                        <tr>
                                            <td><spring:message code="appointment.AppointmentBlock.column.location"/>: </td>
                                                <td>
                                                <openmrs_tag:locationTree formFieldName="${formFieldName}" initialValue="${initialValue}" selectableTags="${selectableTags}"/>
                                                </td>
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