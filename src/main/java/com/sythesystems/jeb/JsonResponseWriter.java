package com.sythesystems.jeb;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;

import com.fasterxml.jackson.core.JsonGenerator;

public class JsonResponseWriter {
    public void writeJson(Object responseObject, Blueprint blueprint, JsonGenerator jsonGen)
        throws IOException
    {
        writeValue(responseObject, jsonGen, blueprint, "");
    }
    
    private void writeValue(Object value, JsonGenerator jsonGen, Blueprint blueprint, String path) throws IOException {
        if(value == null) {
            jsonGen.writeNull();
        } else if(Boolean.class.isInstance(value)) {
            jsonGen.writeBoolean((Boolean) value);
        } else if(Integer.class.isInstance(value)) {
            jsonGen.writeNumber((Integer) value);
        } else if(Long.class.isInstance(value)) {
            jsonGen.writeNumber((Long) value);
        } else if(Float.class.isInstance(value)) {
            jsonGen.writeNumber((Float) value);
        } else if(Double.class.isInstance(value)) {
            jsonGen.writeNumber((Double) value);
        } else if(BigDecimal.class.isInstance(value)) {
            jsonGen.writeNumber((BigDecimal) value);
        } else if(String.class.isInstance(value)) {
            jsonGen.writeString((String) value);
        } else if(Enum.class.isInstance(value)) {
            jsonGen.writeString(value.toString());
        } else if(Map.class.isInstance(value)) {
            Map<?,?> map = (Map<?,?>) value;
            jsonGen.writeStartObject();
            for(Object key: map.keySet()) {
                jsonGen.writeFieldName(key.toString());
                writeValue(map.get(key), jsonGen, blueprint, path);
            }
            jsonGen.writeEndObject();
        } else if (Page.class.isInstance(value)) {
            Page<?> page = (Page<?>) value;
            jsonGen.writeStartObject();
            jsonGen.writeFieldName("number");
            jsonGen.writeNumber(page.getNumber());
            jsonGen.writeFieldName("size");
            jsonGen.writeNumber(page.getSize());
            jsonGen.writeFieldName("totalPages");
            jsonGen.writeNumber(page.getTotalPages());
            jsonGen.writeFieldName("totalElements");
            jsonGen.writeNumber(page.getTotalElements());
            jsonGen.writeFieldName("content");
            writeValue(page.getContent(), jsonGen, blueprint, path);
            jsonGen.writeEndObject();
        } else if(Iterable.class.isInstance(value)) {
            jsonGen.writeStartArray();
            for(Object obj: (Iterable<?>) value) {
                writeValue(obj, jsonGen, blueprint, path);
            }
            jsonGen.writeEndArray();
        } else if(value.getClass().isArray()) {
            throw new RuntimeException("Arrays are not yet supported");
        } else if(Date.class.isInstance(value)) {
        } else if(Calendar.class.isInstance(value)) {
        } else if(LocalDate.class.isInstance(value)) {
            jsonGen.writeString(DateTimeFormatter.ISO_DATE.format((TemporalAccessor) value));
        } else if(LocalDateTime.class.isInstance(value) ||
                  OffsetDateTime.class.isInstance(value) ||
                  ZonedDateTime.class.isInstance(value))
        {
            jsonGen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((TemporalAccessor) value));
        } else {
            writeObject(new BeanWrapperImpl(value), jsonGen, blueprint, path);
        }
    }
    
    private void writeObject(BeanWrapper wrapper, JsonGenerator jsonGen, Blueprint blueprint, String path) throws IOException {
        jsonGen.writeStartObject();

        for(PropertyDescriptor desc: wrapper.getPropertyDescriptors()) {
            if (desc.getReadMethod() != null && ! desc.getReadMethod().getDeclaringClass().equals(Object.class)) {
                String name = desc.getName();
                String newPath = path + "/" + name;
                Class<?> valueClass = wrapper.getPropertyType(name);
                if(isJsonPrimitive(valueClass) || blueprint.shouldWrite(newPath)) {
                    Object value = wrapper.getPropertyValue(name);
                    jsonGen.writeFieldName(name);
                    writeValue(value, jsonGen, blueprint, newPath);
                }
            }
        }
        
        jsonGen.writeEndObject();
    }
    
    /*
     * Anything that can be represented in JSON that is not an array or an object.
     */
    private boolean isJsonPrimitive (Class<?> value) {
        return (value.isPrimitive() ||
                Boolean.class.isAssignableFrom(value) ||
                Integer.class.isAssignableFrom(value)||
                Long.class.isAssignableFrom(value)||
                Float.class.isAssignableFrom(value)||
                Double.class.isAssignableFrom(value)||
                BigDecimal.class.isAssignableFrom(value)||
                String.class.isAssignableFrom(value)||
                Date.class.isAssignableFrom(value)||
                Calendar.class.isAssignableFrom(value)||
                LocalDate.class.isAssignableFrom(value) ||
                LocalDateTime.class.isAssignableFrom(value)) ||
                OffsetDateTime.class.isAssignableFrom(value) ||
                ZonedDateTime.class.isAssignableFrom(value) ||
                Enum.class.isAssignableFrom(value);
    }
}
