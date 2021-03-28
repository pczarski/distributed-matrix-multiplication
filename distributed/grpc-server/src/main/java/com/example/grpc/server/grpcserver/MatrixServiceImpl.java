package com.example.grpc.server.grpcserver;

import com.example.matrix.MatrixHelpers;
import com.example.matrix.MatrixMultiplier;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Arrays;
import java.util.List;

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase{

    private Double[][] parseMatrix(List<Row> listMatrix){
        MatrixHelpers matrixHelper = new MatrixHelpers(listMatrix.size());
        for(Row row: listMatrix){
            matrixHelper.parseRow(row.getJList());
        }
        return matrixHelper.getMatrix();
    }
    @Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        Double[][] result = MatrixMultiplier.multiplyBlock(
                parseMatrix(request.getI1List()),
                parseMatrix(request.getI2List())
        );

        MatrixResponse.Builder builder = MatrixResponse.newBuilder();
        for(Double[] row: result){
            builder.addI(Row.newBuilder().addAllJ(
                    Arrays.asList(row)
            ));
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        super.addBlock(request, responseObserver);
    }

    @Override
    public void multiplyAndAdd(MatrixRequest request, StreamObserver<Success> responseObserver) {
        super.multiplyAndAdd(request, responseObserver);
    }

    @Override
    public void getAccumulated(getAccRequest request, StreamObserver<MatrixResponse> responseObserver) {
        super.getAccumulated(request, responseObserver);
    }
}
