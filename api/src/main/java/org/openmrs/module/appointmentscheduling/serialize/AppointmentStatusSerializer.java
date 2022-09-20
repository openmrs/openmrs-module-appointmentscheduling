package org.openmrs.module.appointmentscheduling.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;


import static org.openmrs.module.appointmentscheduling.Appointment.AppointmentStatus;

public class AppointmentStatusSerializer extends JsonSerializer<AppointmentStatus> {
	
	@Override
	public void serialize(AppointmentStatus appointmentStatus, JsonGenerator jsonGenerator,
	        SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
		
		jsonGenerator.writeStartObject();
		jsonGenerator.writeFieldName("code");
		jsonGenerator.writeString(appointmentStatus.name());
		jsonGenerator.writeFieldName("name");
		jsonGenerator.writeString(appointmentStatus.getName());
		jsonGenerator.writeFieldName("type");
		jsonGenerator.writeString(appointmentStatus.getType().toString());
		jsonGenerator.writeEndObject();
	}
}
