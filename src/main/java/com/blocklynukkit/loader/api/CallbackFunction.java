package com.blocklynukkit.loader.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallbackFunction {
    String[] classes() default {};
    String[] parameters() default {};
    String[] comments() default {};
}
