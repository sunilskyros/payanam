package com.sunilskyros.payanam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SecurityFilter securityFilter;
    private final CookieAuthFilter cookieAuthFilter;

    public SecurityConfig(SecurityFilter securityFilter, CookieAuthFilter cookieAuthFilter) {
        this.securityFilter = securityFilter;
        this.cookieAuthFilter = cookieAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/collector/**").hasRole("TICKETCOLLECTOR")
                .requestMatchers("/api/tickets/verify", "/api/buses", "/api/buses/**", "/api/auth/me").permitAll()
                .requestMatchers("/api/book", "/api/tickets/my", "/api/tickets/cancel", "/api/passenger/stats", "/api/passenger/feedback").authenticated()
                .requestMatchers("/ws/**").permitAll()
                .anyRequest().permitAll()
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Unauthorized\"}");
                    } else {
                        response.sendRedirect("/index.html?error=unauthorized");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Forbidden\"}");
                    } else {
                        response.sendRedirect("/index.html?error=forbidden");
                    }
                })
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutRequestMatcher(new org.springframework.security.web.util.matcher.AntPathRequestMatcher("/logout", "GET"))
                .logoutSuccessHandler((request, response, authentication) -> {
                    jakarta.servlet.http.Cookie userCookie = new jakarta.servlet.http.Cookie("payanam_user", "");
                    userCookie.setMaxAge(0);
                    userCookie.setPath("/");
                    userCookie.setHttpOnly(true);
                    userCookie.setSecure(true);
                    response.addCookie(userCookie);
                    
                    response.sendRedirect("/index.html?logged_out=true");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .addFilterBefore(cookieAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(securityFilter, CookieAuthFilter.class);

        return http.build();
    }
}
