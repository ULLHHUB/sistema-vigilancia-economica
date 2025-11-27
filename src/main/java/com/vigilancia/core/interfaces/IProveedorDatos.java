package com.vigilancia.core.interfaces;

/**
 * Estrategia para obtener datos de diferentes fuentes (API, Web Scraping, Mock).
 */
public interface IProveedorDatos {
    EventoEconomico obtenerDatos();
    String getNombreIdentificador();
}