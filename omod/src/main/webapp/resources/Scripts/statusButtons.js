var statusButtons = new Object;

var checkIn = new Object;
checkIn.scheduled = true;
checkIn.rescheduled = true;
checkIn.walkin = false;
checkIn.waiting = false;
checkIn.inconsultation = false;
checkIn.completed = false;
checkIn.missed = false;
checkIn.canceled = false;

var startConsult = new Object;
startConsult.scheduled = false;
startConsult.rescheduled = false;
startConsult.walkin = true;
startConsult.waiting = true;
startConsult.inconsultation = false;
startConsult.completed = false;
startConsult.missed = false;
startConsult.canceled = false;

var endConsult = new Object;
endConsult.scheduled = false;
endConsult.rescheduled = false;
endConsult.walkin = false;
endConsult.waiting = false;
endConsult.inconsultation = true;
endConsult.completed = false;
endConsult.missed = false;
endConsult.canceled = false;

var miss = new Object;
miss.scheduled = true;
miss.rescheduled = true;
miss.walkin = true;
miss.waiting = true;
miss.inconsultation = false;
miss.completed = false;
miss.missed = false;
miss.canceled = false;

var cancel = new Object;
cancel.scheduled = true;
cancel.rescheduled = true;
cancel.walkin = true;
cancel.waiting = true;
cancel.inconsultation = false;
cancel.completed = false;
cancel.missed = false;
cancel.canceled = false;

statusButtons.checkInButton = checkIn;
statusButtons.startConsultButton = startConsult;
statusButtons.endConsultButton = endConsult;
statusButtons.missButton = miss;
statusButtons.cancelButton = cancel;

function updateStatusButtonsAvailabilty(appointmentStatus){
	appointmentStatus = appointmentStatus.toLowerCase();
	appointmentStatus = appointmentStatus.replace("-","");
	
	for(var buttonName in statusButtons){
		if(!statusButtons[buttonName][appointmentStatus])
			$j('#'+buttonName).attr("disabled", "disabled");
		else
			$j('#'+buttonName).attr("disabled", false);
	}
}

//Without knowing the selected line (for example on posts)
function initStatusButtons() {
			var table = $j('#appointmentsTable')
					.dataTable();
			var nNodes = table.fnGetNodes();
			var selectedRow = null;
			for ( var i = 0; i < nNodes.length; i++) {
				if($j('input:radio', nNodes[i]).attr('checked')){
					var appointmentStatus = $j('td:eq(7)', nNodes[i]).text();
					updateStatusButtonsAvailabilty(appointmentStatus);
				}
			}
}