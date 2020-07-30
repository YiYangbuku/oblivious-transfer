package com.thoughtworks.security.oblivioustransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OTClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(OTClientApplication.class, args);
	}

}
