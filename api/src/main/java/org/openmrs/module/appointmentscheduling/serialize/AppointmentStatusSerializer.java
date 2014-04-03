package org.openmrs.module.appointmentscheduling.serialize;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

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
