package com.vigilancia.model.providers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IProveedorDatos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Proveedor de Forex en tiempo real usando Binance como fuente.
 * Calcula cruces de monedas (Cross Rates) basándose en pares con USDT y BTC.
 * Reemplaza al proveedor XML del BCE para ofrecer datos en tiempo real.
 */
public class ProveedorForexBinance implements IProveedorDatos {
    private final String monedaDestino; // "USD", "GBP", "JPY"
    private static final String API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";

    public ProveedorForexBinance(String monedaDestino) {
        this.monedaDestino = monedaDestino;
    }

    @Override
    public EventoEconomico obtenerDatos() {
        double valor = 0.0;
        try {
            if ("USD".equals(monedaDestino)) {
                // Directo: EUR -> USDT (~USD)
                valor = getPrice("EURUSDT");
            } else if ("GBP".equals(monedaDestino)) {
                // Cruzado: (EUR/USDT) / (GBP/USDT) = EUR/GBP
                double eurUsd = getPrice("EURUSDT");
                double gbpUsd = getPrice("GBPUSDT");
                if (gbpUsd != 0) valor = eurUsd / gbpUsd;
            } else if ("JPY".equals(monedaDestino)) {
                // Complejo: (EUR/USDT) * (USDT/JPY)
                // Donde (USDT/JPY) se deriva de (BTC/JPY) / (BTC/USDT)
                double eurUsd = getPrice("EURUSDT");
                double btcUsd = getPrice("BTCUSDT");
                double btcJpy = getPrice("BTCJPY");
                
                if (btcUsd != 0) {
                    double usdtJpy = btcJpy / btcUsd; // Cuántos Yenes vale 1 USDT
                    valor = eurUsd * usdtJpy;
                }
            }
        } catch (Exception e) {
            System.err.println("Error Forex Binance (" + monedaDestino + "): " + e.getMessage());
        }
        
        return new EventoEconomico("Forex (RealTime): EUR/" + monedaDestino, valor, monedaDestino, LocalDateTime.now());
    }

    private double getPrice(String symbol) throws Exception {
        URL url = new URI(API_URL + symbol).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);

        if (conn.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();
            
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            return json.get("price").getAsDouble();
        }
        return 0.0;
    }

    @Override
    public String getNombreIdentificador() {
        return "Binance Forex: EUR/" + monedaDestino;
    }
}
