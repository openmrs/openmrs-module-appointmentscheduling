<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Scripts/jquery.dataTables.js" />
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/appointmentBlock_jQueryDatatable.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/AppointmentBlockStyle.css"/>
<openmrs:htmlInclude file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css"/>
<openmrs:htmlInclude 
	file="/moduleResources/appointmentscheduling/TableTools/media/ZeroClipboard/ZeroClipboard.js" /> 
<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/TableTools/media/js/TableTools.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/TableTools/media/css/TableTools.css" />

<openmrs:htmlInclude file="/scripts/timepicker/timepicker.js" />
<openmrs:htmlInclude
	file="/moduleResources/appointmentscheduling/Styles/jQuerySmoothness/jquery-ui-1.9.2.custom.css" />

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<openmrs:require privilege="Manage Provider Schedules" otherwise="/login.htm" redirect="/module/appointmentscheduling/appointmentBlockForm.form" />
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/engine.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/util.js'></script>
<script type="text/javascript" src='${pageContext.request.contextPath}/dwr/interface/DWRAppointmentService.js'></script>

<script type="text/javascript">	
	var dialogDataTableCreated;
	function deleteFuncionality(e,event) { //A function that defines what to do when the delete button is pressed
		//Initialize the action to do nothing.
		document.getElementById('action').value = "";
		var selectedAppointmentBlockId = "${appointmentBlock.appointmentBlockId}";
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
						$j(".resourceDiv").html('<spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.location"/>: ${appointmentBlock.location.name} | <spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.provider"/>: ${appointmentBlock.provider.name} | <spring:message code="appointmentscheduling.AppointmentBlock.dialogTable.date"/>: <fmt:formatDate type="date" value="${appointmentBlock.startDate}" pattern="dd/MM/yyyy"/>');
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
			});
		}
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
		$j('.dataTables_wrapper tbody tr').live('click',function(){
			//Dialog Table
			var dialogTable = $j('#deleteDialogTable').dataTable();
			var dialogNodes = dialogTable.fnGetNodes();
			for(var i=0; i<dialogNodes.length; i++){
				$j(dialogNodes[i]).addClass('notSelectedRow');
				$j(dialogNodes[i]).removeClass('selectedRow');
			 }		
		});
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
						//POST back to the controller in order to void/purge the selected appointment block.
						document.forms['appointmentBlockForm'].submit();
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
	function confirmPurge() {
		if (confirm("<spring:message code='appointmentscheduling.AppointmentBlock.purgeConfirmMessage' />")) {
			return true;
		} else {
			return false;
		}
	}	
	function updateAppointmentTypes(add){
        var availableAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");
        var from;
        var to;

        if(add==true)
       	{
        	from = availableAppointmentTypes;
        	to = selectedAppointmentTypes;
        }
        else
       	{
       		to = availableAppointmentTypes;
       		from = selectedAppointmentTypes;
       	}
        
        for(var i=0;i<from.options.length;i++)
       	{
       		if(from.options[i].selected == true)
       			{
       				to[to.options.length] = new Option(from.options[i].text,from.options[i].value);
       				from.remove(i);
       			}
       	}
		
		//Need to sort only the destination element
		if(add)
			$j("#currentAppointmentTypes").html($j("#currentAppointmentTypes option").sort(sortOptionsFunction));
		else
			$j("#appointmentTypeSelect").html($j("#appointmentTypeSelect option").sort(sortOptionsFunction));
	}
	
	//This function is used to sort an array of OptionElements alphabetically (ascending)
	function sortOptionsFunction(optionA, optionB){
		if(optionA.text<optionB.text)
			return -1;
		else if(optionA.text>optionB.text)
			return 1;
		else
			return 0;
	}
	function selectAllTypes()
	{
	        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");	
	        for(var i=0;i<selectedAppointmentTypes.options.length;i++)
	    	{
				selectedAppointmentTypes.options[i].selected = true;
		    }
	        if(selectedAppointmentTypes.options.length == 0){
	        	document.getElementById("emptyTypes").value = "yes";
	        }
	}
	function updateAvailableAppointmentTypes(){
        var availableAppointmentTypes = document.getElementById("appointmentTypeSelect");
        var selectedAppointmentTypes = document.getElementById("currentAppointmentTypes");
		var i=0;
		var match = false;
		while(i<availableAppointmentTypes.options.length){
        	for(var j=0;j<selectedAppointmentTypes.options.length;j++){
        		//check if the appointment type is already selected
        		if(availableAppointmentTypes[i].value == selectedAppointmentTypes[j].value){
					match = true;
        			//remove from available because the type is already selected
        			availableAppointmentTypes.remove(i);
					break;
        		}
        	}
			if(!match)
				i++;
			match = false;			
    	}
	}
	function updateToDate(object) {
		if (object.value == '') {
			var startDate = document.getElementById('startDate').value;
			if (startDate != '')
				object.value = startDate;
		}
		showDateTimePicker(object);
	}
    $j(document).ready(function() { 
		dialogDataTableCreated = false;
    	updateAvailableAppointmentTypes();
		TableToolsInit.sSwfPath = "${pageContext.request.contextPath}/moduleResources/appointmentscheduling/TableTools/media/swf/ZeroClipboard.swf";
		//Initialize the action to do nothing (for page refresh bugs)
		document.getElementById('action').value = "";
		//Initialize the dialogs
		initializeDialog();
    });
</script>

<script type="text/javascript">
	function forceMaxLength(object, maxLength) {
		if (object.value.length >= maxLength) {
			object.value = object.value.substring(0, maxLength);
		}
	}
