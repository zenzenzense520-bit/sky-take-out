package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.interceptor.JwtTokenUserInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;
import java.util.List;

/**
 * Spring MVC 配置
 * 1. 注册自定义 JSON 消息转换器
 * 2. 注册管理端与用户端 JWT 拦截器
 * 3. 注册 Knife4j 与 本地上传图片 静态资源映射
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
        converters.add(0, converter);
    }

    /** 静态资源映射：Knife4j 接口文档 + 本地上传文件目录 */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        String uploadDir = new File("uploads").getAbsolutePath();
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadDir + File.separator);
    }

    /** 注册拦截器并配置放行路径 */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/employee/login",
                        "/admin/common/**",
                        "/uploads/**",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/error"
                );

        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/user/login",
                        "/user/shop/**",
                        "/user/dish/**",
                        "/user/setmeal/**",
                        "/uploads/**",
                        "/error"
                );
    }
}
