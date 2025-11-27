package com.vigilancia.core.interfaces;

import java.time.LocalDateTime;

/**
 * Representa un cambio en un valor económico.
 * Inmutable para garantizar thread-safety.
 */
public record EventoEconomico(String nombreActivo, double valor, String unidad, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return String.format("[%s] %s: %.4f %s", timestamp, nombreActivo, valor, unidad);
    }
}