</script>
<h2>
	<spring:message code="appointmentscheduling.AppointmentBlock.manage.title" />
</h2>

<spring:hasBindErrors name="appointmentBlock">
	<spring:message code="fix.error" />
	<br />
</spring:hasBindErrors>
<form method="post" name="appointmentBlockForm">
	<fieldset>
		<table id="appointmentBlockFormTable">
			<tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointmentscheduling.AppointmentBlock.steps.selectClinician"/></td></tr>
				<td><spring:bind path="appointmentBlock.provider">
					<select name="${status.expression}" id="providerSelect">
					<c:forEach items="${providerList}" var="provider">
						<option value="${provider.providerId}" <c:if test="${provider.providerId == status.value}">selected="selected"</c:if>><c:out value="${provider.name}"/></option>
					</c:forEach>
					</select>
				</td>
				<td>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>    
				</td>       
				</spring:bind>
				
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointmentscheduling.AppointmentBlock.steps.selectLocation"/></td></tr>
			<tr>
				<td><spring:bind path="appointmentBlock.location">
              			      <openmrs_tag:locationField formFieldName="location" initialValue="${status.value}"/>
           			         </td>
           			         <td></td>
              			      <td>
              			      <c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
             			       </spring:bind>
          				     </td>
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointmentscheduling.AppointmentBlock.steps.selectAppointmentTypes"/></td></tr>
			<tr>
				<td>
					<fieldset align="center"><legend><spring:message code="appointmentscheduling.AppointmentBlock.availableTypes"/></legend>
					<select multiple name="appointmentTypeSelect" ondblclick="updateAppointmentTypes(true)"
						id="appointmentTypeSelect">
							<c:forEach var="appointmentType" items="${appointmentTypeList}">
							<option value="${appointmentType.appointmentTypeId}"><c:out value="${appointmentType.name}"/></option>
							</c:forEach>

						</select></fieldset>
				</td>
				<td>
				<table>
				<tr><td align="center"><input type="button" class="appointmentBlockButton" id="addButton" value="->" onClick="updateAppointmentTypes(true)"></td></tr>
				<tr><td align="center"><input type="button" class="appointmentBlockButton" id="removeButton" value="<-" onClick="updateAppointmentTypes(false)"></td></tr>
				</table>
				</td>
				<td>

				<spring:bind path="appointmentBlock.types">
				<fieldset align="center"><legend><spring:message code="appointmentscheduling.AppointmentBlock.chosenTypes"/></legend>
				<select multiple name="${status.expression}" ondblclick="updateAppointmentTypes(false)" id="currentAppointmentTypes">
						<c:forEach var="appointmentType" items="${appointmentBlock.types}">
							<option value="${appointmentType.appointmentTypeId}"
								${param.appointmentTypeSelect==appointmentType.appointmentTypeId ? 'selected' : ''}><c:out value="${appointmentType.name}"/></option>
						</c:forEach>
				</select>
				</fieldset>
				<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
				</spring:bind>

				</td>
			</tr>
			<tr></tr>

			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointmentscheduling.AppointmentBlock.steps.selectTimeInterval"/></td></tr>
		
			<tr>
				<td>
					<spring:bind path="appointmentBlock.startDate">
                    <fieldset align="center"><legend><spring:message code="appointmentscheduling.AppointmentBlock.startDate"/></legend>
					<input type="text" name="startDate" id="startDate" size="16" value="${status.value}"
					onfocus="showDateTimePicker(this)"/> <img
					src="${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('startDate').focus();" />
					</fieldset>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
				<td></td>
				<td>
					<spring:bind path="appointmentBlock.endDate">
                    <fieldset align="center"><legend><spring:message code="appointmentscheduling.AppointmentBlock.endDate"/></legend>
					<input type="text" name="endDate" id="endDate" size="16" value="${status.value}"
					onfocus="updateToDate(this)" /> <img
					src="${pageContext.request.contextPath}/moduleResources/appointmentscheduling/Images/calendarIcon.png"
					class="calendarIcon" alt=""
					onClick="document.getElementById('endDate').focus();" />
                    </fieldset>
					<c:if test="${status.errorMessage != ''}"><span class="error">${status.errorMessage}</span></c:if>
					</spring:bind>
				</td>
			</tr>
			<tr class="boxHeader steps"><td colspan="3"><spring:message code="appointmentscheduling.AppointmentBlock.steps.defineTimeSlotLength"/></td></tr>
			<tr>
				<td><input type="text" name="timeSlotLength" id="timeSlotLength" value="${timeSlotLength}" size="8" />
				<spring:message code="appointmentscheduling.AppointmentBlock.minutes"/></td>
			</tr>		
			<tr>
				<td><input type="submit" class="appointmentBlockButton" onClick="selectAllTypes()" value="<spring:message code="appointmentscheduling.AppointmentBlock.save"/>" name="save"></td>
				<td></td>
				<c:if test="${not empty appointmentBlock.appointmentBlockId}">
				<td align="right"><input type="button" id="deleteBtn" class="appointmentBlockButton" value="<spring:message code="appointmentscheduling.AppointmentBlock.delete"/>" onclick="deleteFuncionality(this, event)"> </td>
				</c:if>
			</tr>
		</table>
	</fieldset>
		<input type="hidden" name="emptyTypes" id="emptyTypes" value="no" />
		<input type="hidden" name="action" id="action" value="<c:out value="${action}"/>" />
		<input type="hidden" name="appointmentBlockObject" id="appointmentBlockObject" value="<c:out value="${appointmentBlock}"/>" />
</form>
<br />
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

<%@ include file="/WEB-INF/template/footer.jsp"%>