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
 * Proveedor real de Criptomonedas usando la API pública de Binance.
 * URL: https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT
 */
public class ProveedorCriptoAPI implements IProveedorDatos {
    private final String baseAsset; // e.g., "BTC"
    private String quoteAsset = "USDT"; // Default: USD Tether
    private final String nombreMostrar;
    private static final String API_URL = "https://api.binance.com/api/v3/ticker/price?symbol=";

    public ProveedorCriptoAPI(String baseAsset, String nombreMostrar) {
        this.baseAsset = baseAsset;
        this.nombreMostrar = nombreMostrar;
    }

    public void setMonedaCotizacion(String moneda) {
        // Binance usa "USDT" para USD y "EUR" para Euro
        if ("USD".equals(moneda)) {
            this.quoteAsset = "USDT";
        } else {
            this.quoteAsset = moneda;
        }
    }

    @Override
    public EventoEconomico obtenerDatos() {
        double precio = 0.0;
        String symbol = baseAsset + quoteAsset;
        try {
            URL url = new URI(API_URL + symbol).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parsear JSON: {"symbol":"BTCUSDT","price":"87432.79000000"}
                JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                if (jsonObject.has("price")) {
                    precio = jsonObject.get("price").getAsDouble();
                }
            } else {
                System.err.println("Error API Binance (" + symbol + "): " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Excepción API Cripto (" + symbol + "): " + e.getMessage());
        }

        // Devolvemos la moneda real (USD o EUR) para que la vista lo muestre
        String unidad = "USDT".equals(quoteAsset) ? "USD" : quoteAsset;
        return new EventoEconomico(nombreMostrar, precio, unidad, LocalDateTime.now());
    }

    @Override
    public String getNombreIdentificador() {
        return "Binance: " + baseAsset + "/" + quoteAsset;
    }
}