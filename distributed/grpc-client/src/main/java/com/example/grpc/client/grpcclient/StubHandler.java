package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixServiceGrpc;

public class StubHandler extends Thread{

    private MatrixServiceGrpc.MatrixServiceBlockingStub stub;
    private Double[][] resultMatrix;
    public StubHandler(MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] resultMatrix){
        super();
        this.stub = stub;
        this.resultMatrix = resultMatrix;
    }

    public void run() {

    }
}
