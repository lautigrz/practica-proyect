package org.practica.config;

import org.practica.controller.dto.EstadoConexion;
import org.practica.controller.dto.EstadoUsuarioDTO;
import org.practica.dominio.SessionService;
import org.practica.dominio.SessionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SessionServiceImpl sessionService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @EventListener
    public void manejarConexion(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();

        if(principal != null) {
            String sessionId = accessor.getSessionId();
            String username = principal.getName();

            String sessionAnterior = sessionService.obtenerSessionPorUsername(username);
            sessionService.agregarSesion(username,sessionId);

            logger.info("Session anterior:" + sessionAnterior);
            if(sessionAnterior != null && !sessionAnterior.equals(sessionId)) {
                messagingTemplate.convertAndSendToUser(
                        username,
                        "/queue/desconectar",
                        "Desconectado: tu sesión fue reemplazada por otra"
                );
                logger.info("Usuario duplicado se debera cerrar conexion");
            }

            logger.info("Usuario conectado: " + username + " (session: " + sessionId + ")");

        }

        }

    @EventListener
    public void manejarDesconexion(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getSessionId();
        sessionService.quitarSesion(sessionId);
        logger.info("Sesión desconectada: " + sessionId);
        EstadoUsuarioDTO estadoUsuarioDTO = EstadoUsuarioDTO.builder()
                .username(Objects.requireNonNull(sha.getUser()).getName())
                .estado(EstadoConexion.DESCONECTADO)
                .build();


        messagingTemplate.convertAndSend("/topic/grupo", estadoUsuarioDTO);
    }



}


