package org.example.annos;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleLimit {

    int[] roleLevel() default {1};

}
