package com.example.grpc.client.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class MatrixEndpoint {

	GRPCClientService grpcClientService;

	@Autowired
	public MatrixEndpoint(GRPCClientService grpcClientService) {
		this.grpcClientService = grpcClientService;
	}

	@GetMapping("/mult")
	public String mult() {
		grpcClientService.muliplyBlock();
		return "mult";
	}

	@GetMapping("/m/{time}")
	public String m(@PathVariable String time) {
		try {
			grpcClientService.multiplyMatrix(Double.parseDouble(time), System.currentTimeMillis());
		} catch (InterruptedException e){
			return "Multiplication interrupted";
		} catch (ExecutionException e) {
			return "Something went wrong"; // TODO return internal server error
		}
		return "m";
	}

	@GetMapping("/exit")
	public String exit() {
		grpcClientService.shutDownChannels();
		return "m";
	}
}