package com.sanatoryApp.HealthInsuranceService;

import com.sanatoryApp.shared_security.config.SharedSecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@SpringBootApplication
@Import(SharedSecurityConfig.class)
public class HealthInsuranceServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthInsuranceServiceApplication.class, args);
	}

}
