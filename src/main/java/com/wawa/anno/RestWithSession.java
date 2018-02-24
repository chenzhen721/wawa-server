package com.wawa.anno;

import com.wawa.web.spring.Interceptors;
import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * date: 13-6-4 下午5:25
 *
 * @author: yangyang.cong@ttpod.com
 */
@java.lang.annotation.Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("com.wawa.anno.RestStaticCompileProcessor")
public @interface RestWithSession {
    Class[] value = {Controller.class, Interceptors.class};
}
