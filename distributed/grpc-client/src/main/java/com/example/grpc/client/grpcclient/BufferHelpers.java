package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.getAccRequest;
import com.example.grpc.server.grpcserver.Row;
import com.example.matrix.MatrixHelpers;

import java.util.Arrays;
import java.util.List;

public class BufferHelpers {
    public static Double[][] parseMatrix(List<Row> listMatrix){
        MatrixHelpers matrixHelper = new MatrixHelpers(listMatrix.size());
        for(Row row: listMatrix){
            matrixHelper.parseRow(row.getJList());
        }
        return matrixHelper.getMatrix();
    }
    public static MatrixRequest buildRequest(Double[][] A, Double[][] B){
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
    public static getAccRequest buildGetAccRequest(){
        return getAccRequest.newBuilder().build();
    }
}
