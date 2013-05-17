<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/jquery.tree.min.js" />
<openmrs:htmlInclude file="/scripts/jquery/jsTree/themes/classic/style.css" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/AppointmentBlockStyle.css" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/appointmentBlock_jQueryDatatable.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude 
	file="/moduleResources/appointmentscheduling/TableTools/media/ZeroClipboard/ZeroClipboard.js" /> 
<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/TableTools/media/js/TableTools.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/TableTools/media/css/TableTools.css" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/json2.js" />

<openmrs:require privilege="View Provider Schedules" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentBlockList.list" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
		//updates the table if changes occurred
       function updateAppointmentBlockTable(firstTime)
       {
   			   var blocksAsJson;
               var fromDate = document.getElementById('fromDate').value;
               var toDate = document.getElementById('toDate').value;
			   //date valdition
			   DWRAppointmentService.validateDates(fromDate,toDate,function(error){
				   if(error == true){
					   if(!firstTime){
					   //need to post/refresh the page in order to show the message.
					   document.forms['appointmentBlockListForm'].submit();
					   }
					   else return;
				   }
				   else{
						//remove error if it displays in the page. otherwise continue.
						var errorDiv = document.getElementById("openmrs_error");
						if(errorDiv!=null){
							//If an error desplays in the page but the error is checked and it's false(no error) - remove it.
							errorDiv.parentNode.removeChild(errorDiv);
						}
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
                  blocksAsJSON = [];
                  for(var i=0;i<appointmentBlocks.length;i++)
                  {
                      //clean the table content
                      if(count ==0){
                        document.getElementById('appointmentBlocksTable').innerHTML='';
                      }
                      count++;
                      tableContent += '<tr class="notSelectedRow">';
                      tableContent += '<td align="center" style="display:none;">'+'<input type="radio" name="appointmentBlockRadios" value="'+appointmentBlocks[i].appointmentBlockId+'"/></td>';
                      tableContent += '<td align="center">'+appointmentBlocks[i].location+"</td>";
                      tableContent += '<td align="center">'+appointmentBlocks[i].provider+"</td>";
                      //Linking the appointment types in a string.
                      var appointmentTypes = "";
                      var appointmentTypesArray = appointmentBlocks[i].types;
                      for(var j=0;j<appointmentTypesArray.length;j++)
                      {
                            appointmentTypes += appointmentTypesArray[j];
                            if(j<(appointmentTypesArray.length - 1)){
                                appointmentTypes += ", ";
                            }
                      }
                      tableContent += '<td align="center">'+appointmentTypes+"</td>";
                      var date = appointmentBlocks[i].date;
                      var startTime = appointmentBlocks[i].startTime;
                      var endTime = appointmentBlocks[i].endTime;
                      var startDate = appointmentBlocks[i].startDate;
                      tableContent += '<td align="center">'+date+'</td>';
                      tableContent += '<td align="center">'+startTime+'</td>';
                      tableContent += '<td align="center">'+endTime+'</td>';
                      tableContent += '<td align="center">'+appointmentBlocks[i].timeSlotLength+'</td>';
					  tableContent += '<td align="center">'+startDate+'</td>';
                      tableContent += "</tr>";
                      //If we got to the end
                      if(count == appointmentBlocks.length){
                        var oTable = $j('#appointmentBlocksTable').dataTable();
                        oTable.fnClearTable();
                        oTable.fnDestroy();
                        document.getElementById('appointmentBlocksTable').innerHTML += tableContent;
                      }
					  //Json object update
                	  var block = {};
					  block.id = appointmentBlocks[i].appointmentBlockId;
                	  block.title = "Provider: "+appointmentBlocks[i].provider+" Location: "+appointmentBlocks[i].location; //for test only
                	  block.start = appointmentBlocks[i].startDate.toString();
                	  block.end = appointmentBlocks[i].endDate.toString();
					  block.allDay = false;
					  block.provider = appointmentBlocks[i].provider;
					  block.location = appointmentBlocks[i].location;
					  block.appointmentTypes = appointmentTypes;
					  block.startTime=startTime;
					  block.endTime=endTime;
					  block.timeSlotLength=appointmentBlocks[i].timeSlotLength;
                	  blocksAsJSON.push(block);

                    }
                     if(count == appointmentBlocks.length){
                            //if there was no appointment blocks, clear table and destroy
                            if(appointmentBlocks.length == 0){
                                var oTable = $j('#appointmentBlocksTable').dataTable();
                                oTable.fnClearTable();
                                oTable.fnDestroy();
                             }
							TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointmentscheduling/TableTools/media/swf/ZeroClipboard.swf";
                            $j('#appointmentBlocksTable').dataTable({
								"aoColumns" : [ {
									"bVisible"  : true												
								}, { 
									"bSortable" : true
								}, {
									"bSortable" : true
								}, {
									"bSortable" : true
								} ,{
									"iDataSort" : 8
								}, {
									"bSortable" : true
								}, {
									"bSortable" : true
								} ,{
									"bSortable" : true
								} ,{
									"bVisible" : false
								}],
                                "aLengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
                                "iDisplayLength": 25,
								"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix' fl>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'ip<'toolbar' T><'calendarDiv'>>",
                                "bLengthChange": true,
                                "bFilter": false,
                                "bInfo": true,
                                "bPaginate": true,
                                "bJQueryUI": true,
								"fnDrawCallback" : function() {
									//Clear and prepend the status buttons
									$j(".calendarDiv").html("");
									$j('.calendarDiv')
											.prepend(
													"<img id='calendarViewButton' src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarViewIcon.png' Title='Calendar view' class='appointmentBlockCalendarIcon' onclick='changeToCalendarView(this, event)'>"+
													"<br/>"
													);
								},
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
							
                            //update blocks as json content
                            document.getElementById('appointmentBlocksJSON').value = JSON.stringify(blocksAsJSON);
                  }	
				  

                });
        }
		
		function changeToCalendarView(e, event){
			//change action to calendar view 
			document.getElementById('action').value = "changeToCalendarView";
			//POST back in order to redirect to the calendar view via the controller.
			document.forms['appointmentBlockListForm'].submit();
		}
		
	   	function deleteFuncionality(e,event) {
	   			//Initialize the action to do nothing.
				document.getElementById('action').value = "";
	   			var selectedAppointmentBlockId = document.getElementById("appointmentBlockId").value;
	   			if(selectedAppointmentBlockId != null){
		   			//calling the DWR method in order to get the patients that exits in the selected appointment block.
		   			DWRAppointmentService.getPatientsInAppointmentBlock(selectedAppointmentBlockId,function(patients){
		   				if(patients != null){
							var totalAppointments = patients[0].length+patients[1].length+patients[2].length;
							if(totalAppointments != 0){
								if(patients[0].length>0){	
									//Notify the user that the block cannot be deleted because it has active appointments
									document.getElementById("notifyDialogText").innerHTML = '<spring:message code="appointmentscheduling.AppointmentBlock.cannotBeDeleted.part1"/> '+patients[0].length+' <spring:message code="appointmentscheduling.AppointmentBlock.cannotBeDeleted.part2"/>';
									$j('#notifyDialog').dialog('open');
									event.stopPropagation();
								}
								else{
									if(patients[1].length>0){
										//cancel appointments that have the status : "Scheduled" but don't do anything to the other appointments with the statuses : Missed/Cancelled/Completed (If there are any).
										 var dialogHeader ='';
										 var dialogTableContent ='';
										 dialogHeader += '<h3><spring:message code="appointmentscheduling.AppointmentBlock.deletingConfirmation.part1"/> '+patients[1].length+' <spring:message code="appointmentscheduling.AppointmentBlock.deletingConfirmation.part2"/></h3>';
										 dialogHeader += '<u><spring:message code="appointmentscheduling.AppointmentBlock.dialogHeader"/>:</u></br>';
										 dialogTableContent += '<thead><tr><th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTableHeaderName"/></b></th><th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTableHeaderPhone"/></b></th></thead>';
										 dialogTableContent += '<tbody>';
										 for(var i=0;i<patients[1].length;i++){
											 var fullName = patients[1][i].fullName;
											 var phoneNumber = patients[1][i].phoneNumber;
											 if(phoneNumber == null){//In case phone number is missing
												phoneNumber = '<spring:message code="appointmentscheduling.AppointmentBlock.dialogTableMissing"/>';
											 }
											 dialogTableContent += '<tr><td>'+fullName+'</td><td> '+phoneNumber+'</td></tr>';
										 }
										 dialogTableContent += '</tbody>';
										 document.getElementById("deleteDialogHeader").innerHTML = dialogHeader;
										 document.getElementById("deleteDialogTable").innerHTML = dialogTableContent;
										$j('#deleteDialog').dialog('open');
										event.stopPropagation();
									}
									else{
										//only appointments which have the statuses : Missed/Cancelled/Completed.	
										//update the aciton to "void".
										document.getElementById('action').value = "void";
										//POST back to the controller in order to void the selected appointment block.
										document.forms['appointmentBlockListForm'].submit();
									}
								}
							}
							else{ 
								//update the aciton to "purge" because there are no appointments assiciated with the selected appointment block.
								document.getElementById('action').value = "purge";
								//POST back to the controller in order to purge the selected appointment block.
								document.forms['appointmentBlockListForm'].submit();
							}
						}
						else{
							//notify the user to select an appointment block.
							document.getElementById('action').value = "notifyToSelectAppointmentBlock";
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
		function initializeDialog(){
					//Dialog with buttons
					$j('#deleteDialog').dialog({
						autoOpen: false,
						height: 415,
						width: 500,
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

					//Dialog without buttons 
					$j('#notifyDialog').dialog({
						autoOpen: false,
						height: 250,
						width: 300,
						modal: true,
						resizable: false
					});
				
				
		}
		//On the page load updates necessary stuff
         $j(document).ready(function() {  
	   			//Initialize the action to do nothing (for page refresh bugs)
				document.getElementById('action').value = "";
	   			//Initialize the dialogs
				initializeDialog();
                //data table initialization
                $j('#appointmentBlocksTable').dataTable({
                 "aLengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]],
				"aoColumns" : [ {
					"bVisible"  : true												
				}, { 
					"bSortable" : true
				}, {
					"bSortable" : true
				}, {
					"bSortable" : true
				} ,{
					"iDataSort" : 8
				}, {
					"bSortable" : true
				}, {
					"bSortable" : true
				} ,{
					"bSortable" : true
				} ,{
					"bVisible" : false
								}],
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
 	        	updateAppointmentBlockTable(true);
        });
 
</script>
<h2><spring:message code="appointmentscheduling.AppointmentBlock.manage.title"/></h2>
<br/><br/>
 <form method="post" name="appointmentBlockListForm">
<fieldset id="propertiesFieldset" style="clear: both">
        <legend><spring:message code="appointmentscheduling.AppointmentBlock.legend.properties"/></legend>
        <div style="margin: 0.5em 0;">
                <table>
                        <tr>
                                <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.pickDate"/>: </td>
                                <td><input type="text" name="fromDate" id="fromDate" size="18" value="${fromDate}" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('fromDate').focus();"/></td>
                                <td><input type="text" name="toDate" id="toDate" size="18" value="${toDate}" onfocus="showDateTimePicker(this)"/><img src="${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarIcon.png" class="calendarIcon" alt="" onClick="document.getElementById('toDate').focus();"/></td>
                        </tr>
                        <tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.column.location"/>: </td>
                            <td><openmrs_tag:locationField formFieldName="locationId" initialValue="${chosenLocation}" optionHeader="[blank]"/></td>
                        </tr>
                        <tr>
                                <td><input type="button" class="appointmentBlockButton" value=<spring:message code="appointmentscheduling.AppointmentBlock.apply"/> onClick="updateAppointmentBlockTable(false)"></td>
                        </tr>
                </table>
        </div>
</fieldset>
 
<br/>
	<table id="appointmentBlocksTable" cellspacing="0">
			<thead>
				<tr>
					<th align="center" style="display:none;">select</th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.location"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.provider"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.appointmentTypes"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.date"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.startTime"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.column.endTime"/> </th>
					<th align="center"> <spring:message code="appointmentscheduling.AppointmentBlock.slotLength"/> </th>
					<th>Hidden sortable dates</th>
				</tr>
			</thead>

	</table>
	<input type="hidden" name="appointmentBlockId" id="appointmentBlockId" value="${appointmentBlockId}" />
	<input type="hidden" name="action" id="action" value="${action}" />
	<input type="hidden" name="appointmentBlocksJSON" id="appointmentBlocksJSON" value="${appointmentBlocksJSON}" />
	<openmrs:hasPrivilege privilege="Manage Provider Schedules">
		<table id="managementButtonsTable" align="center">
				<tr>
				<td><input type="submit" class="appointmentBlockButton" value="<spring:message code="appointmentscheduling.AppointmentBlock.add"/>" name="add"> </td>
				<td><input type="submit" class="appointmentBlockButton" value="<spring:message code="appointmentscheduling.AppointmentBlock.edit"/>" name="edit"> </td>
				 <td><input type="button" id="deleteBtn" class="appointmentBlockButton" value="<spring:message code="appointmentscheduling.AppointmentBlock.delete"/>" onclick="deleteFuncionality(this, event)"> </td>
				</tr>
		</table>
	</openmrs:hasPrivilege>
 </form>
 
 <div id="deleteDialog" title='<spring:message code="appointmentscheduling.AppointmentBlock.deleteDialog.title"/>'>
	<div id="deleteDialogHeader"></div>
	<table id='deleteDialogTable' class="dialogTable" style="padding:8x;" border="1" cellpadding="5">
	</table>
</div>
 <div id="notifyDialog" title='<spring:message code="appointmentscheduling.AppointmentBlock.deleteDialog.title"/>'>
	<table id='notifyDialogTable' class="dialogTable">
		<tr><td><div id="notifyDialogText"></div></td></tr>
	</table>
</div>
<%@ include file="/WEB-INF/template/footer.jsp" %>