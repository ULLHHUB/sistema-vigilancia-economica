# Sistema de Vigilancia Económica Autónoma 📈

Este proyecto es una aplicación de monitoreo financiero en tiempo real desarrollada en **Java (Spring Boot)** que implementa el **Patrón de Diseño Observer** para notificar cambios en los mercados financieros de manera eficiente y desacoplada.

## 🚀 Características Principales

*   **Monitoreo en Tiempo Real:** Seguimiento de precios de Criptomonedas (Bitcoin, Ethereum, etc.) y divisas Forex (EUR/USD, EUR/GBP).
*   **Arquitectura Reactiva:** Uso de WebSockets (STOMP) para enviar actualizaciones instantáneas al frontend sin necesidad de recargar la página.
*   **Visualización Dinámica:** Gráficos interactivos con **Chart.js** que muestran la evolución de los precios en vivo.
*   **Gestión de Cartera:** Calculadora integrada para estimar el valor de tus tenencias de criptomonedas en tiempo real.
*   **Alertas de Volatilidad:** Sistema de notificaciones (Toasts) que avisa cuando un activo sufre cambios bruscos de precio (>0.5%).
*   **Soporte Multi-divisa:** Capacidad de cambiar la moneda base de cotización entre Dólar (USD) y Euro (EUR).

## 🛠️ Tecnologías Utilizadas

### Backend
*   **Java 17**
*   **Spring Boot 3.2.0** (Web, WebSocket)
*   **Patrón Observer:** Implementación manual (`ISujetoObservable`, `IObservador`) para la gestión de eventos.
*   **Jsoup & Gson:** Para el consumo y parseo de APIs externas (Binance, BCE).

### Frontend
*   **HTML5 / CSS3**
*   **Bootstrap 5:** Diseño responsivo y moderno.
*   **Chart.js:** Gráficos de línea en tiempo real.
*   **SockJS & Stomp.js:** Cliente WebSocket para comunicación bidireccional.

## 🏗️ Arquitectura del Proyecto

El sistema sigue una arquitectura limpia basada en el patrón **Observer**:

1.  **Sujeto (MonitorEconomico):** El núcleo que orquesta la obtención de datos. Mantiene una lista de observadores y les notifica cuando hay nuevos datos.
2.  **Proveedores (IProveedorDatos):** Clases encargadas de conectar con fuentes externas (API de Binance, API del Banco Central Europeo).
3.  **Observadores (IObservador):**
    *   `WebObserver`: Puente que reenvía los eventos al navegador vía WebSockets.
    *   `RegistradorHistorico`: Guarda un log de los precios en un archivo de texto local.
    *   `NotificadorEscritorio`: (Opcional) Envía alertas al sistema operativo.

## 📦 Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/ULLHHUB/sistema-vigilancia-economica.git
    cd sistema-vigilancia-economica
    ```

2.  **Compilar y Ejecutar:**
    Asegúrate de tener Maven y JDK 17 instalados.
    ```bash
    mvn spring-boot:run
    ```

3.  **Acceder a la Aplicación:**
    Abre tu navegador y visita: `http://localhost:8080`

4.  **Uso:**
    *   Haz clic en **"Iniciar"** para comenzar a recibir datos.
    *   Selecciona una fila de la tabla para ver el gráfico detallado de ese activo.
    *   Ingresa la cantidad de criptomonedas que posees en la columna "Tenencia" para ver el valor de tu cartera.
    *   Cambia entre USD y EUR usando el selector superior.

## 👥 Autores

Proyecto desarrollado como práctica de Diseño de Arquitectura de Software.
*   **Integrante 1:** Desarrollo de Proveedores de Datos y Lógica de Negocio.
*   **Integrante 2:** Desarrollo de Interfaz Web y Controlador REST.
