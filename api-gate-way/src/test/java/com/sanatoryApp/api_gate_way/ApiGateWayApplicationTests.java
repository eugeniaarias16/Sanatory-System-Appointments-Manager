package com.sanatoryApp.api_gate_way;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false"
})
class ApiGateWayApplicationTests {

	@Test
	void contextLoads() {
	}

}
