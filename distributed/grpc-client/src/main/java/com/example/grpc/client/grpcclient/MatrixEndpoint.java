package com.example.grpc.client.grpcclient;

import com.example.grpc.client.grpcclient.exceptions.BadMatrixException;
import com.example.grpc.client.grpcclient.exceptions.IncompatibleMatrixException;
import com.example.grpc.client.grpcclient.exceptions.MatrixTooSmallException;
import com.example.matrix.MatrixHelpers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@RestController
public class MatrixEndpoint {

	GRPCClientService grpcClientService;

	@Autowired
	public MatrixEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}

	@GetMapping("/multiply")
	public String multiply(@RequestParam String time) {
		Double[][] C;
		try {
			long t1 = System.currentTimeMillis();
			C = grpcClientService.multiplyMatrix(Double.parseDouble(time), t1);
			long t2 = System.currentTimeMillis();
			System.out.println("Deadline: " + time + "\nTime taken: "+(t2-t1));
		} catch (InterruptedException e){
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Client service interrupted");
		} catch (ExecutionException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (IncompatibleMatrixException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		return MatrixHelpers.matrixToString(C);
	}

	@PutMapping("/matrix/{matrixName}")
	public void uploadMatrix(@PathVariable String matrixName, @RequestBody String matrix){
		Double[][] parsedMatrix;
		try {
			parsedMatrix = MatrixHelpers.parseMatrixFromString(matrix);
			if(!MatrixHelpers.isPowerOf2(parsedMatrix)){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matrix size must be a power of two");
			}
			// matrix must either A or B
			if(matrixName.equalsIgnoreCase("a")){
				grpcClientService.setA(parsedMatrix);
			} else if(matrixName.equalsIgnoreCase("b")){
				grpcClientService.setB(parsedMatrix);
			} else {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Matrix name must be either A or B");
			}

		} catch (IllegalArgumentException e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every value must be a number");
		} catch (IndexOutOfBoundsException e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Matrix must be square");
		} catch (MatrixTooSmallException | IncompatibleMatrixException | BadMatrixException e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/exit")
	public String exit() {
		grpcClientService.shutDownChannels();
		return "exit";
	}
}