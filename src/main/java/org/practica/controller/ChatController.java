package org.practica.controller;

import org.practica.config.JwtTokenProvider;

import org.practica.controller.dto.EstadoUsuarioDTO;
import org.practica.controller.dto.LoginRequest;
import org.practica.controller.dto.LoginResponse;
import org.practica.controller.dto.MensajeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;


@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public ChatController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/grupo")
    @PreAuthorize("hasRole('ROLE_USER')")
    public MensajeDto enviarMensaje(Principal principal, @Payload MensajeDto mensaje) {

        if(principal == null){
            logger.info("principal es null");
            throw new IllegalArgumentException("principal es null");
        }


        logger.info("entro al topic " + mensaje);
        logger.info("principal " + principal.getName());
        mensaje.setNombre(principal.getName());
        return mensaje;
    }
    @GetMapping("/chat")
    public String loginChat() {
        return "chat";
    }
    @MessageMapping("/chat.register")
    @SendTo("/topic/grupo")
    public EstadoUsuarioDTO register(@Payload EstadoUsuarioDTO estado) {

        return estado;
    }

    @GetMapping("/probar-desconexion")
    public String probarDesconexion() {
        messagingTemplate.convertAndSendToUser("messi", "/queue/desconectar", "Mensaje de prueba");

        return "chat";
    }

}
