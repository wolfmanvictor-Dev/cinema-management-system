package com.cineverse.dto;

/** Formato padrão de resposta de erro da API: { "erro": "mensagem" }. */
public class ApiError {

    private String erro;

    public ApiError(String erro) {
        this.erro = erro;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }
}
