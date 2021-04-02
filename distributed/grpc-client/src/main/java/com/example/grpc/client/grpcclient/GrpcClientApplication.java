/**
 * Przemyslaw Leonard Czarski - Distributed Matrix Multiplication Client
 */

package com.example.grpc.client.grpcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;



@SpringBootApplication
@EnableAsync
public class GrpcClientApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(GrpcClientApplication.class, args);
	}

	@Bean(name = "asyncExecutor")
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(9);
		executor.setMaxPoolSize(32);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("StubHandler-");
		executor.initialize();
		return executor;
	}

}
