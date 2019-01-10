package com.github.mayaming.demo_repo.dubbo.consumer.controller;

import com.github.mayaming.demo_repo.dubbo.provider.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/add", params = {"a", "b"})
    public Long add(@RequestParam("a") Long a, @RequestParam("b") Long b) {
        return demoService.add(a, b);
    }
}
