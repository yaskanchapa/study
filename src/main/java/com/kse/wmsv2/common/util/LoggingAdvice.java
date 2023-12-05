package com.kse.wmsv2.common.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LoggingAdvice {


    @Pointcut("execution(* com.kse.wmsv2.*.controller..*.**(..))")
    public void cut(){}

    @Before("cut()")
    public void before(JoinPoint joinPoint){
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("=======================================================================");
        log.info("Start :  " + method.toString());
        Object[] args = joinPoint.getArgs();
        for(Object obj : args) {
            if(obj == null) {
                log.info("INPUT PARAMETER :   type : null  / value : null");
            } else {
                log.info("INPUT PARAMETER :   type : "+obj.getClass().getSimpleName() + "  / value : "+obj);
            }
        }
    }


    @AfterReturning(value = "cut()", returning = "obj")
    public void afterReturn(JoinPoint joinPoint, Object obj){
        log.info("return Value : " + obj);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        log.info("END : " + method.toString());
        log.info("=======================================================================");
    }


}
