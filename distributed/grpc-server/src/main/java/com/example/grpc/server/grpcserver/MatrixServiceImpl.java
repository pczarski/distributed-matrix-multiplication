package com.example.grpc.server.grpcserver;

import com.example.matrix.MatrixMultiplier;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase{

    private void done(){
        System.out.println("done");
    }
    @Override
    public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        System.out.println("multiplying...");
        Double[][] result = MatrixMultiplier.multiplyBlock(
                BufferHelpers.parseMatrix(request.getI1List()),
                BufferHelpers.parseMatrix(request.getI2List())
        );
        responseObserver.onNext(BufferHelpers.buildResponse(result));
        responseObserver.onCompleted();
        done();
    }

    @Override
    public void addBlock(MatrixRequest request, StreamObserver<MatrixResponse> responseObserver) {
        System.out.println("adding...");
        Double[][] result = MatrixMultiplier.addBlock(
                BufferHelpers.parseMatrix(request.getI1List()),
                BufferHelpers.parseMatrix(request.getI2List())
        );
        responseObserver.onNext(BufferHelpers.buildResponse(result));
        responseObserver.onCompleted();
        done();
    }
}
