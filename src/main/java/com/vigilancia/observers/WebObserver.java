package com.vigilancia.observers;

import com.vigilancia.core.interfaces.EventoEconomico;
import com.vigilancia.core.interfaces.IObservador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Observador Web que envía los eventos a través de WebSockets.
 * Reemplaza a la Vista Swing en la arquitectura Web.
 */
@Component
public class WebObserver implements IObservador {

    private static final Logger logger = LoggerFactory.getLogger(WebObserver.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebObserver(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void actualizar(EventoEconomico evento) {
        // Enviar el evento al tópico /topic/precios
        logger.debug("Enviando evento WebSocket: {} - {}", evento.nombreActivo(), evento.valor());
        messagingTemplate.convertAndSend("/topic/precios", evento);
    }
}
