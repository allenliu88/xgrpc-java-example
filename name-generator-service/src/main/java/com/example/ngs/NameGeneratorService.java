package com.example.ngs;

import com.example.dto.DemoRequest;
import com.example.dto.DemoResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.xgrpc.api.grpc.auto.Payload;
import io.xgrpc.api.grpc.auto.RequestGrpc;
import io.xgrpc.api.remote.request.Request;
import io.xgrpc.api.remote.request.RequestMeta;
import io.xgrpc.api.remote.response.Response;
import io.xgrpc.client.transport.DefaultRpcClientManager;
import io.xgrpc.client.transport.RpcClientManager;
import io.xgrpc.client.transport.ServerListManager;
import io.xgrpc.common.remote.ConnectionType;
import io.xgrpc.common.remote.client.RpcClient;
import io.xgrpc.common.remote.client.ServerRequestHandler;
import io.xgrpc.common.remote.client.grpc.GrpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
public class NameGeneratorService {

    public static void main(String[] args) {
        SpringApplication.run(NameGeneratorService.class, args);
    }

}


@FeignClient(name = "scientist-service-client", url = "${scientist.service.prefix.url}")
interface ScientistServiceClient {

    @GetMapping("/api/v1/scientists/random")
    String randomScientistName();

}

@FeignClient(name = "animal-service-client", url = "${animal.service.prefix.url}")
interface AnimalServiceClient {

    @GetMapping("/api/v1/animals/random")
    String randomAnimalName();

}


@RestController
@RequestMapping("/api/v1/names")
class NameResource {
    private final Logger LOGGER = LoggerFactory.getLogger(NameResource.class);

    @Autowired
    private AnimalServiceClient animalServiceClient;
    @Autowired
    private ScientistServiceClient scientistServiceClient;

    @GetMapping(path = "/random")
    public String name(@RequestHeader HttpHeaders headers) throws Exception {
        String animal = animalServiceClient.randomAnimalName();
        String name = animal;
        // String scientist = scientistServiceClient.randomScientistName();
        // String name = toKebabCase(scientist) + "-" + toKebabCase(animal);
        System.out.println("===========================================");
        System.out.println("HttpHeaders: " + headers);
        System.out.println("===========================================");

        executeDemoRequest0();
        return name;
    }

    private void executeDemoRequest0() {
        RpcClientManager rpcClientManager =
                new DefaultRpcClientManager(ConnectionType.GRPC, new ServerListManager(Arrays.asList("127.0.0.1:8848")));

        String connectId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);

        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setRequestId(requestId);
        Response demoResponse = rpcClientManager.request(rpcClientManager.build("0"), demoRequest, 5000);

        System.out.println("response type: " + (demoResponse instanceof  DemoResponse));
        System.out.println(((DemoResponse)demoResponse).getMsg());
    }

    private void executeDemoRequest() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9848).usePlaintext().build();
        RequestGrpc.RequestStub streamStub = RequestGrpc.newStub(channel);

        String connectId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        RequestMeta metadata = new RequestMeta();
        metadata.setClientIp("127.0.0.1");
        metadata.setConnectionId(connectId);

        DemoRequest demoRequest = new DemoRequest();
        demoRequest.setRequestId(requestId);
        Payload request = GrpcUtils.convert(demoRequest, metadata);

        StreamObserver<Payload> streamObserver = new StreamObserver<Payload>() {
            @Override
            public void onNext(Payload payload) {
                System.out.println("Receive data from server: " + payload);
                Object res = GrpcUtils.parse(payload);
                System.out.println("response class assert: " + (res instanceof DemoResponse));
                DemoResponse demoResponse = (DemoResponse) res;
                System.out.println("response msg: " + demoResponse.getMsg());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("complete");
            }
        };

        streamStub.request(request, streamObserver);
    }
}