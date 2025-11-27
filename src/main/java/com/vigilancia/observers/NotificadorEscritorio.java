package com.vigilancia.observers;

import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IObservador;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Observador que muestra notificaciones en la bandeja del sistema.
 * Implementa lógica para evitar spam de notificaciones.
 */
public class NotificadorEscritorio implements IObservador {
    private TrayIcon trayIcon;
    private final Map<String, Double> ultimosValoresNotificados = new HashMap<>();
    private final Map<String, String> ultimasUnidades = new HashMap<>();
    private static final double UMBRAL_CAMBIO_SIGNIFICATIVO = 0.01; // 1% de cambio

    public NotificadorEscritorio() {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); // Icono dummy
            
            trayIcon = new TrayIcon(image, "Vigilancia Económica");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Monitor de Precios Activo");
            
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("No se pudo agregar al system tray");
            }
        }
    }

    @Override
    public void actualizar(EventoEconomico evento) {
        if (trayIcon == null) return;

        String key = evento.nombreActivo();
        double nuevoValor = evento.valor();
        String nuevaUnidad = evento.unidad();
        
        // Verificar si es la primera vez o si el cambio es significativo
        if (!ultimosValoresNotificados.containsKey(key)) {
            ultimosValoresNotificados.put(key, nuevoValor);
            ultimasUnidades.put(key, nuevaUnidad);
            // Opcional: No notificar la primera carga para no saturar al inicio
            // mostrarNotificacion("Nuevo Activo Detectado", evento); 
            return; 
        }

        // Verificar si cambió la moneda (para evitar falsas alarmas por cambio de divisa)
        String unidadAnterior = ultimasUnidades.getOrDefault(key, "");
        if (!unidadAnterior.equals(nuevaUnidad)) {
            // Actualizamos la referencia sin notificar
            ultimosValoresNotificados.put(key, nuevoValor);
            ultimasUnidades.put(key, nuevaUnidad);
            return;
        }

        double valorAnterior = ultimosValoresNotificados.get(key);
        double diferencia = Math.abs(nuevoValor - valorAnterior);
        double porcentajeCambio = diferencia / valorAnterior;

        if (porcentajeCambio >= UMBRAL_CAMBIO_SIGNIFICATIVO) {
            ultimosValoresNotificados.put(key, nuevoValor);
            String tendencia = nuevoValor > valorAnterior ? "📈 Subió" : "📉 Bajó";
            String mensaje = String.format("%s: %s a %.4f %s (%.2f%%)", 
                evento.nombreActivo(), tendencia, nuevoValor, evento.unidad(), porcentajeCambio * 100);
            
            mostrarNotificacion("Alerta de Mercado", mensaje);
        }
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        trayIcon.displayMessage(titulo, mensaje, TrayIcon.MessageType.INFO);
    }
}