package com.cineverse.config;

import com.cineverse.security.AuthInterceptor;
import com.cineverse.service.AuthService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthService authService;

    public WebConfig(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Libera o acesso à API a partir do front-end (aberto localmente, ex: file:// ou live-server)
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(authService))
                .addPathPatterns(
                        "/api/filmes", "/api/filmes/**",
                        "/api/salas", "/api/salas/**",
                        "/api/sessoes", "/api/sessoes/**",
                        "/api/assentos", "/api/assentos/**",
                        "/api/compras",
                        "/api/admin/dashboard"
                );
    }
}
