/**
 * Przemyslaw Leonard Czarski - Distributed Matrix Multiplication Client
 */

package com.example.grpc.server.grpcserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Some of the methods for the server are already documented by the client.
 * The rest should be self explanatory.
 */
@SpringBootApplication
public class GrpcServerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(GrpcServerApplication.class, args);
	}
}
