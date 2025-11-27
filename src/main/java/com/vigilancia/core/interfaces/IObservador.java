package com.vigilancia.core.interfaces;

/**
 * Interfaz para los observadores que reaccionan a cambios económicos.
 */
public interface IObservador {
    void actualizar(EventoEconomico evento);
}