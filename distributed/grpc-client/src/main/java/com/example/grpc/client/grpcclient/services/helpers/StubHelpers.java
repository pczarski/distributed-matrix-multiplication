package com.example.grpc.client.grpcclient.services.helpers;

import com.example.grpc.server.grpcserver.MatrixResponse;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import com.example.matrix.BlockMatrix;
import com.example.matrix.helpers.MatrixHelpers;

public class StubHelpers {

    public static Double[][] multiplyBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.multiplyBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    public static Double[][] addBlock(Double[][] A, Double[][] B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        MatrixResponse response = stub.addBlock(BufferHelpers.buildRequest(A, B));
        return BufferHelpers.parseMatrix(response.getIList());
    }

    /**
     * calculate the dot product of "blocks" selected by block at row ai and column bi
     * @param rows number of "block rows"
     * @param ai row for A
     * @param bi column for B
     * @param A block matrix A
     * @param B lock matrix B
     * @param stub the blocking stub
     * @return block dot product result
     */
    public static Double[][] blockDotProduct(int rows, int ai, int bi, BlockMatrix A, BlockMatrix B, MatrixServiceGrpc.MatrixServiceBlockingStub stub){
        Double[][] temp;
        Double[][] res = MatrixHelpers.zeroMatrix(A.getBlockSize());
        for(int i = 0; i<rows; i++){
            temp = multiplyBlock(A.getBlock(ai, i), B.getBlock(i, bi), stub);
            res = addBlock(res, temp, stub);
        }
        return res;
    }
}
