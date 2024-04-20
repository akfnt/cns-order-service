package com.akfnt.cnsorderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/actuator/**").permitAll()    // 임시적으로 스프링 부트 액추에이터 엔드포인트에 인증되지 않은 액세스를 허용한다
                        .anyExchange().authenticated())                         // 모든 요청은 인증이 필요하다
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))                        // JWT 에 기반한 기본 설정을 사용해 OAuth2 리소스 서버 지원을 활성화한다(JWT 인증)
                .requestCache(requestCacheSpec -> requestCacheSpec
                        .requestCache(NoOpServerRequestCache.getInstance()))    // 각 요청은 액세스 토큰을 가지고 있어야 하기 때문에 요청 간 세션 캐시를 유지할 필요가 없다. 상태를 갖지 않아야 한다.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)                     // 인증 전략이 상태를 갖지 않고, 브라우저 기반 클라이언트가 관여되지 않기 때문에 CSRF 보호는 비활성화해도 된다.
                .build();
    }
}
