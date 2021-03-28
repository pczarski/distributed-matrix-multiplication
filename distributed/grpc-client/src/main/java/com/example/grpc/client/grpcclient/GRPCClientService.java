package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import com.example.matrix.MatrixHelpers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class GRPCClientService {
    static Double A[][] = {
            {1., 2., 3., 4.},
            {5., 6., 7., 8.},
            {9., 10., 11., 12.},
            {13., 14., 15., 16.}
    };

    static Double B[][] = {
            {1., 2., 3., 4.},
            {5., 6., 7., 8.},
            {9., 10., 11., 12.},
            {13., 14., 15., 16.}
    };

    public void muliplyBlock(){

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        channel.shutdown();
        Double[][] C = BufferHelpers.parseMatrix(response.getIList());
        System.out.println(MatrixHelpers.twoDArrToString(C));
    }


}
