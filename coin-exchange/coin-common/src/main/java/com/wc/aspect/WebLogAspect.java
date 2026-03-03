package com.wc.aspect;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.wc.model.WebLog;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;

@Component
@Aspect
@Order(1)
@Slf4j
public class WebLogAspect {

    @Pointcut("execution(* com.wc..controller..*.*(..))") // controller 包里面所有类，类里面的所有方法 都有该切面
    public void   WebLog(){
    }

    @Around("WebLog()")
    public Object recordWebLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        WebLog webLog = new WebLog() ;
        long start = System.currentTimeMillis();
        result= joinPoint.proceed(joinPoint.getArgs());
        long end = System.currentTimeMillis();
        webLog.setSpendTime((int)(end-start)/1000);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        Authentication authorization =  SecurityContextHolder.getContext().getAuthentication();
        String url = request.getRequestURL().toString();
        webLog.setUri(request.getRequestURI());
        webLog.setUrl(url);
        webLog.setBasePath(StrUtil.removeSuffix(url,URLUtil.url(url).getPath()));
        webLog.setUsername(authorization == null ? "anonymous":authorization.getPrincipal().toString());
        webLog.setIp(request.getRemoteAddr()); //todo
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = joinPoint.getTarget().getClass().getName();
        ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
        webLog.setDescription(apiOperation == null ? "no desc" : apiOperation.value());
        webLog.setMethod(name+"."+method.getName());
        webLog.setParameter(getMethodParameter(method, joinPoint.getArgs() ));
        log.info(JSON.toJSONString(webLog,true));
        webLog.setResult(result);
        return  result;

    }

    private Object getMethodParameter(Method method,Object[] args) {
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        LocalVariableTableParameterNameDiscoverer localVariableTableParameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = localVariableTableParameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < parameterNames.length; i++) {
            if(parameterNames[i].equals("password") || parameterNames[i].equals("file")){
                objectObjectHashMap.put(parameterNames[i],"受限的支持类型") ;
            }else{
                objectObjectHashMap.put(parameterNames[i],args[i]) ;
            }
        }
        return  objectObjectHashMap;
    }

}
