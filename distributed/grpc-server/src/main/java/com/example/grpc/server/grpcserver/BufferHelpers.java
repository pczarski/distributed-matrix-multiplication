package com.example.grpc.server.grpcserver;

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

    public static MatrixResponse buildResponse(Double[][] C){
        MatrixResponse.Builder builder = MatrixResponse.newBuilder();
        for(Double[] row: C){
            builder.addI(Row.newBuilder().addAllJ(
                    Arrays.asList(row)
            ));
        }
        return builder.build();
    }
}
