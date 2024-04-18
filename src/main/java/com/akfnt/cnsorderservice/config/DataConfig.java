package com.akfnt.cnsorderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

@Configuration
@EnableR2dbcAuditing
public class DataConfig {
    @Bean
    ReactiveAuditorAware<String> auditorAware() {
        return () -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)        // 인증되지 않은 사용자가 데이터를 변경하려는 경우를 처리하기 위한 부분
                .map(Authentication::getName);                  // Authentication 객체로부터 현재 인증된 사용자의 유저명을 추출한다
    }
}
