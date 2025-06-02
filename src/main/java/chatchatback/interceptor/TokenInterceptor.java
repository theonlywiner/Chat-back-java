package chatchatback.interceptor;

import chatchatback.constant.JwtClaimsConstant;
import chatchatback.properties.JwtProperties;
import chatchatback.utils.CurrentHolder;
import chatchatback.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final JwtProperties JwtProperties;

    // preHandle 在目标资源方法运行之前运行，返回true表示放行，false表示拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle方法被调用...");

        // 1. 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            log.info("header: {} = {}", headerName, request.getHeader(headerName));
//        }

        //3.拿到token
        String token = request.getHeader("token");
        log.info("拿到token：{}",token);

        //4.判断token是否为空，为空，返回401
        if (token == null) {
            log.info("请求头中没有token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"token缺失\"}");
            return false;
        }

        //5.解析token，如果解析失败，返回401
        try {
            Claims claims = JwtUtils.parseJwt(JwtProperties.getAdminSecretKey(), token);
            Integer employeeId = (Integer) claims.get(JwtClaimsConstant.USER_ID);
            log.info("当前登录用户userid为：{}", employeeId);
            CurrentHolder.setCurrentId(employeeId);
        } catch (Exception e) {
            log.info("解析令牌失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"token无效或已过期\"}");
            return false;
        }

        //6.解析成功，放行
        log.info("令牌解析成功,放行");
        return true;
    }

    // postHandle 在目标资源方法运行之后运行
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle方法被调用...");

        // 移除当前线程绑定的id
        CurrentHolder.remove();
    }

    // afterCompletion 在渲染视图之后运行
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("afterCompletion方法被调用");
//    }
}