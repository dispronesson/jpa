package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut(
            "within(com.example.demo..*)"
            + " && !within(com.example.demo.component.CustomCache)"
    )
    public void allDemoExceptCache() {}

    @Pointcut("execution(* com.example.demo.repository.*+.*(..))")
    public void allRepositories() {}

    @Pointcut("allDemoExceptCache() || allRepositories()")
    public void allDemoMethods() {}

    @Before("allDemoMethods()")
    public void allDemoMethodsBefore(JoinPoint joinPoint) {
        logger.info("[METHOD] {}.{} was called",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    @After("allDemoMethods()")
    public void allDemoMethodsAfter(JoinPoint joinPoint) {
        logger.info("[METHOD] {}.{} was executed",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    @AfterReturning(value = "allDemoMethods()", returning = "result")
    public void allDemoMethodsAfterReturn(JoinPoint joinPoint, Object result) {
        logger.info("[METHOD] {}.{} returned a value: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(value = "allDemoMethods()", throwing = "ex")
    public void allDemoMethodsThrows(JoinPoint joinPoint, Throwable ex) {
        logger.error("[EXCEPTION] {}.{} threw exception: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), ex.getMessage());
    }

    @After("execution(* com.example.demo.component.CustomCache.put*(..))")
    public void linkedHashMapPut(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String cacheName = methodName.toLowerCase().contains("user") ? "userCache" : "orderCache";
        Object key = joinPoint.getArgs()[0];
        Object value = joinPoint.getArgs()[1];
        logger.info("[CACHE] {} with key '{}' was saved in {}", value, key, cacheName);
    }

    @AfterReturning(
            value = "execution(* com.example.demo.component.CustomCache.remove*(..))",
            returning = "result"
    )
    public void linkedHashMapRemove(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String cacheName = methodName.toLowerCase().contains("user") ? "userCache" : "orderCache";
        Object key = joinPoint.getArgs()[0];
        if (result != null) {
            logger.info("[CACHE] {} with key '{}' was removed from {}", result, key, cacheName);
        } else {
            logger.info("[CACHE] No item with key '{}' was removed from {}", key, cacheName);
        }
    }

    @AfterReturning(
            value = "execution(* com.example.demo.component.CustomCache.getUser(..))"
            + "|| execution(* com.example.demo.component.CustomCache.getOrder())",
            returning = "result"
    )
    public void linkedHashMapGet(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String cacheName = methodName.toLowerCase().contains("user") ? "userCache" : "orderCache";
        Object key = joinPoint.getArgs()[0];
        if (result != null) {
            logger.info("[CACHE] {} with key '{}' was received from {}", result, key, cacheName);
        } else {
            logger.info("[CACHE] No item with key '{}' found in {}", key, cacheName);
        }
    }
}
