package org.practica.controller;

import org.practica.config.JwtTokenProvider;
import org.practica.controller.dto.LoginRequest;
import org.practica.controller.dto.LoginResponse;
import org.practica.controller.dto.MensajeDto;
import org.practica.controller.dto.MensajePrivado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/grupo")
    @PreAuthorize("hasRole('ROLE_USER')")
    public MensajeDto enviarMensaje(Principal principal, @Payload MensajeDto mensaje) {
        // Sobre-escribo el nombre que llega con el del usuario autenticado, para mayor seguridad

        if(principal == null){
            logger.info("principal es null");
            throw new IllegalArgumentException("principal es null");
        }


        logger.info("entro al topic " + mensaje);
        logger.info("principal " + principal.getName());
        mensaje.setNombre(principal.getName());
        return mensaje;
    }

    @GetMapping("/login-chat")
    public String loginChat() {
        return "chat";
    }

    @MessageMapping("/chat.private")
    public void procesarMensaje(MensajePrivado mensaje, Principal principal) {
        Map<String, String> payload = new HashMap<>();
        payload.put("destinatario", mensaje.getDestinatario());
        payload.put("mensaje", mensaje.getContenido());
        payload.put("remitente", principal.getName());
        messagingTemplate.convertAndSendToUser(
                mensaje.getDestinatario(),
                "/queue/mensajes",
                payload
        );
    }

    @GetMapping("/chat-privado")
    public String chatPrivado(){
        return "chat-privado";
    }

}
