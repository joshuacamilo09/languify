package io.languify.infra.health.controller;

import io.languify.infra.health.dto.CheckHealthDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping
    public ResponseEntity<CheckHealthDTO>checkHealth() {
        return ResponseEntity.ok().body(new CheckHealthDTO());
    }
}
