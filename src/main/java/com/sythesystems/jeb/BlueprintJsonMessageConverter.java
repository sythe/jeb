package com.sythesystems.jeb;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class BlueprintJsonMessageConverter implements HttpMessageConverter<BlueprintJsonResponse> {

    private JsonFactory jsonFactory = new JsonFactory();
    
    private JsonResponseWriter jsonResponseWriter = new JsonResponseWriter();
    
    @Autowired
    private Map<String, Blueprint> blueprints;
    
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return clazz.equals(BlueprintJsonResponse.class);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Arrays.asList(MediaType.APPLICATION_JSON);
    }

    @Override
    public BlueprintJsonResponse read(Class<? extends BlueprintJsonResponse> clazz, HttpInputMessage input) throws IOException, HttpMessageNotReadableException {
        // canRead() always returns false, so we should never get here
        return null;
    }

    @Override
    public void write(BlueprintJsonResponse response, MediaType mediaType, HttpOutputMessage output)
            throws IOException, HttpMessageNotWritableException
    {
        output.getHeaders().add("Content-Type", "application/json");

        JsonGenerator jsonGen = jsonFactory.createGenerator(output.getBody());
        
        Blueprint blueprint = blueprints.get(response.getBlueprintName());
        if(blueprint == null) {
            blueprint = new Blueprint();
        }
        jsonResponseWriter.writeJson(response.getResponseObject(), blueprint,  jsonGen);
        
        jsonGen.close();
    }
    
}
