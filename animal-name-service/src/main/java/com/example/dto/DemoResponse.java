package com.example.dto;

import io.xgrpc.api.remote.response.Response;

public class DemoResponse extends Response {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public DemoResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}