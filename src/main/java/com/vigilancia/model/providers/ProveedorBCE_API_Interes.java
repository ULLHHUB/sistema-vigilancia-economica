package com.vigilancia.model.providers;

import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IProveedorDatos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;

/**
 * Proveedor de datos del BCE usando la API SDMX oficial.
 * Obtiene el tipo de interés de las operaciones principales de financiación (MRO).
 * URL: https://data-api.ecb.europa.eu/service/data/FM/D.U2.EUR.4F.KR.MRR_RT.LEV
 */
public class ProveedorBCE_API_Interes implements IProveedorDatos {
    // Series Key para "Main refinancing operations - Minimum bid rate/fixed rate (date of changes) - Level"
    private static final String API_URL = "https://data-api.ecb.europa.eu/service/data/FM/D.U2.EUR.4F.KR.MRR_RT.LEV?format=csvdata&lastNObservations=1";

    @Override
    public EventoEconomico obtenerDatos() {
        double tasa = 0.0;
        try {
            URL url = new URI(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String headerLine = reader.readLine(); // Leer encabezados
                String dataLine = reader.readLine();   // Leer datos
                reader.close();

                if (headerLine != null && dataLine != null) {
                    String[] headers = headerLine.split(",");
                    String[] values = dataLine.split(",");

                    // Buscar el índice de la columna OBS_VALUE
                    int obsValueIndex = -1;
                    for (int i = 0; i < headers.length; i++) {
                        if ("OBS_VALUE".equals(headers[i])) {
                            obsValueIndex = i;
                            break;
                        }
                    }

                    if (obsValueIndex != -1 && obsValueIndex < values.length) {
                        tasa = Double.parseDouble(values[obsValueIndex]);
                    } else {
                        System.err.println("API BCE SDMX: No se encontró la columna OBS_VALUE");
                    }
                }
            } else {
                System.err.println("Error API BCE SDMX: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Excepción API BCE SDMX: " + e.getMessage());
        }

        return new EventoEconomico("Tipo Interés BCE (Oficial)", tasa, "%", LocalDateTime.now());
    }

    @Override
    public String getNombreIdentificador() {
        return "BCE API (SDMX)";
    }
}