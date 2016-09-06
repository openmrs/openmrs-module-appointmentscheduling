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

<openmrs:require privilege="View Provider Schedules" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentBlockList.list" />
 <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">
	var selectedRowData;
	var dialogDataTableCreated;
	function updateAppointmentBlockTable(firstTime) //updates the table if changes occur
	{
		var blocksAsJson;
		var fromDate = document.getElementById('fromDate').value;
		var toDate = document.getElementById('toDate').value;
		//DWR call for date valdition
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
		//AppointmentType filter
		var appointmentTypeId = null;
		var selectedAppointmentType = document.getElementById("appointmentTypeSelect");
		if(selectedAppointmentType != null){
			appointmentTypeId = selectedAppointmentType.options[selectedAppointmentType.selectedIndex].value;
		}
		document.getElementById("chosenType").value = appointmentTypeId;
		//Provider filter
		var providerId = null;
		var selectedProvider = document.getElementById("providerSelect");
		if(selectedProvider != null){
			providerId = selectedProvider.options[selectedProvider.selectedIndex].value;
		}
		document.getElementById("chosenProvider").value = providerId;
		//Location filter
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
		//Check if  change to calendar view is needed
		var selectedView = document.getElementById("viewSelect");
		if(selectedView.options[selectedView.selectedIndex].value == "calendarView"){
			changeToCalendarView();
		}
		//DWR call for getting the appointment blocks that have the selected properties
		DWRAppointmentService.getAppointmentBlocks(fromDate,toDate,locationId, providerId, appointmentTypeId,function(appointmentBlocks){
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
				//Calendar event properties
				block.id = appointmentBlocks[i].appointmentBlockId;
				block.title = appointmentBlocks[i].provider+" ("+appointmentBlocks[i].location+")";
				block.start = appointmentBlocks[i].startDate.toString();
				block.end = appointmentBlocks[i].endDate.toString();
				block.allDay = false;
				//Additional appointment block properties
				block.provider = appointmentBlocks[i].provider;
				block.location = appointmentBlocks[i].location;
				block.appointmentTypes = appointmentTypes;
				block.startTime=startTime;
				block.endTime=endTime;
				block.timeSlotLength=appointmentBlocks[i].timeSlotLength;
				
				blocksAsJSON.push(block);
			}
			if(count == appointmentBlocks.length){
				if(appointmentBlocks.length == 0){ //if there was no appointment blocks, clear table and destroy
					var oTable = $j('#appointmentBlocksTable').dataTable();
					oTable.fnClearTable();
					oTable.fnDestroy();
				 }
				createBlocksDataTable(); //Initialize data table
				//Toggle Checked Row
				$j('.dataTables_wrapper tbody tr').live('click',dataTableClickFunction);
				var theTable = $j('#appointmentBlocksTable').dataTable();
				theTable.fnSort([[4,'asc']]);
				theTable.fnAdjustColumnSizing();				
			}	
		  

		});
	}
	
	function dataTableClickFunction(){
		//Blocks Table
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
		//row data
		var rowData = nNodes = table.fnGetData(this);
		selectedRowData = rowData;
		if(dialogDataTableCreated){
			//Dialog Table
			var dialogTable = $j('#deleteDialogTable').dataTable();
			var dialogNodes = dialogTable.fnGetNodes();
			for(var i=0; i<dialogNodes.length; i++){
				$j(dialogNodes[i]).addClass('notSelectedRow');
				$j(dialogNodes[i]).removeClass('selectedRow');
			 }
		 }
	}
	function changeToCalendarView(){ //A function that updates the action to change the view to clanedar view and submits the form
		//change action to calendar view 
		document.getElementById('action').value = "changeToCalendarView";
		//POST back in order to redirect to the calendar view via the controller.
		document.forms['appointmentBlockListForm'].submit();
	}
		
	function deleteFuncionality(e, event) { //A function that defines what to do when the delete button is pressed
		//Initialize the action to do nothing.
		document.getElementById('action').value = "";
		var selectedAppointmentBlockId = document.getElementById("appointmentBlockId").value;
		if(selectedAppointmentBlockId != null){
			//calling the DWR method in order to get the appointments that exits in the selected appointment block.
			DWRAppointmentService.getPatientsInAppointmentBlock(selectedAppointmentBlockId,function(appointments){
				if(appointments != null){
					var totalAppointments = appointments[0].length+appointments[1].length+appointments[2].length;
					if(appointments[0].length>0){	
						//Notify the user that the block cannot be deleted because it has active appointments
						document.getElementById("notifyDialogText").innerHTML = '<spring:message code="appointmentscheduling.AppointmentBlock.cannotBeDeleted.part1"/> '+appointments[0].length+' <spring:message code="appointmentscheduling.AppointmentBlock.cannotBeDeleted.part2"/>';
						$j('#notifyDialog').dialog('open');
						event.stopPropagation();
					}
					else{ //appointments[0].length = 0 (no active appointments within the appointment block)
						//cancel appointments that have the status : "Scheduled" but don't do anything to the other appointments with the statuses : Missed/Cancelled/Completed (If there are any).
						//clear previous table if exists
						if (dialogDataTableCreated) {
							var oTable = $j('#deleteDialogTable').dataTable();
							oTable.fnClearTable();
							oTable.fnDestroy();
							dialogDataTableCreated = false;
						}
						//Dialog Content
						var dialogHeader ='';
						var dialogTableContent ='';
						dialogHeader += '<h3><spring:message code="appointmentscheduling.AppointmentBlock.deletingConfirmation.part1"/> '+appointments[1].length+' <spring:message code="appointmentscheduling.AppointmentBlock.deletingConfirmation.part2"/></h3></br>';
						dialogHeader += '<u><spring:message code="appointmentscheduling.AppointmentBlock.dialogHeader"/>:</u></br></br>';
						//Table's header
						dialogTableContent += '<thead><tr>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.openMrsId"/></b></th>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.name"/></b></th>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.time"/></b></th>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.type"/></b></th>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.phoneNumber"/></b></th>';
						dialogTableContent +='<th align="center"><b><spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.reason"/></b></th>';
						dialogTableContent += '</tr></thead>';
						//Table's body
						dialogTableContent += '<tbody>';
						for(var i=0;i<appointments[1].length;i++){
							var id = appointments[1][i].patient.identifiers[0].split(":")[1];
							var fullName = appointments[1][i].patient.fullName;
							var phoneNumber = appointments[1][i].patient.phoneNumber;
							var date = appointments[1][i].date;
							var time = appointments[1][i].startTime+"-"+ appointments[1][i].endTime;
							var type = appointments[1][i].appointmentType;
							var reason = appointments[1][i].reason;
							if(phoneNumber == null){//In case phone number is missing
								phoneNumber = '<spring:message code="appointmentscheduling.AppointmentBlock.dialogTableMissing"/>';
							}
							if(reason == null){//In case appointment reason is missing
								reason = '<spring:message code="appointmentscheduling.AppointmentBlock.dialogTableMissing"/>';
							}
							dialogTableContent += '<tr><td>'+id+'</td>';
							dialogTableContent += '<td> '+fullName+'</td>';
							dialogTableContent += '<td> '+time+'</td>';
							dialogTableContent += '<td> '+type+'</td>';
							dialogTableContent += '<td> '+phoneNumber+'</td>';
							dialogTableContent += '<td> '+reason+'</td>';
						}
						dialogTableContent += '</tbody>';
						document.getElementById("deleteDialogHeader").innerHTML = dialogHeader;
						document.getElementById("deleteDialogTable").innerHTML = dialogTableContent;	
						createDialogDataTable(); //Apply dataTable to the dialog table
						var appointmentBlockDetails =  '<spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.location"/>: '+selectedRowData[1]+' | <spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.provider"/>: '+selectedRowData[2]+' | <spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.date"/>: '+selectedRowData[4];
						$j(".resourceDiv").html(appointmentBlockDetails);
						if(totalAppointments != 0){
							//update the aciton to "void" because there are appointments assiciated with the selected appointment block.
							document.getElementById('action').value = "void";
						}
						else{
							//update the aciton to "purge" because there are no appointments assiciated with the selected appointment block.
							document.getElementById('action').value = "purge";
						}
						$j('#deleteDialog').dialog('open');
						event.stopPropagation();
					}
				}
				else{
					//notify the user to select an appointment block.
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
	function createBlocksDataTable(){
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
									"<img id='calendarViewButton' src='${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarViewIcon.png' Title='Calendar view' class='appointmentBlockCalendarIcon' onclick='changeToCalendarView()'>"+
									"<br/>"
									);
				}
			});		
		}
	function createDialogDataTable(){
			var theTable = $j('#deleteDialogTable').dataTable({
			"aoColumns" : [ {
				"bSortable"  : true												
			}, { 
				"bSortable"  : true												
			}, { 
				"bSortable"  : true												
			}, { 
				"bSortable"  : true												
			}, { 
				"bSortable"  : true												
			}, { 			
				"bSortable" : true
			}],
			"aLengthMenu": [[-1], ["All"]],
			"iDisplayLength": -1,
			"sDom" : "<'fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix' fl <'resourceDiv'>>t<'fg-toolbar ui-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix'ip<'toolbar' T>>",
			"bLengthChange": true,
			"bFilter": false,
			"bInfo": true,
			"bPaginate": true,
			"bJQueryUI": true
			});
			theTable.fnSort([[2,'asc']]);
			theTable.fnAdjustColumnSizing();
			dialogDataTableCreated = true;
			//Toggle Checked Row
			$j('.dataTables_wrapper tbody tr').live('click',dataTableClickFunction);
		}
	function initializeDialog(){
		//Dialog with buttons
		$j('#deleteDialog').dialog({
			autoOpen: false,
			height: 'auto',
			width: 'auto',
			modal: false,
			resizable: false,
			buttons: {
				"<spring:message code='general.cancel' />": function() {
					//update the aciton to do nothing.
					document.getElementById('action').value = "";
					//close the dialog.
					$j(this).dialog("close");
				},
				"<spring:message code='general.submit' />": function() {
						//POST back to the controller in order to void\purge the selected appointment block.
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
	function locationInitialize(){
		//If the user is using "Simple" version
		if ($j('#locationId').length > 0) {
			var selectLocation = $j('#locationId');
			//Set the 'All locations' option text (Default is empty string)
			if (selectLocation[0][0].innerHTML == '')
				selectLocation[0][0].innerHTML = "(<spring:message code='appointmentscheduling.AppointmentBlock.filters.locationNotSpecified'/>)";

		}
	}
	$j(document).ready(function() {  //On the page load
		dialogDataTableCreated = false;
		//Initialize selected view as table view
		document.getElementById("viewSelect").selectedIndex = 0;
		//zeroClipboard (data tables printing options)
		TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointmentscheduling/TableTools/media/swf/ZeroClipboard.swf";
		//Initialize the action to do nothing (for page refresh bugs)
		document.getElementById('action').value = "";
		//Initialize the dialogs
		initializeDialog();
		//data table initialization
		//createDialogDataTable();
		createBlocksDataTable();
		//Some more visual settings
		var theTable = $j('#appointmentBlocksTable').dataTable();
		theTable.fnAdjustColumnSizing();
		//Fill the content of the appointmnet blocks table
		updateAppointmentBlockTable(true);
		//Initialize location
		locationInitialize();
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
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.provider"/>: </td>
   							<td>
	   							<select name="providerSelect" id="providerSelect">
									<option value="" ${null==chosenProvider ? 'selected' : ''}>
										(<spring:message
											code="appointmentscheduling.AppointmentBlock.filters.providerNotSpecified" />)
									</option>
									<c:forEach var="provider" items="${providerList}">
										<option value="${provider.providerId}"
											${provider.providerId==chosenProvider ? 'selected' : ''}><c:out value="${provider.name}"/></option>
									</c:forEach>
								</select>
							</td>
                        </tr>
                        <tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.type"/>: </td>
   							<td>
	   							<select name="appointmentTypeSelect" id="appointmentTypeSelect">
									<option value="" ${null==chosenType ? 'selected' : ''}>
										(<spring:message
											code="appointmentscheduling.AppointmentBlock.filters.typeNotSpecified" />)
									</option>
									<c:forEach var="type" items="${appointmentTypeList}">
										<option value="${type.appointmentTypeId}"
											${type.appointmentTypeId==chosenType ? 'selected' : ''}><c:out value="${type.name}"/></option>
									</c:forEach>
								</select>
							</td>
                        </tr>
						<tr>
                            <td class="formLabel"><spring:message code="appointmentscheduling.AppointmentBlock.filters.view"/>: </td>
   							<td>
	   							<select name="viewSelect" id="viewSelect">
									<option value="tableView"><spring:message code="appointmentscheduling.AppointmentBlock.tableView"/></option>
									<option value="calendarView"><spring:message code="appointmentscheduling.AppointmentBlock.calendarView"/></option>
								</select>
							</td>
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
	<input type="hidden" name="action" id="action" value="<c:out value="${action}"/>" />
	<input type="hidden" name="chosenProvider" id="chosenProvider" value="<c:out value="${chosenProvider}"/>" />
	<input type="hidden" name="chosenType" id="chosenType" value="<c:out value="${chosenType}"/>" />
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