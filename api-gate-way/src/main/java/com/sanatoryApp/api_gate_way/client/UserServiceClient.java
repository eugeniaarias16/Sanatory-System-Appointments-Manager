package com.sanatoryApp.api_gate_way.client;

import com.sanatoryApp.api_gate_way.config.FeignConfig;
import com.sanatoryApp.api_gate_way.dto.request.ValidateCredentialsRequest;
import com.sanatoryApp.api_gate_way.dto.response.ValidateCredentialsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/api/v1",configuration = FeignConfig.class)
public interface UserServiceClient {

    @PostMapping("/auth/validate")
    ValidateCredentialsResponse validateCredentials(@RequestBody ValidateCredentialsRequest credentialsRequest);
}
