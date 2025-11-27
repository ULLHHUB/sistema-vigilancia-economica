package com.vigilancia.core.interfaces;

/**
 * Interfaz para el sujeto observable.
 */
public interface ISujetoObservable {
    void suscribir(IObservador observador);
    void desuscribir(IObservador observador);
    void notificarObservadores(EventoEconomico evento);
}