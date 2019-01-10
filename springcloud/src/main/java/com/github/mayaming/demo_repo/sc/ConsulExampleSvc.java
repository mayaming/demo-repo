package com.github.mayaming.demo_repo.sc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mayaming on 7/24/18.
 */
@Configuration
@Profile("svc")
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
public class ConsulExampleSvc {
    @GetMapping("/consul/health")
    public ResponseEntity<String> myHealthCheck() {
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @PostMapping("/svc/add")
    public ResponseEntity<Integer> add(@RequestParam(value="param1") Integer param1,
                                       @RequestParam(value="param2") Integer param2) {
        return new ResponseEntity<>(param1 + param2, HttpStatus.OK);
    }

    @GetMapping("/")
    public String home() {
        return "Hello World";
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsulExampleSvc.class, args);
    }
}
