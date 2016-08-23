package com.icfolson.sling.translate.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Translation {

    String value() default "";

    String comment() default "";

    DefaultTranslation[] defaults() default {};

}
