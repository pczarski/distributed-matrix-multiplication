package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixResponse;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.matrix.BlockMatrix;
import com.example.matrix.MatrixHelpers;

public class StubHelpers {
    public static void addAndMultiply(
            MatrixServiceGrpc.MatrixServiceBlockingStub stub, Double[][] C, BlockMatrix Ab, BlockMatrix Bb,
            int a1i, int a1j, int b1i, int b1j, int a2i, int a2j, int b2i, int b2j, int row, int col
    ){
        MatrixHelpers.mapToLargerMatrix(C,
                addBlock(
                        multiplyBlock(Ab.getBlock(a1i, a1j), Bb.getBlock(b1i, b1j), stub),
                        multiplyBlock(Ab.getBlock(a2i, a2j), Bb.getBlock(b2i, b2j), stub), stub),
                row * Ab.getBlockSize(), col * Ab.getBlockSize()
        );
    }

    public static Double[][] multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    public static Double[][] addBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.addBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    public static void blockDotProduct(int rows, int ai, int bi, BlockMatrix A, BlockMatrix B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        for(int i = 0; i<rows; i++){

        }
    }
}
