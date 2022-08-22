package com.example.dto;

import io.xgrpc.api.remote.request.ServerRequest;

public class DemoServerRequest extends ServerRequest {
    private String name;

    public String getName() {
        return name;
    }

    public DemoServerRequest setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getModule() {
        return "server module";
    }
}
