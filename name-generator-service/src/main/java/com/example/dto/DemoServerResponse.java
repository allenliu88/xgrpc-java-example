package com.example.dto;

import io.xgrpc.api.remote.response.Response;

public class DemoServerResponse extends Response {
    String msg;

    public String getMsg() {
        return msg;
    }

    public DemoServerResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
