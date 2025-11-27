package com.vigilancia.model;

import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IObservador;
import com.vigilancia.core.interfaces.IProveedorDatos;
import com.vigilancia.core.interfaces.ISujetoObservable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase central del Modelo. Actúa como el Sujeto Observable.
 * Gestiona los proveedores de datos y notifica a los observadores.
 */
public class MonitorEconomico implements ISujetoObservable {
    private static final Logger logger = LoggerFactory.getLogger(MonitorEconomico.class);
    private final List<IObservador> observadores = new ArrayList<>();
    private final List<IProveedorDatos> proveedores = new ArrayList<>();
    private ScheduledExecutorService scheduler;
    private boolean activo = false;

    public void agregarProveedor(IProveedorDatos proveedor) {
        this.proveedores.add(proveedor);
    }

    public void iniciarMonitoreo() {
        if (!activo) {
            activo = true;
            scheduler = Executors.newScheduledThreadPool(1);
            logger.info("Iniciando ciclo de monitoreo...");
            // Ejecutar cada 5 segundos para demostración
            scheduler.scheduleAtFixedRate(this::verificarCambios, 0, 5, TimeUnit.SECONDS);
        }
    }

    public void detenerMonitoreo() {
        activo = false;
        if (scheduler != null) {
            scheduler.shutdown();
            logger.info("Monitoreo detenido.");
        }
    }

    private void verificarCambios() {
        if (!activo) return;
        
        for (IProveedorDatos proveedor : proveedores) {
            try {
                EventoEconomico evento = proveedor.obtenerDatos();
                // Si el valor es 0.0 (error en API), lo ignoramos para no ensuciar la gráfica/tabla
                if (evento.valor() != 0.0) {
                    notificarObservadores(evento);
                }
            } catch (Exception e) {
                // Loguear error pero no detener el hilo
                logger.error("Error obteniendo datos de {}: {}", proveedor.getNombreIdentificador(), e.getMessage());
            }
        }
    }

    @Override
    public void suscribir(IObservador observador) {
        observadores.add(observador);
    }

    @Override
    public void desuscribir(IObservador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(EventoEconomico evento) {
        for (IObservador observador : observadores) {
            observador.actualizar(evento);
        }
    }
}