package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixResponse;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;

public class StubHandler extends Thread{

    private MatrixServiceGrpc.MatrixServiceBlockingStub stub;
    private Double[][] resultMatrix;
    private Double[][] tempResult;
    private Double[][] A;
    private Double[][] B;
    private ActionType actionType;
    public StubHandler(MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] resultMatrix){
        super();
        this.stub = stub;
        this.resultMatrix = resultMatrix;
    }

    public Double[][] performSingleAction(ActionType actionType, Double[][] A, Double[][] B){
        this.actionType = actionType;
        this.A = A;
        this.B = B;
        this.start();
        return tempResult;
    }

    private Double[][] multiplyBlock(Double[][] A, Double[][] B){
        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    private Double[][] addBlock(Double[][] A, Double[][] B){
        MatrixResponse response = stub.addBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    @Override
    public void run() {
        switch (actionType){
            case ADD_BLOCK:
                tempResult = addBlock(A, B);
                break;
            case MULTIPLY_BLOCK:
                tempResult = multiplyBlock(A, B);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }
}

enum ActionType{
    MULTIPLY_BLOCK,
    ADD_BLOCK
}
