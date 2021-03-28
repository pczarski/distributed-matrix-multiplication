package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.*;
import com.example.matrix.MatrixHelpers;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GRPCClientService {
//    public String ping() {
//        	ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
//                .usePlaintext()
//                .build();
//
//
//        List<List<Double>> test = new ArrayList<>();
//        List<Double> e1 = new ArrayList<>();
//        List<Double> e2 = new ArrayList<>();
//        e1.add(1.);e1.add(2.);e2.add(3.);e2.add(4.);
//        test.add(e1);
//        test.add(e2);
//
//
//		PingPongServiceGrpc.PingPongServiceBlockingStub stub
//                = PingPongServiceGrpc.newBlockingStub(channel);
//
//		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
//                .setPing("")
//                .addI(Inner.newBuilder().addAllJ(test.get(0)))
//                .addI(Inner.newBuilder().addAllJ(test.get(1)))
//                .build());
//		channel.shutdown();
//		return helloResponse.getPong();
//    }

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

        MatrixResponse response = stub.multiplyBlock(buildRequest(A, B));
        channel.shutdown();
        Double[][] C = parseMatrix(response.getIList());
        System.out.println(MatrixHelpers.twoDArrToString(C));
    }

    private MatrixRequest buildRequest(Double[][] A, Double[][] B){
        MatrixRequest.Builder builder = MatrixRequest.newBuilder();
        for(Double[] row: A){
            builder.addI1(Row.newBuilder().addAllJ(
                    Arrays.asList(row)
            ));
        }
        for(Double[] row: B){
            builder.addI2(Row.newBuilder().addAllJ(
                    Arrays.asList(row)
            ));
        }
        return builder.build();
    }

    private Double[][] parseMatrix(List<Row> listMatrix){
        MatrixHelpers matrixHelper = new MatrixHelpers(listMatrix.size());
        for(Row row: listMatrix){
            matrixHelper.parseRow(row.getJList());
        }
        return matrixHelper.getMatrix();
    }

}
