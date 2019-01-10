package com.github.mayaming.demo_repo.dubbo.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

@SpringBootApplication
@ImportResource(locations = "classpath:provider-ctx.xml")
public class Provider {
    public static void main(String[] args) throws IOException {
        SpringApplication app = new SpringApplication(Provider.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
        System.in.read();
    }
}
