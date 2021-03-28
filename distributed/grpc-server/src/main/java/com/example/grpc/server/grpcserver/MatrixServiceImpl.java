package com.example.grpc.server.grpcserver;

import com.example.matrix.MatrixMultiplier;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase{

    private MatrixMultiplier matrixMultiplier = null;

    @Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        Double[][] result = MatrixMultiplier.multiplyBlock(
                BufferHelpers.parseMatrix(request.getI1List()),
                BufferHelpers.parseMatrix(request.getI2List())
        );
        responseObserver.onNext(BufferHelpers.buildResponse(result));
        responseObserver.onCompleted();
    }

    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        Double[][] result = MatrixMultiplier.addBlock(
                BufferHelpers.parseMatrix(request.getI1List()),
                BufferHelpers.parseMatrix(request.getI2List())
        );
        responseObserver.onNext(BufferHelpers.buildResponse(result));
        responseObserver.onCompleted();
    }

    @Override
    public void multiplyAndAdd(MatrixRequest request, StreamObserver<Success> responseObserver) {
        Double[][] A = BufferHelpers.parseMatrix(request.getI1List());
        Double[][] B = BufferHelpers.parseMatrix(request.getI2List());
        if(matrixMultiplier == null){
            matrixMultiplier = new MatrixMultiplier(A.length);
        }
        matrixMultiplier.multiplyAndAdd(A, B);
        responseObserver.onNext(Success.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAccumulated(getAccRequest request, StreamObserver<MatrixResponse> responseObserver) {
        responseObserver.onNext(BufferHelpers.buildResponse(matrixMultiplier.getAccMatrix()));
        matrixMultiplier = null; // reset the accumulator so that the server can be reused
        responseObserver.onCompleted();
    }
}
