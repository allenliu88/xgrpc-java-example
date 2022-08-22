package com.example.ans;

import static strman.Strman.toKebabCase;

import com.example.dto.DemoRequest;
import com.example.dto.DemoResponse;
import com.example.dto.DemoServerRequest;
import com.example.dto.DemoServerResponse;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableMap;
import io.xgrpc.api.exception.XgrpcException;
import io.xgrpc.api.remote.PushCallBack;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.core.GrpcServerBootstrap;
import io.xgrpc.core.GuiceInjectorBootstrap;
import io.xgrpc.core.remote.grpc.BaseGrpcServer;
import io.xgrpc.core.remote.handler.RequestHandler;
import io.xgrpc.core.remote.push.RpcPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableFeignClients
public class AnimalNameService {

    public static void main(String[] args) {
        // GrpcServerBootstrap.startServer();
        GuiceInjectorBootstrap.getBean(BaseGrpcServer.class);
        SpringApplication.run(AnimalNameService.class, args);
    }

}

@FeignClient(name = "scientist-service-client", url = "${scientist.service.prefix.url}")
interface ScientistServiceClient {

    @GetMapping("/api/v1/scientists/random")
    String randomScientistName();

}

@RestController
@RequestMapping("/api/v1/animals")
class AnimalNameResource {
    private final Logger LOGGER = LoggerFactory.getLogger(AnimalNameResource.class);

    private final List<String> animalNames;
    private Random random;

    @Autowired
    private ScientistServiceClient scientistServiceClient;


    public AnimalNameResource() throws IOException {
        InputStream inputStream = new ClassPathResource("/animals.txt").getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            animalNames = reader.lines().collect(Collectors.toList());
        }
        random = new Random();
    }

    @GetMapping(path = "/random")
    public String name(@RequestHeader HttpHeaders headers) {
        String name = animalNames.get(random.nextInt(animalNames.size()));
        // String scientist = scientistServiceClient.randomScientistName();

        // name = toKebabCase(scientist) + "-" + toKebabCase(name);

        System.out.println("===========================================");
        System.out.println("HttpHeaders: " + headers);
        System.out.println("===========================================");
        // throw new RuntimeException("Invalid Operations.");

        return name;
    }

    @GetMapping(path = "/push")
    public String push(@RequestHeader HttpHeaders headers) {
        String name = animalNames.get(random.nextInt(animalNames.size()));
        // String scientist = scientistServiceClient.randomScientistName();

        // name = toKebabCase(scientist) + "-" + toKebabCase(name);

        System.out.println("===========================================");
        System.out.println("HttpHeaders: " + headers);
        System.out.println("===========================================");
        // throw new RuntimeException("Invalid Operations.");


        this.syncServerRequest();
        // this.asyncServerRequest();
        return name;
    }

    /**
     * 服务器端向客户端发送同步请求
     */
    private void syncServerRequest() {
        RpcPushService rpcPushService = GuiceInjectorBootstrap.getBean(RpcPushService.class);
        DemoServerRequest demoServerRequest = new DemoServerRequest().setName("AnimalNameService");
        Map<String, Response> ret = rpcPushService.pushWithoutAck(Collections.singletonMap("uuidName", "NameGeneratorService"), demoServerRequest);
        ret.forEach((key, value) -> System.out.println("========From client connection id [" + key + "], msg: " + ((DemoServerResponse)value).getMsg()));
    }

    /**
     * 服务器端向客户端发送异步请求
     */
    private void asyncServerRequest() {
        RpcPushService rpcPushService = GuiceInjectorBootstrap.getBean(RpcPushService.class);
        DemoServerRequest demoServerRequest = new DemoServerRequest().setName("AnimalNameService");
        rpcPushService.pushWithCallback(
                Collections.singletonMap("uuidName", "NameGeneratorService"),
                demoServerRequest,
                new PushCallBack() {
                    @Override
                    public long getTimeout() {
                        return 10000;
                    }

                    @Override
                    public void onSuccess(Response response) {
                        System.out.println("========From client async server request, msg: " + ((DemoServerResponse)response).getMsg());
                    }

                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                },
                null
        );
    }
}

