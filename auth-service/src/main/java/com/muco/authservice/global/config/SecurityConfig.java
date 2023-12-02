package com.muco.authservice.global.config;

import com.muco.authservice.global.auth.jwt.JwtAccessDeniedHandler;
import com.muco.authservice.global.auth.jwt.JwtAuthenticationEntryPoint;
import com.muco.authservice.global.auth.jwt.JwtFilter;
import com.muco.authservice.global.auth.jwt.JwtProvider;
import com.muco.authservice.global.auth.oauth.OAuth2AuthenticationFailureHandler;
import com.muco.authservice.global.auth.oauth.OAuth2AuthenticationSuccessHandler;
import com.muco.authservice.global.auth.oauth.OAuth2AuthorizationRequestCookieRepository;
import com.muco.authservice.global.auth.oauth.OAuth2UserServiceImpl;
import com.muco.authservice.global.auth.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2UserServiceImpl oAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthorizationRequestCookieRepository oAuth2AuthorizationRequestCookieRepository;

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar -> ar.anyRequest().permitAll())
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .userDetailsService(userDetailsService)
                .oauth2Login(ol -> {
                    ol.authorizationEndpoint(ep -> {
                        ep.baseUri("/oauth2/authorization");
                        ep.authorizationRequestRepository(oAuth2AuthorizationRequestCookieRepository);
                    });
                    ol.redirectionEndpoint(ep -> ep.baseUri("/oauth2/callback/*"));
                    ol.userInfoEndpoint(ep -> ep.userService(oAuth2UserService));
                    ol.successHandler(oAuth2AuthenticationSuccessHandler);
                    ol.failureHandler(oAuth2AuthenticationFailureHandler);
                })
                .exceptionHandling(eh -> {
                    eh.accessDeniedHandler(jwtAccessDeniedHandler);
                    eh.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                });

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico"))
                .requestMatchers(new AntPathRequestMatcher("/css/**"))
                .requestMatchers(new AntPathRequestMatcher("/js/**"))
                .requestMatchers(new AntPathRequestMatcher("/img/**"))
                .requestMatchers(new AntPathRequestMatcher("/lib/**"));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}