package com.gmailsystem.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);

    @Before("execution(public * *(..)) && within(com.gmailsystem..*)")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("[START] {}", joinPoint.getSignature());
    }

    @AfterReturning("execution(public * *(..)) && within(com.gmailsystem..*)")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("[END] {}", joinPoint.getSignature());
    }

    @AfterThrowing(pointcut = "execution(public * *(..)) && within(com.gmailsystem..*)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        logger.error("[EXCEPTION] {} - {}", joinPoint.getSignature(), ex.getMessage());
    }
}
