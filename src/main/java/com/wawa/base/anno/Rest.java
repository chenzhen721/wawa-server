package com.wawa.base.anno;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
@GroovyASTTransformationClass("com.wawa.base.anno.RestStaticCompileProcessor")
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