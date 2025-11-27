package com.vigilancia.observers;

import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IObservador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Observador que registra todos los eventos en un archivo de log.
 */
public class RegistradorHistorico implements IObservador {
    private final String rutaArchivo;

    public RegistradorHistorico(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    @Override
    public void actualizar(EventoEconomico evento) {
        try (FileWriter fw = new FileWriter(rutaArchivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(evento.toString());
        } catch (IOException e) {
            System.err.println("Error escribiendo en histórico: " + e.getMessage());
        }
    }
}