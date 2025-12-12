package chatchatback.config;

import chatchatback.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *  2.注册拦截器
 * */

@Configuration // 配置类 里面有@Component
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor) // 拦截器
                .addPathPatterns("/ai/**") // 拦截所有请求   如果是/* 只能拦截一级路由，如/emps/{id}就不行
                .addPathPatterns("/sentence-breaking/**")
                .addPathPatterns("/users/grade")   // UserController用户修改年级字段
                .addPathPatterns("/poemsByGrade")  // PoemController
                .addPathPatterns("/dify/**") //DifyController
                .addPathPatterns("/sse/**") // 新增：保护 SSE 接口
                .addPathPatterns("/questions/**")
                .excludePathPatterns("/login"); // 放行登录接口
    }

    // 配置跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("token", "content-type", "accept", "authorization", "x-requested-with")
                .exposedHeaders("token");
    }

    // 日期格式化
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        registrar.registerFormatters(registry);
    }
}
