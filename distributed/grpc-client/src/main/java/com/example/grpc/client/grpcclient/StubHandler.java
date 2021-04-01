package com.example.grpc.client.grpcclient;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.matrix.BlockMatrix;
import com.example.matrix.MatrixHelpers;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

@Service
public class StubHandler{

    private MatrixServiceGrpc.MatrixServiceBlockingStub stub;
    private Double[][] resultMatrix;
    private Double[][] tempResult;
    private Double[][] A;
    private Double[][] B;
   // private ActionType actionType;

//    public StubHandler(MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] resultMatrix){
//        super();
//        this.stub = stub;
//        this.resultMatrix = resultMatrix;
//    }

    public StubHandler() {
    }

    private static void printCurrentThreadName() throws InterruptedException{
        System.out.println("On Thread: "+ Thread.currentThread().getName());
    }

    @Async("asyncExecutor")
    public CompletableFuture<Double[][]> multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub) throws InterruptedException{
        printCurrentThreadName();
        return CompletableFuture.completedFuture(StubHelpers.multiplyBlock(A, B, stub));
    }

    @Async("asyncExecutor")
    public CompletableFuture<Double[][]> addBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub) throws InterruptedException{
        printCurrentThreadName();
        return CompletableFuture.completedFuture(StubHelpers.addBlock(A, B, stub));
    }

    @Async("asyncExecutor")
    public CompletableFuture<Object> addAndMultiply (

            MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] C, BlockMatrix Ab, BlockMatrix Bb,
            int a1i, int a1j, int b1i, int b1j, int a2i, int a2j, int b2i, int b2j, int row, int col
    ) throws InterruptedException {

        printCurrentThreadName();
        MatrixHelpers.mapToLargerMatrix(C,
                StubHelpers.addBlock(
                        StubHelpers.multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub),
                        StubHelpers.multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub), stub),
                row * Ab.getBlockSize(), col * Ab.getBlockSize()
        );
        return CompletableFuture.completedFuture(null);
    }

//    public Double[][] performSingleAction(ActionType actionType, Double[][] A, Double[][] B){
//        this.actionType = actionType;
//        this.A = A;
//        this.B = B;
//        this.start();
//        return tempResult;
//    }

//    @Override
//    public void run() {
//        switch (actionType){
//            case ADD_BLOCK:
//                tempResult = StubHelpers.addBlock(A, B, stub);
//                break;
//            case MULTIPLY_BLOCK:
//                tempResult = StubHelpers.multiplyBlock(A, B, stub);
//                break;
//            case ADD_AND_MULTIPLY:
//                //tempResult = StubHelpers.addAndMultiply();
//                break;
//            default:
//                throw new IllegalArgumentException();
//        }
//    }
}
//
//enum ActionType{
//    MULTIPLY_BLOCK,
//    ADD_BLOCK,
//    ADD_AND_MULTIPLY,
//}
//
//class handlerAction {
//
//}
