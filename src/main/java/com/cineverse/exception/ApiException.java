package com.cineverse.exception;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada pelas camadas de serviço quando uma regra de negócio é violada.
 * Carrega o status HTTP que deve ser devolvido ao cliente.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public static ApiException badRequest(String mensagem) {
        return new ApiException(HttpStatus.BAD_REQUEST, mensagem);
    }

    public static ApiException notFound(String mensagem) {
        return new ApiException(HttpStatus.NOT_FOUND, mensagem);
    }

    public static ApiException conflict(String mensagem) {
        return new ApiException(HttpStatus.CONFLICT, mensagem);
    }

    public static ApiException unauthorized(String mensagem) {
        return new ApiException(HttpStatus.UNAUTHORIZED, mensagem);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
