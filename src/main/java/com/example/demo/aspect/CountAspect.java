package com.example.demo.aspect;

import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    @Around("getMappingMethods()")
    public Object countRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        counter.incrementAndGet();
        return joinPoint.proceed();
    }

    public int getCounter() {
        return counter.get();
    }
}
