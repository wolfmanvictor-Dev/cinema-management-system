package com.cineverse.security;

import com.cineverse.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Verifica se o cabeçalho "Authorization: Bearer TOKEN" contém um token
 * válido antes de deixar a requisição chegar aos endpoints administrativos
 * (criar/editar/excluir filmes, salas e sessões, ver compras, etc.).
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Requisições de "preflight" do CORS não enviam o cabeçalho de autenticação
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Leituras (GET) de filmes/salas/sessões/assentos são públicas, assim como
        // o envio de uma nova compra (POST /api/compras) — o site público precisa
        // dessas rotas sem estar logado como admin.
        String path = request.getRequestURI();
        boolean leituraPublica = "GET".equalsIgnoreCase(request.getMethod())
                && !path.startsWith("/api/compras")
                && !path.equals("/api/admin/dashboard");
        boolean envioDeCompra = "POST".equalsIgnoreCase(request.getMethod()) && path.equals("/api/compras");

        if (leituraPublica || envioDeCompra) {
            return true;
        }

        String cabecalho = request.getHeader("Authorization");
        String token = (cabecalho != null && cabecalho.startsWith("Bearer "))
                ? cabecalho.substring("Bearer ".length())
                : null;

        if (!authService.tokenValido(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"erro\":\"Sessão expirada. Faça login novamente.\"}");
            return false;
        }

        return true;
    }
}
