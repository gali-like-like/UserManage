package org.example.inteceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.context.BaseContext;
import org.example.entity.Result;
import org.example.message.CommonMessage;
import org.example.pojo.AccessLog;
import org.example.tool.JwtTool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Objects;

@Component
@Slf4j
public class LoginInteceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AccessLog accessLog = new AccessLog();
        accessLog.setBegin(System.currentTimeMillis());
        String token = request.getHeader("token");
        String ip = request.getRemoteAddr();
        String realIp = request.getHeader("X-For-warded");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        accessLog.setMethod(method);
        accessLog.setUri(uri);
        log.info("url:{}",request.getRequestURL());
        accessLog.setMethod(method);
        if (Objects.isNull(ip) && Objects.nonNull(realIp)) {
            accessLog.setIp(realIp);
        }
        else {
            accessLog.setIp(ip);
        }
        if (handler instanceof HandlerMethod && ((HandlerMethod) handler).getMethod().getName() != "openapiJson") {
            HandlerMethod methodHandle = (HandlerMethod) handler;
            accessLog.setHandlerMethod(methodHandle.getMethod().getName());
        }
        else if(uri.contains("/main/login/") || uri.contains("/main/user/regedit") || uri.contains("/code")) {
            return true;
        }
        else {
            //放行静态资源
            accessLog.setMethod("");
            log.info(accessLog.toString());
            return true;
        }

        try {
            HashMap<String,Object> people = JwtTool.parseJwt(token);
            BaseContext.setCurrentId(people.get("username").toString());
            return true;
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            ObjectMapper mapper = new ObjectMapper();
            String jsonStr = mapper.writeValueAsString(Result.error(CommonMessage.JWT_ERROR));
            response.getWriter().write(jsonStr);
            return false;
        }
    }
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("username:{}",BaseContext.getCurrentId());
        BaseContext.removeCurrentId();
    }
}
