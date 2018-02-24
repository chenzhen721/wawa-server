package com.wawa.web.support;

import java.lang.reflect.Method;

import static com.wawa.web.support.ControllerSupport7.staticRequestArg;
import static com.wawa.web.support.ControllerSupport7.staticResponseArg;
import static com.wawa.web.support.ControllerSupport7.staticTwoArg;
import static com.wawa.web.support.ControllerSupport7.staticZeroArg;

/**
 * TODO Comment here.
 * date: 13-6-9 下午3:53
 *
 * @author: yangyang.cong@ttpod.com
 */
public class FreemarkerSupport7 extends FreemarkerSupport {

    protected MethodExec zeroArg(Method method,Object self){
        return staticZeroArg(method,self);
    }
    protected MethodExec twoArg(Method method,Object self){
        return staticTwoArg(method, self);
    }
    protected MethodExec requestArg(Method method,Object self){
        return staticRequestArg(method, self);
    }
    protected MethodExec responseArg(Method method,Object self){
        return staticResponseArg(method, self);
    }
}
