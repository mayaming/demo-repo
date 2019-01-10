package com.github.mayaming.demo_repo.dubbo.provider.service.impl;

import com.github.mayaming.demo_repo.dubbo.provider.service.DemoService;

public class DemoServiceImpl implements DemoService {
    @Override
    public Long add(Long a, Long b) {
        return a + b;
    }
}
