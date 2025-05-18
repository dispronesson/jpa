package com.example.demo.aspect;

import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CountAspect {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Pointcut("execution(@org.springframework.web.bind.annotation.GetMapping * "
            + "com.example.demo.controller.*.*(..)) "
            + "&& !within(com.example.demo.controller.CountController)")
    public void getMappingMethods() {}

    @Before("getMappingMethods()")
    public void countRequests() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }
}
