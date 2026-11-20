package com.sky.annnotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这里定义了一个注解AutoFill,这个注解的作用是指出这是一个方法,而且对应的SQL操作是insert或者update
 * 这个接口主要是表明这个接口是针对方法的,不是针对某一个类的,
 * 同时针对的只有insert和update这两个操作
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //知名针对数据库操作类insert和update
    OperationType value();
}
