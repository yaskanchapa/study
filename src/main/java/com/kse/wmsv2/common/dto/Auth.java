package com.kse.wmsv2.common.dto;


import javax.management.relation.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {

    Role role() default Role.USER;


    enum Role{
        USER,
        CUSTOM_USER,
        WAREHOUSE_USER,
        ADMIN
    }




}
