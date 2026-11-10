package com.flood.config;

import com.flood.security.JwtAuthenticationFilter;
import com.flood.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * Spring Security 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF(JWT无状态认证不需要)
            .csrf(csrf -> csrf.disable())
            // 启用CORS
            .cors(Customizer.withDefaults())
            // 无状态会话管理
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 异常处理: 未认证返回401, 无权限返回403, 均以JSON返回
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    writeJson(response, 401, "未登录或登录已过期"))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeJson(response, 403, "没有权限执行此操作"))
            )
            // 请求授权配置
            .authorizeHttpRequests(auth -> auth
                // 登录、注册接口放行
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                // 错误页面放行
                .requestMatchers("/error").permitAll()
                // 图标放行
                .requestMatchers("/favicon.ico").permitAll()
                // K: WebSocket 握手放行
                .requestMatchers("/ws/**").permitAll()
                // B: 接口级角色权限限制
                .requestMatchers("/api/user/**").hasRole("ADMIN")
                .requestMatchers("/api/log/**").hasRole("ADMIN")
                .requestMatchers("/api/dispatch/generate").hasAnyRole("ADMIN", "COMMANDER")
                .requestMatchers("/api/dispatch/status/**").hasAnyRole("ADMIN", "COMMANDER")
                // 其余请求需要认证
                .anyRequest().authenticated()
            )
            // 在用户名密码认证过滤器前添加JWT过滤器
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    /**
     * 输出统一JSON错误响应
     */
    private void writeJson(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":" + code + ",\"message\":\"" + message + "\",\"data\":null}");
    }
}