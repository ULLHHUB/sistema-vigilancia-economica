package com.vigilancia.controller;

import com.vigilancia.core.interfaces.IObservador;
import com.vigilancia.model.MonitorEconomico;
import com.vigilancia.model.providers.ProveedorBCE_API_Interes;
import com.vigilancia.model.providers.ProveedorCriptoAPI;
import com.vigilancia.model.providers.ProveedorForexBinance;
import com.vigilancia.observers.NotificadorEscritorio;
import com.vigilancia.observers.RegistradorHistorico;
import com.vigilancia.observers.WebObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/control")
public class VigilanciaRestController {

    private static final Logger logger = LoggerFactory.getLogger(VigilanciaRestController.class);
    private final WebObserver webObserver;
    private MonitorEconomico modelo;
    private final List<ProveedorCriptoAPI> proveedoresCripto = new ArrayList<>();

    public VigilanciaRestController(WebObserver webObserver) {
        this.webObserver = webObserver;
    }

    @PostConstruct
    public void init() {
        this.modelo = new MonitorEconomico();
        configurarSistema();
    }

    private void configurarSistema() {
        logger.info("Configurando sistema de vigilancia...");
        // 1. Agregar Proveedores
        modelo.agregarProveedor(new ProveedorBCE_API_Interes());
        
        modelo.agregarProveedor(new ProveedorForexBinance("USD"));
        modelo.agregarProveedor(new ProveedorForexBinance("GBP"));
        modelo.agregarProveedor(new ProveedorForexBinance("JPY"));

        agregarCripto("BTC", "Bitcoin (BTC)");
        agregarCripto("ETH", "Ethereum (ETH)");
        agregarCripto("ADA", "Cardano (ADA)");
        agregarCripto("DOT", "Polkadot (DOT)");
        agregarCripto("SOL", "Solana (SOL)");
        
        // Nuevos activos (Commodities & More)
        agregarCripto("LTC", "Litecoin (LTC)"); // Reemplazo PAXG por LTC para mejor soporte EUR
        agregarCripto("XRP", "Ripple (XRP)");

        // 2. Agregar Observadores
        modelo.suscribir(webObserver); // WebSocket
        modelo.suscribir(new RegistradorHistorico("historial_economico.txt"));
        
        // Intentar agregar notificador de escritorio si es soportado (opcional en servidor)
        try {
            modelo.suscribir(new NotificadorEscritorio());
        } catch (Exception e) {
            logger.warn("Notificador de escritorio no disponible: {}", e.getMessage());
        }
    }

    private void agregarCripto(String baseAsset, String nombre) {
        ProveedorCriptoAPI p = new ProveedorCriptoAPI(baseAsset, nombre);
        proveedoresCripto.add(p);
        modelo.agregarProveedor(p);
    }

    @PostMapping("/iniciar")
    public String iniciar() {
        modelo.iniciarMonitoreo();
        return "Sistema Iniciado";
    }

    @PostMapping("/detener")
    public String detener() {
        modelo.detenerMonitoreo();
        return "Sistema Detenido";
    }

    @PostMapping("/moneda")
    public String cambiarMoneda(@RequestParam("moneda") String moneda) {
        modelo.detenerMonitoreo(); // Detener para cambiar configuración
        for (ProveedorCriptoAPI p : proveedoresCripto) {
            p.setMonedaCotizacion(moneda);
        }
        // No reiniciamos automáticamente. El usuario debe darle a Iniciar.
        return "Moneda cambiada a " + moneda + ". Sistema detenido.";
    }
}
