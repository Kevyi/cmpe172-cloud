package edu.sjsu.cmpe172.starterdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
            .addPathPatterns("/appointments/**", "/booking/**", "/slots/**", "/admin/**")
            .excludePathPatterns("/", "/login", "/register", "/confirmation", "/css/**", "/api/**", "/server");
    }
}
