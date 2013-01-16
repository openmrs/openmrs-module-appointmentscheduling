<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />
<openmrs:htmlInclude file="/moduleResources/appointment/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude file="/moduleResources/appointment/Styles/AppointmentBlockStyle.css" />
<openmrs:htmlInclude file="/moduleResources/appointment/Styles/appointmentBlock_jQueryDatatable.css"/>
<openmrs:htmlInclude file="/moduleResources/appointment/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>

 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
		//updates the table if changes occurred
       function updateAppointmentBlockTable()
       {
               var fromDate = document.getElementById('fromDate').value;
               var toDate = document.getElementById('toDate').value;
               //date valdition
               DWRAppointmentService.validateDates(fromDate,toDate,function(error){
            	   if(error == true){
            		   //need to post/refresh the page in order to show the message.
            		   document.forms['appointmentBlockListForm'].submit();
            	   }
               });
	           var selectedLocation = document.getElementById("locationId");
	           var modelAttributeSelectedLocation = "${chosenLocation}";
	           var locationId = null;
	           if(selectedLocation == null){
	           		locationId = $j("[name='locationId']")[0].value;
				}
				else{
				    locationId = selectedLocation.options[selectedLocation.selectedIndex].value;
				}
	            //location not selected
				if(locationId ==""){
					//If the location widget is not loaded yet
					if(modelAttributeSelectedLocation != null){
						locationId = "${chosenLocation.id}";
					}
					else locationId = null;				
				}

                DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId,function(appointmentBlocks){
                  var tableContent = '';
                  var count = 0;
                  for(var i=0;i<appointmentBlocks.length;i++)
                  {
                      //clean the table content
                      if(count ==0){
                        document.getElementById('appointmentBlocksTable').innerHTML='';
                      }
                      count++;
                      tableContent += '<tr class="notSelectedRow">';
                      tableContent += '<td align="center" style="display:none;">'+'<input type="radio" name="appointmentBlockRadios" value="'+appointmentBlocks[i].appointmentBlockId+'"/></td>';
                      tableContent += '<td align="center">'+appointmentBlocks[i].location.name+"</td>";
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
                      //If we got to the end
                      if(count == appointmentBlocks.length){
                        var oTable = $j('#appointmentBlocksTable').dataTable();
                        oTable.fnClearTable();
                        oTable.fnDestroy();
                        document.getElementById('appointmentBlocksTable').innerHTML += tableContent;
                      }


                    }
                     if(count == appointmentBlocks.length){
                            //if there was no appointment blocks, clear table and destroy
                            if(appointmentBlocks.length == 0){
                                var oTable = $j('#appointmentBlocksTable').dataTable();
                                oTable.fnClearTable();
                                oTable.fnDestroy();
                             }
                            $j('#appointmentBlocksTable').dataTable({
                                "aLengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                                "iDisplayLength": 25,
                                "bLengthChange": true,
                                "bFilter": false,
                                "bInfo": true,
                                "bPaginate": true,
                                "bJQueryUI": true
                            });
                            //Toggle Checked Row
                            $j('.dataTables_wrapper tbody tr').live('click',
                              function(){
		                            var table = $j('#appointmentBlocksTable').dataTable();
		                            var nNodes = table.fnGetNodes();
		                            $j('input:radio', this).attr('checked',true);
		                            //update the hidden input in order to update the appointmentBlockId model attribute
		                            document.getElementById('appointmentBlockId').value	= $j('input:radio', this).attr('value');
		                            for(var i=0; i<nNodes.length; i++){
		                                $j(nNodes[i]).removeClass('selectedRow');
		                             	$j(nNodes[i]).addClass('notSelectedRow');
                           			 }
		                            $j(this).addClass('selectedRow');
		                            $j(this).removeClass('notSelectedRow');
                              }
                            );
                            var theTable = $j('#appointmentBlocksTable').dataTable();
                            theTable.fnSort([[4,'asc']]);
                            theTable.fnAdjustColumnSizing();
                  }

                });
        }
		
	   	function deleteFuncionality(e,event) {
	   			//Initialize the action to do nothing.
				document.getElementById('action').value = "";
	   			var selectedAppointmentBlockId = document.getElementById("appointmentBlockId").value;
	   			if(selectedAppointmentBlockId != null){
		   			//calling the DWR method in order to check how many appointments are exits in the selected appointment block.
		   			DWRAppointmentService.getNumberOfAppointmentsInAppointmentBlock(selectedAppointmentBlockId,function(appointmentsCount){
		   				if(appointmentsCount != null && appointmentsCount != 0){
		   					//TODO - need to this using spring.message
					   		document.getElementById("dialogText").innerHTML = "Deleting this appointment block will cancel "+appointmentsCount+" appointments, are you sure you want to proceed?";
					   		$j('#deleteDialog').dialog('open');
					   		event.stopPropagation();
		   				}
		   				else{ 
		   					//update the aciton to "purge" because there are no appointments assiciated with the selected appointment block.
		   					document.getElementById('action').value = "purge";
							//POST back to the controller in order to purge the selected appointment block.
		   					document.forms['appointmentBlockListForm'].submit();
		   				}
		   			});
	   			}
	   			else{
	   				//update the action to notify the user to select an appointment block
	   				document.getElementById('action').value = "notifyToSelectAppointmentBlock";
	   				//POST in order to notify the user to select an appointmnet block 
	   				document.forms['appointmentBlockListForm'].submit();
	   			}
	   	}
		
		//On the page load updates necessary stuff
         $j(document).ready(function() {      	 
	   			//Initialize the action to do nothing (for page refresh bugs)
				document.getElementById('action').value = "";
				//Delete dialog configeration 
				$j('#deleteDialog').dialog({
					autoOpen: false,
					height: 250,
					width: 300,
					modal: true,
					resizable: false,
					buttons: {
						"<spring:message code='general.cancel' />": function() {
							//update the aciton to do nothing.
							document.getElementById('action').value = "";
							//close the dialog.
							$j(this).dialog("close");
						},
						"<spring:message code='general.submit' />": function() {
								//update the aciton to "void" because there are appointments assiciated with the selected appointment block and the user clicked "Submit".
			   					document.getElementById('action').value = "void";
								//POST back to the controller in order to void the selected appointment block.
								document.forms['appointmentBlockListForm'].submit();
						}
					}
				});
                //data table initialization
                $j('#appointmentBlocksTable').dataTable({
                 "aLengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                 "aoColumnDefs": [{ "bSearchable": false, "bVisible": false, "aTargets": [ 0 ] }],
                 "iDisplayLength": -1,
                 "bLengthChange": true,
                 "bFilter": false,
                 "bInfo": true,
                 "bPaginate": true,
                 "bJQueryUI": true
                });
                //Some more visual settings
                var theTable = $j('#appointmentBlocksTable').dataTable();
                theTable.fnAdjustColumnSizing();
                //Fill the content of the appointmnet blocks table
 	        	updateAppointmentBlockTable();
        });
 
