package aibe.hosik.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * 보안 설정 클래스 
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${front-end.redirect}")
    private String frontEndRedirect;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 비활성화 (REST 전용)
            .csrf(csrf -> csrf.disable())
            // 기본 CORS 설정
            .cors(cors -> cors.configurationSource(req -> new CorsConfiguration().applyPermitDefaultValues()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // URL 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauth2/**").permitAll()           // OAuth2 콜백
                .requestMatchers("/api/data/**").authenticated()    // 인증 필요 API
                .anyRequest().permitAll()                             // 나머지 허용
            )
            // OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization/kakao")           // OAuth2 로그인 진입점
                .successHandler(oauth2SuccessHandler())               // 로그인 성공 후 처리
            )
            // JWT 인증 필터 등록
            .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT 인증 필터 빈 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * OAuth2 로그인 성공 시 JWT 발급 및 리다이렉트 핸들러
     */
    @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            var oauthToken = (org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken) authentication;
            String regId = oauthToken.getAuthorizedClientRegistrationId();
            var user = oauthToken.getPrincipal();

            String username;
            if ("github".equals(regId)) {
                username = "github_" + user.getAttribute("login");
            } else {
                username = "kakao_" + user.getAttribute("id").toString();
            }

            // JWT 생성
            String token = jwtTokenProvider.generateToken(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, "")
            );

            // 프론트엔드로 전달
            String redirectUrl = UriComponentsBuilder.fromUriString(frontEndRedirect)
                .queryParam("token", token)
                .build().toUriString();
            response.sendRedirect(redirectUrl);
        };
    }
}
