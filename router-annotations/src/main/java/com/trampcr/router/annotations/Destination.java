package com.trampcr.router.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Destination {
    /**
     * 当前页面 URL, 不能为空
     * @return
     */
    String url();

    /**
     * 当前页面描述, 不能为空
     * @return
     */
    String description();
}
