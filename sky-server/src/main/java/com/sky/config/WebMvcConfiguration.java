package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * Spring MVC 配置
 * 1. 注册自定义 JSON 消息转换器
 * 2. 注册管理端与用户端 JWT 拦截器
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    private final JwtTokenAdminInterceptor adminInterceptor;
    private final JwtTokenUserInterceptor userInterceptor;

    public WebMvcConfiguration(JwtTokenAdminInterceptor adminInterceptor,
                               JwtTokenUserInterceptor userInterceptor) {
        this.adminInterceptor = adminInterceptor;
        this.userInterceptor = userInterceptor;
    }

    /** 扩展消息转换器，加入自定义 Jackson 转换器 */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new JacksonObjectMapper());
        // 优先使用自定义转换器，保证 Long 转 String 等规则生效
        converters.add(0, converter);
    }

    /** 注册拦截器并配置放行路径 */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/employee/login",
                        "/admin/common/**",
                        "/error"
                );

        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/user/login",
                        "/user/shop/**",
                        "/user/dish/**",
                        "/user/setmeal/**",
                        "/error"
                );
    }
}
