<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/moduleResources/appointment/Styles/createAppointmentStyle.css"/>
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />


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
				DWRAppointmentService.purgeAppointmentBlock(parseInt(appointmentBlockId), function(){
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
	           var locationId = null;
	           if(selectedLocation == null){
	           locationId = $j("[name='locationId']")[0].value;
				}
				else{
				    locationId = selectedLocation.options[selectedLocation.selectedIndex].value;
				}
				if(locationId =="")
     		    locationId =null;
                      var tableHeader= '';
                      document.getElementById('appointmentBlocksTable').innerHTML = tableHeader;
                      tableHeader = '<tr class="tableHeader" align="center">';
                      tableHeader +='<th align="center"><spring:message code="appointment.AppointmentBlock.column.select"/></th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.provider"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.date"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>';
                      tableHeader +='<th align="center"> <spring:message code="appointment.AppointmentBlock.slotLength"/> </th>';
                      tableHeader +="</tr>";
                      DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId,function(appointmentBlocks){
		      	var tableContent = '';
       			var count = 0;
                      for(var i=0;i<appointmentBlocks.length;i++)
                      {
					if(count == 0){
						document.getElementById('appointmentBlocksTable').innerHTML +=tableHeader;
					}
  			        var backgroundColor = 'background-color:#E6E6E6';
  				        if((count++)%2 ==0){
					backgroundColor = 'background-color:#ffffff';
  			   	        }
                          tableContent = '<tr style="'+backgroundColor +'">';
                          tableContent += '<td align="center">'+'<input type="radio" name="appointmentBlockRadios" value="'+appointmentBlocks[i].appointmentBlockId+'"/></td>';
                          var location = appointmentBlocks[i].location;
                          var locationString = "";
                          //levels for the location tree.			
					while(location != null){
					      if(location.id != locationId)
					      {
					  	    locationString = location.name + locationString;
					  	    locationString = "\\" + locationString;
					      }
					      else{
							if(locationString==""){
								locationString = location.name;
							}
							break;
                                }
              		     	  location = location.parentLocation;
							}
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
						  tableContent += '<td align="center">'+appointmentBlocks[i].timeSlotLength+'</td>';
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
                                                <td class="formLabel"><spring:message code="appointment.AppointmentBlock.pickDate"/>: </td>
                                                <td><input type="text" name="fromDate" id="fromDate" size="18" value="" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('fromDate').focus();"/></td>
                                                <td><input type="text" name="toDate" id="toDate" size="18" value="" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('toDate').focus();"/></td>
                                        </tr>
                                        <tr>
                                            <td class="formLabel"><spring:message code="appointment.AppointmentBlock.column.location"/>: </td>
											<td><openmrs_tag:locationField formFieldName="locationId" initialValue="${selectedLocation}" optionHeader="[blank]"/></td>
                                        </tr>
                                        <tr>
                                                <td><input type="button" class="appointmentButton" value=<spring:message code="appointment.AppointmentBlock.apply"/> onClick="updateAppointmentBlockTable()"></td>
                                        </tr>
                                </table>
        </div>
</fieldset>
 
<br/>
<b class="boxHeader"><spring:message code="appointment.AppointmentBlock.list.title"/></b>
<form method="post" class="box">
        <table id="appointmentBlocksTable" cellspacing="0"></table>
<table>
        <tr><td><a href="appointmentBlockForm.form"><spring:message code="appointment.AppointmentBlock.add"/></a></td>
        <td><a id="editLink" onClick="updateHrefForAppointmentBlockEdit()"  ><spring:message code="appointment.AppointmentBlock.edit"/></a></td>
        <td><a id="deleteLink" onClick="deleteSelectedAppointmentBlock()"><spring:message code="appointment.AppointmentBlock.delete"/></a></td>
        </tr>
</table>
 </form>
 
 
<%@ include file="/WEB-INF/template/footer.jsp" %>