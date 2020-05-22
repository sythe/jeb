package com.sythesystems.jeb;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

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
        } else if(LocalDate.class.isInstance(value) || 
                  LocalDateTime.class.isInstance(value))
        {
            jsonGen.writeString(value.toString());
        } else {
            writeObject(new BeanWrapperImpl(value), jsonGen, blueprint, path);
        }
    }
    
    private void writeObject(BeanWrapper wrapper, JsonGenerator jsonGen, Blueprint blueprint, String path) throws IOException {
        jsonGen.writeStartObject();

        for(PropertyDescriptor desc: wrapper.getPropertyDescriptors()) {
            if (! desc.getReadMethod().getDeclaringClass().equals(Object.class)) {
                String name = desc.getName();
                Object value = wrapper.getPropertyValue(name);
                String newPath = path + "/" + name;
                if(isJsonPrimitive(value) || blueprint.shouldWrite(newPath)) {
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
    private boolean isJsonPrimitive (Object value) {
        return (value == null ||
                Boolean.class.isInstance(value) ||
                Integer.class.isInstance(value)||
                Long.class.isInstance(value)||
                Float.class.isInstance(value)||
                Double.class.isInstance(value)||
                BigDecimal.class.isInstance(value)||
                String.class.isInstance(value)||
                Date.class.isInstance(value)||
                Calendar.class.isInstance(value)||
                LocalDate.class.isInstance(value) ||
                LocalDateTime.class.isInstance(value));
    }
}
