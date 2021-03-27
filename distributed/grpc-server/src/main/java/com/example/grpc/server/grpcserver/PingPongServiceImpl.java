package com.example.grpc.server.grpcserver;

import com.example.matrix.Matrix;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@GrpcService
public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {
    @Override
    public void ping(
        PingRequest request, StreamObserver<PongResponse> responseObserver) {

//    	List<Double> l1 = new ArrayList<>();
//    	List<Double> l2 = new ArrayList<>();
//    	l1 = request.getI(0).getJList();
//    	l2 = request.getI(1).getJList();
//    	System.out.println(l1);
//    	System.out.println(l2);
		List<List<Double>> l1 = new ArrayList<>();
		l1.add(request.getI(0).getJList());
		l1.add(request.getI(1).getJList());
		for(List<Double> ls: l1){
			System.out.println(ls);
		}
		Double[][] hi = Matrix.convertToMatrix(l1);
		for(Double[] a: hi){
			System.out.println(Arrays.toString(a));
		}


		String ping = new StringBuilder()
                .append("pong")
                .toString();        
		PongResponse response = PongResponse.newBuilder()
                .setPong(ping)
                .build();        
		responseObserver.onNext(response);
        	responseObserver.onCompleted();
    }
}
