package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.Inner;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GRPCClientService {
    public String ping() {
        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();


        List<List<Double>> test = new ArrayList<>();
        List<Double> e1 = new ArrayList<>();
        List<Double> e2 = new ArrayList<>();
        e1.add(1.);e1.add(2.);e2.add(3.);e2.add(4.);
        test.add(e1);
        test.add(e2);


		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);

		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .addI(Inner.newBuilder().addAllJ(test.get(0)))
                .addI(Inner.newBuilder().addAllJ(test.get(1)))
                .build());
		channel.shutdown();
		return helloResponse.getPong();
    }
}
