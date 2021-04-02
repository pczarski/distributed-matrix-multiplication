package com.example.grpc.client.grpcclient.controllers;

import com.example.grpc.client.grpcclient.services.GRPCClientService;
import com.example.grpc.client.grpcclient.exceptions.BadMatrixException;
import com.example.grpc.client.grpcclient.exceptions.IncompatibleMatrixException;
import com.example.grpc.client.grpcclient.exceptions.MatrixTooSmallException;
import com.example.matrix.helpers.MatrixHelpers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

/**
 * This REST interface requires for the uploaded matrices to be squared and a power of 2 in size
 */
@RestController
public class MatrixEndpoint {

	GRPCClientService grpcClientService;

	@Autowired
	public MatrixEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}

	/**
	 * Order matrix multiplication of currently uploaded matrices
	 * @param time the deadline given by the user of how long the multiplicaiton should take
	 * @return matrix multiplication result as plaintext
	 */
	@GetMapping("/multiply")
	public String multiply(@RequestParam String time) {
		Double[][] C;
		try {
			long t1 = System.currentTimeMillis();
			C = grpcClientService.multiplyMatrix(Long.parseLong(time), t1);
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

	/**
	 * upload matrix you would like to multiply.
	 * It is a put request as the matrices "A" and "B" always exist
	 * @param matrixName which matrix you want the uploaded file to be
	 * @param matrix the matrix file in raw text format
	 */
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

	/**
	 * Because why not.
	 * The channels aren't shut down automatically to allow multiple multiplications to take place on the same servers.
	 * But if the user wants to shut down the channels, the option is given.
	 * This is a get request because the user is requesting confirmation that the channels are shut down.
	 * @return
	 */
	@GetMapping("/exit")
	public String exit() {
		grpcClientService.shutDownChannels();
		return "Channels shut down";
	}
}