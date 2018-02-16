package com.jahnelgroup.jgbay.search.rsql

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.boot.jackson.JsonComponent

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonComponent
class JsonDateConverter {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    static class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    
        private static final long serialVersionUID = -8667629790552878000L
        
        LocalDateTimeSerializer() {
            super(LocalDateTime.class)
        }
    
        @Override
        void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String dateString = null
            if(value!=null) {
                dateString = FORMATTER.format(value)
            }       
            gen.writeString(dateString)
        }
    
    }

    static class LocalDateSerializer extends StdSerializer<LocalDate> {

        private static final long serialVersionUID = -8667629790552878000L

        LocalDateSerializer() {
            super(LocalDate.class)
        }

        @Override
        void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String dateString = null
            if(value!=null) {
                dateString = FORMATTER.format(value.atStartOfDay())
            }
            gen.writeString(dateString)
        }

    }
    
    static class CustomLocalDateTimeDeserializer extends FromStringDeserializer<LocalDateTime> {

        private static final long serialVersionUID = -8667629790552878000L
        
        private static final DateTimeFormatter FORMATTER = JsonDateConverter.FORMATTER
        
        CustomLocalDateTimeDeserializer() {
            super(LocalDateTime.class)
        }
        
        @Override
        LocalDateTime _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.from(FORMATTER.parse(value))
        }

    }
    
    static class CustomLocalDateDeserializer extends FromStringDeserializer<LocalDate> {

        private static final long serialVersionUID = -8667629790552878000L
        
        private static final DateTimeFormatter FORMATTER = JsonDateConverter.FORMATTER

        CustomLocalDateDeserializer() {
            super(LocalDate.class)
        }
        
        @Override
        LocalDate _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return LocalDateTime.from(FORMATTER.parse(value)).toLocalDate()
        }

    }

}
