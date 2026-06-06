package com.example.inpe_ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InpeIngestorApplication {

	public static void main(String[] args) {
		SpringApplication.run(InpeIngestorApplication.class, args);
	}

}
