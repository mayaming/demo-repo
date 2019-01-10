package com.github.mayaming.demo_repo.sc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by mayaming on 7/24/18.
 */
@SpringBootApplication
@Profile("client")
@EnableDiscoveryClient
@RestController
public class ConsulExampleClient {
    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 从所有服务中选择一个服务（轮询）
     */
    @GetMapping("/discover")
    public Object discover() {
        return loadBalancer.choose("consul-example-svc").getUri().toString();
    }

    /**
     * 获取所有服务
     */
    @GetMapping("/services")
    public Object services() {
        return discoveryClient.getInstances("consul-example-svc");
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsulExampleClient.class, args);
    }
}

