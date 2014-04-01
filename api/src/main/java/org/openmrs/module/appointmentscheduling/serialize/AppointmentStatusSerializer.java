package org.openmrs.module.appointmentscheduling.serialize;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.openmrs.module.appointmentscheduling.Appointment;

import java.io.IOException;

public class AppointmentStatusSerializer extends JsonSerializer<Appointment.AppointmentStatus> {
	
	@Override
	public void serialize(Appointment.AppointmentStatus appointmentStatus, JsonGenerator jsonGenerator,
	        SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
		
		jsonGenerator.writeStartObject();
		jsonGenerator.writeFieldName("code");
		jsonGenerator.writeString(appointmentStatus.name());
		jsonGenerator.writeFieldName("name");
		jsonGenerator.writeString(appointmentStatus.getName());
		jsonGenerator.writeFieldName("active");
		jsonGenerator.writeBoolean(appointmentStatus.isActive());
		jsonGenerator.writeFieldName("cancelled");
		jsonGenerator.writeBoolean(appointmentStatus.isActive());
		jsonGenerator.writeEndObject();
	}
}
