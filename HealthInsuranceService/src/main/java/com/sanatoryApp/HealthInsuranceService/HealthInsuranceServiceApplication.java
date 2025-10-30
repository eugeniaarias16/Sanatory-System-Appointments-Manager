package com.sanatoryApp.HealthInsuranceService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class HealthInsuranceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthInsuranceServiceApplication.class, args);
	}

}
