package com.example.handler;

import com.example.dto.DemoRequest;
import com.example.dto.DemoResponse;
import com.google.auto.service.AutoService;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.core.remote.handler.RequestHandler;

@AutoService(RequestHandler.class)
public class DemoRequestHandler extends RequestHandler<DemoRequest, DemoResponse> {
    @Override
    public DemoResponse handle(DemoRequest request, RequestMeta meta) throws XgrpcException {
        return new DemoResponse().setMsg("hello world.");
    }
}