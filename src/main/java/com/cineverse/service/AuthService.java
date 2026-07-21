package com.cineverse.service;

import com.cineverse.dto.LoginRequest;
import com.cineverse.dto.LoginResponse;
import com.cineverse.exception.ApiException;
import com.cineverse.model.Admin;
import com.cineverse.repository.AdminRepository;
import com.cineverse.security.HashUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cuida do login do administrador e da validação dos tokens de sessão.
 * Os tokens ficam guardados em memória (Map) com uma validade de 4 horas.
 */
@Service
public class AuthService {

    private static final long VALIDADE_TOKEN_MINUTOS = 4 * 60;

    private final AdminRepository adminRepository;
    private final Map<String, TokenInfo> tokens = new ConcurrentHashMap<>();

    public AuthService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public LoginResponse login(LoginRequest request) {
        if (request == null || vazio(request.getUsuario()) || vazio(request.getSenha())) {
            throw ApiException.badRequest("Informe o login e a senha.");
        }

        Admin admin = adminRepository.buscarPorUsuario(request.getUsuario().trim())
                .orElseThrow(() -> ApiException.unauthorized("Login ou senha inválidos."));

        String hashInformado = HashUtil.sha256(request.getSenha());
        if (!hashInformado.equals(admin.getSenhaHash())) {
            throw ApiException.unauthorized("Login ou senha inválidos.");
        }

        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(admin.getNome(), Instant.now().plusSeconds(VALIDADE_TOKEN_MINUTOS * 60)));

        return new LoginResponse(token, admin.getNome());
    }

    public boolean tokenValido(String token) {
        if (token == null) return false;
        TokenInfo info = tokens.get(token);
        if (info == null) return false;

        if (Instant.now().isAfter(info.expiraEm)) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    private boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private record TokenInfo(String nomeAdmin, Instant expiraEm) {
    }
}
