package com.example.grpc.client.grpcclient;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.matrix.BlockMatrix;
import com.example.matrix.MatrixHelpers;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

@Service
public class StubHandler {

    private static void printCurrentThreadName() {
        System.out.println("On Thread: " + Thread.currentThread().getName());
    }

    @Async("asyncExecutor")
    public CompletableFuture<Double[][]> multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub) throws InterruptedException {
        printCurrentThreadName();
        return CompletableFuture.completedFuture(StubHelpers.multiplyBlock(A, B, stub));
    }

    @Async("asyncExecutor")
    public CompletableFuture<Double[][]> addBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub) {
        printCurrentThreadName();
        return CompletableFuture.completedFuture(StubHelpers.addBlock(A, B, stub));
    }

    @Async("asyncExecutor")
    public CompletableFuture<Object> blockDotProduct(int rows, int i, int j, BlockMatrix A, BlockMatrix B, Double[][] C, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        Double[][] result = new Double[C.length][C.length];
        MatrixHelpers.mapToLargerMatrix(C, StubHelpers.blockDotProduct(rows, i, j, A, B, stub), i*A.getBlockSize(),j*B.getBlockSize());
        return CompletableFuture.completedFuture(null);
    }

//    @Async("asyncExecutor")
//    public CompletableFuture<Object> addAndMultiply(
//
//            MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] C, BlockMatrix Ab, BlockMatrix Bb,
//            int a1i, int a1j, int b1i, int b1j, int a2i, int a2j, int b2i, int b2j, int row, int col
//    ) throws InterruptedException {
//
//        printCurrentThreadName();
//        MatrixHelpers.mapToLargerMatrix(C,
//                StubHelpers.addBlock(
//                        StubHelpers.multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub),
//                        StubHelpers.multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub), stub),
//                row * Ab.getBlockSize(), col * Ab.getBlockSize()
//        );
//        return CompletableFuture.completedFuture(null);
//    }
}
