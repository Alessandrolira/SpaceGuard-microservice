package com.example.inpe_ingestor.client;

import com.example.inpe_ingestor.dto.LoginRequest;
import com.example.inpe_ingestor.dto.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "spaceguard-auth", url = "${spaceguard.base-url}")
public interface AuthClient {

    @PostMapping("/auth/login")
    LoginResponse login(@RequestBody LoginRequest request);
}
