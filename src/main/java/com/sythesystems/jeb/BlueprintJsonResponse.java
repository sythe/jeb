package com.sythesystems.jeb;

public class BlueprintJsonResponse {
    private Object responseObject;
    private String blueprintName;

    public BlueprintJsonResponse(Object responseObject, String blueprintName) {
        this.responseObject = responseObject;
        this.blueprintName = blueprintName;
    }

    public Object getResponseObject() {
        return responseObject;
    }

    public String getBlueprintName() {
        return blueprintName;
    }
}
