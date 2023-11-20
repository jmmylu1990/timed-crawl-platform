package com.example.quartz.utils.annotation;

import java.lang.annotation.*;


@Documented
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAlias {

	String[] name() default {};
}