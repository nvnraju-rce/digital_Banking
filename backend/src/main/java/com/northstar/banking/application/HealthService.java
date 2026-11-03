package com.northstar.banking.application;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HealthService {
    public Map<String, String> health() {
        return Map.of("status", "UP", "mode", "MOCK_CORE_BANKING");
    }
}
