package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(com.example.demo.*)")
    public void allDemoMethods() {}

    @Before("allDemoMethods()")
    public void allDemoMethodsBefore(JoinPoint joinPoint) {
        logger.info("Executing method: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    @After("allDemoMethods()")
    public void allDemoMethodsAfter(JoinPoint joinPoint) {
        logger.info("Method {}.{} was executed successfully",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()
        );
    }

    @AfterReturning(value = "allDemoMethods()", returning = "result")
    public void allDemoMethodsAfterReturn(JoinPoint joinPoint, Object result) {
        logger.info("Method {}.{} returned value: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result);
    }

    @AfterThrowing(value = "allDemoMethods()", throwing = "ex")
    public void allDemoMethodsThrows(JoinPoint joinPoint, Throwable ex) {
        logger.error("Execution method {}.{} threw exception: {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), ex.getMessage());
    }

    @After("execution(* java.util.LinkedHashMap.put(..))")
    public void linkedHashMapPut(JoinPoint joinPoint) {
        Object value = joinPoint.getArgs()[1];
        logger.info("Object {} was saved in cache", value);
    }

    @AfterReturning(value = "execution(* java.util.LinkedHashMap.remove(..))", returning = "result")
    public void linkedHashMapRemove(JoinPoint joinPoint, Object result) {
        if (result != null) {
            logger.info("Object {} was removed from cache", result);
        }
    }

    @AfterReturning(value = "execution(* java.util.LinkedHashMap.get(..))", returning = "result")
    public void linkedHashMapGet(JoinPoint joinPoint, Object result) {
        logger.info("Object {} was received from cache", result);
    }
}
