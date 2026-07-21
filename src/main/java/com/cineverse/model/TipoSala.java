package com.cineverse.model;

/**
 * Tipos de sala de cinema disponíveis.
 * Cada tipo tem um layout padrão sugerido (fileiras x assentos por fileira),
 * usado apenas como sugestão ao cadastrar uma sala nova.
 */
public enum TipoSala {
    PEQUENA("Pequena", 4, 6),
    MEDIA("Média", 6, 8),
    GRANDE("Grande", 8, 12),
    VIP("VIP", 4, 5);

    private final String label;
    private final int fileirasSugeridas;
    private final int assentosPorFileiraSugerido;

    TipoSala(String label, int fileirasSugeridas, int assentosPorFileiraSugerido) {
        this.label = label;
        this.fileirasSugeridas = fileirasSugeridas;
        this.assentosPorFileiraSugerido = assentosPorFileiraSugerido;
    }

    public String getLabel() {
        return label;
    }

    public int getFileirasSugeridas() {
        return fileirasSugeridas;
    }

    public int getAssentosPorFileiraSugerido() {
        return assentosPorFileiraSugerido;
    }
}
