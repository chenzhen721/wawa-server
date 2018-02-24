package com.wawa.anno;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * date: 13-6-4 下午5:25
 *
 * @author: yangyang.cong@ttpod.com
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("com.wawa.anno.RestStaticCompileProcessor")
public @interface Rest {
    Class[] value = {Controller.class};
}
/**
 package com.wawa.groovy

 import groovy.transform.AnnotationCollector
 import groovy.transform.CompileStatic
 import org.springframework.stereotype.Controller
    @Controller
    @CompileStatic
    @AnnotationCollector
    public @interface Rest {

    }
**/