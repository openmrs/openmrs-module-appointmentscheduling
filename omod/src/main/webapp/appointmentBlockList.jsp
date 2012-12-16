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
		function updateHrefForAppointmentBlockEdit()
		{
			var appointmentBlockId = getSelectedAppointmentBlockId();
			if(appointmentBlockId!=null){
				document.getElementById("editLink").href="appointmentBlockForm.form?appointmentBlockId="+appointmentBlockId;			
			}
			else
			{
				window.alert('<openmrs:message code="appointment.AppointmentBlock.error.selectAppointmentBlock" javaScriptEscape="true"/>');	
			}
		}
		function getSelectedAppointmentBlockId()
		{
			var radios = document.getElementsByName('appointmentBlockRadios');
			var appointmentBlockId;
			for (var i = 0; i < radios.length; i++) {
			    if (radios[i].type === 'radio' && radios[i].checked) {
			        // get value, set checked flag or do whatever you need to
			        appointmentBlockId = radios[i].value;     
			        break;
			    }
			}
			return appointmentBlockId;
		}
		function deleteSelectedAppointmentBlock()
		{
			var appointmentBlockId = getSelectedAppointmentBlockId();
			if(appointmentBlockId != null)
			{
				DWRAppointmentService.purgeAppointmentBlock(appointmentBlockId, function(){
					updateAppointmentBlockTable();
				});
			}
			else{
				window.alert('<openmrs:message code="appointment.AppointmentBlock.error.selectAppointmentBlock" javaScriptEscape="true"/>');
			}
		}
        function updateAppointmentBlockTable()
        {
                        var fromDate = document.getElementById('fromDate').value;
                        var toDate = document.getElementById('toDate').value;
	                    var selectedLocation = document.getElementById("locationId");
		                var locationId = selectedLocation.options[selectedLocation.selectedIndex].value;	           
                        var tableContent = '';
                        document.getElementById('appointmentBlocksTable').innerHTML = tableContent;
                        tableContent="<tr>";
                        tableContent+='<th align="center"><spring:message code="appointment.AppointmentBlock.column.select"/></th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.provider"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.date"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>';
                        tableContent+='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>';
                        tableContent+="</tr>";
	           			document.getElementById('appointmentBlocksTable').innerHTML +=tableContent;
                        DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId,function(appointmentBlocks){
		    				    tableContent = '';
                                for(var i=0;i<appointmentBlocks.length;i++)
                                {
                                    tableContent = "<tr>";
                                    tableContent += '<td align="center">'+'<input type="radio" name="appointmentBlockRadios" value="'+appointmentBlocks[i].appointmentBlockId+'"/></td>';
                                    var location = appointmentBlocks[i].location;
                                    var locationString = "";
                                    //assumption there are only two levels for the location tree.
                                    if(location.parentLocation!=null){	    
                                	    locationString += location.parentLocation.name+"\\";	
                                    }
                                    locationString += location.name;
                                    tableContent += '<td align="center">'+locationString+"</td>";      
                                    tableContent += '<td align="center">'+appointmentBlocks[i].provider.name+"</td>";
                                    //Linking the appointment types in a string.
                                    var appointmentTypes = "";
                                    var appointmentTypesArray = appointmentBlocks[i].types;
                                    for(var j=0;j<appointmentTypesArray.length;j++)
                                    {
                                    	    appointmentTypes += appointmentTypesArray[j].name;
                                    		if(j<(appointmentTypesArray.length - 1)){
                                    			appointmentTypes += ", ";
                                    		}
                                    }

                                    tableContent += '<td align="center">'+appointmentTypes+"</td>";    
							        var startDate = appointmentBlocks[i].startDate;
							        var endDate = appointmentBlocks[i].endDate;
							        tableContent += '<td align="center">'+startDate.getDate()+"/"+(startDate.getMonth()+1)+"/"+startDate.getFullYear()+'</td>';
							        tableContent += '<td align="center">'+startDate.toLocaleTimeString()+'</td>';
							        tableContent += '<td align="center">'+endDate.toLocaleTimeString()+'</td>';
                                    tableContent += "</tr>";
                                    document.getElementById('appointmentBlocksTable').innerHTML += tableContent;
		      				   }                   
							   
                       });
                        
        }
        function getDateTimeFormat(date){
		   	var newFormat = "";
			if((date.getDate()+"").length == 1){
				newFormat += "0";
			}
			newFormat += date.getDate()+"/";
			if(((date.getMonth()+1)+"").length == 1){
				newFormat += "0";
			}
			newFormat += (date.getMonth()+1)+"/"+date.getFullYear();
			newFormat += " "+date.toLocaleTimeString();
			return newFormat;	
         }
        //Showing the jQuery data table when the page loaded.
         $j(document).ready(function() {
			   var currentDate = new Date();
			   currentDate.setHours(0,0,0,0);	 
			   var currentTime = new Date();
			   var days = 6;
			   currentTime.setTime(currentTime.getTime() + (days * 24 * 60 * 60 * 1000));
			   var nextWeekDate = new Date(currentTime);
			   nextWeekDate.setHours(23,59,59,999);
			   document.getElementById('fromDate').value = getDateTimeFormat(currentDate);
			   document.getElementById('toDate').value = getDateTimeFormat(nextWeekDate);
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
                                                <td><input type="text" name="fromDate" id="fromDate" size="16" value="" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('dateFilter').focus();"/></td>
                                                <td><input type="text" name="toDate" id="toDate" size="16" value="" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('dateFilter').focus();"/></td>
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
        <td><a id="editLink" onClick="updateHrefForAppointmentBlockEdit()"  ><spring:message code="appointment.AppointmentBlock.edit"/></a></td>
        <td><a onClick="deleteSelectedAppointmentBlock()"><spring:message code="appointment.AppointmentBlock.delete"/></a></td>
        </tr>
</table>
 
 
 
<%@ include file="/WEB-INF/template/footer.jsp" %>