</script>
<h2><spring:message code="appointment.AppointmentBlock.manage.title"/></h2>
<br/><br/>
 <form method="post" name="appointmentBlockListForm">
<fieldset style="clear: both">
        <legend><spring:message code="appointment.AppointmentBlock.legend.properties"/></legend>
        <div style="margin: 0.5em 0;">
                <table>
                        <tr>
                                <td class="formLabel"><spring:message code="appointment.AppointmentBlock.pickDate"/>: </td>
                                <td><input type="text" name="fromDate" id="fromDate" size="18" value="${fromDate}" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('fromDate').focus();"/></td>
                                <td><input type="text" name="toDate" id="toDate" size="18" value="${toDate}" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointment/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('toDate').focus();"/></td>
                        </tr>
                        <tr>
                            <td class="formLabel"><spring:message code="appointment.AppointmentBlock.column.location"/>: </td>
                            <td><openmrs_tag:locationField formFieldName="locationId" initialValue="${chosenLocation}" optionHeader="[blank]"/></td>
                        </tr>
                        <tr>
                                <td><input type="button" class="appointmentBlockButton" value=<spring:message code="appointment.AppointmentBlock.apply"/> onClick="updateAppointmentBlockTable()"></td>
                        </tr>
                </table>
        </div>
</fieldset>
 
<br/>
<b class="boxHeader"><spring:message code="appointment.AppointmentBlock.list.title"/></b>
        <table id="appointmentBlocksTable" cellspacing="0">
                <thead>
                    <tr>
                        <th align="center" style="display:none;"><spring:message code="appointment.AppointmentBlock.column.select"/></th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.location"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.provider"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.appointmentTypes"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.date"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.startTime"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.column.endTime"/> </th>
                        <th align="center"> <spring:message code="appointment.AppointmentBlock.slotLength"/> </th>
                    </tr>
                </thead>

        </table>
		<input type="hidden" name="appointmentBlockId" id="appointmentBlockId" value="${appointmentBlockId}" />
		<input type="hidden" name="action" id="action" value="${action}" />
		<table align="center">
		        <tr>
		        <td><input type="submit" class="appointmentBlockButton" value="<spring:message code="appointment.AppointmentBlock.add"/>" name="add"> </td>
		        <td><input type="submit" class="appointmentBlockButton" value="<spring:message code="appointment.AppointmentBlock.edit"/>" name="edit"> </td>
		         <td><input type="button" id="deleteBtn" class="appointmentBlockButton" value="<spring:message code="appointment.AppointmentBlock.delete"/>" onclick="deleteFuncionality(this, event)"> </td>
		        </tr>
		</table>
 </form>
 
 <div id="deleteDialog" >
	<table id='deleteDialogOptions' class="dialogTable">
		<tr><td><h2><spring:message code="appointment.AppointmentBlock.deleteDialog.title"/></h2></td></tr>
		<tr><td><div id="dialogText"></div></td></tr>
	</table>
</div>
 
<%@ include file="/WEB-INF/template/footer.jsp" %>