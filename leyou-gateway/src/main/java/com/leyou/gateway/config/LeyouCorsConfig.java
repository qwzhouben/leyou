package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @DESC: 解决跨域
 * @author: zhouben
 * @date: 2020/3/29 0029 17:12
 */
@Configuration
public class LeyouCorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //1)允许的域，不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");
        //2)是否发送cookie信息
        config.setAllowCredentials(true);
        //3)允许的方法
        config.addAllowedMethod("*");
        //4)允许的头信息
        config.addAllowedHeader("*");

        //2. 添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter
        return new CorsFilter(configurationSource);
    }
}
