package org.fengpp.springcloud.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SPIUse {
    String name() default "";
}
