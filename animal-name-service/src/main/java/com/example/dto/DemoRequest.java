package com.example.dto;

import io.xgrpc.api.remote.request.Request;

public class DemoRequest extends Request {
        @Override
        public String getModule() {
            return "demo";
        }
}