package com.example.grpc.client.grpcclient.services;

import com.example.grpc.client.grpcclient.services.helpers.StubHelpers;
import org.springframework.stereotype.Service;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.matrix.BlockMatrix;
import com.example.matrix.helpers.MatrixHelpers;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

@Service
public class StubHandler {

    private static void printCurrentThreadName() {
        System.out.println("On Thread: " + Thread.currentThread().getName());
    }

    @Async("asyncExecutor")
    public CompletableFuture<Double[][]> multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub) {
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
        printCurrentThreadName();
        MatrixHelpers.mapToLargerMatrix(C, StubHelpers.blockDotProduct(rows, i, j, A, B, stub), i*A.getBlockSize(),j*B.getBlockSize());
        return CompletableFuture.completedFuture(null);
    }
}
