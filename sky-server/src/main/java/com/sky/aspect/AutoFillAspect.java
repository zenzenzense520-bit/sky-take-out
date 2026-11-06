package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 公共字段自动填充切面
 * 拦截标注 @AutoFill 的 Mapper 方法，通过反射为实体赋审计字段
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /** 切入点：mapper 包下标注了 @AutoFill 的方法 */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        // 获取方法上的注解以判断操作类型
        org.aspectj.lang.reflect.MethodSignature methodSignature =
                (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        try {
            if (operationType == OperationType.INSERT) {
                invoke(entity, AutoFillConstant.CREATE_TIME, LocalDateTime.class, now);
                invoke(entity, AutoFillConstant.UPDATE_TIME, LocalDateTime.class, now);
                invoke(entity, AutoFillConstant.CREATE_USER, Long.class, currentId);
                invoke(entity, AutoFillConstant.UPDATE_USER, Long.class, currentId);
            } else if (operationType == OperationType.UPDATE) {
                invoke(entity, AutoFillConstant.UPDATE_TIME, LocalDateTime.class, now);
                invoke(entity, AutoFillConstant.UPDATE_USER, Long.class, currentId);
            }
        } catch (Exception e) {
            log.error("公共字段自动填充失败", e);
            throw new RuntimeException("公共字段自动填充失败", e);
        }
    }

    /** 反射调用实体的 setter 方法 */
    private void invoke(Object target, String methodName, Class<?> paramType, Object value) throws Exception {
        Method method = target.getClass().getDeclaredMethod("set" +
                methodName.substring(0, 1).toUpperCase() + methodName.substring(1), paramType);
        method.invoke(target, value);
    }
}
