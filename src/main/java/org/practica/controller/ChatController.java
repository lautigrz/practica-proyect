package org.practica.controller;

import org.practica.config.JwtTokenProvider;
import org.practica.controller.dto.LoginRequest;
import org.practica.controller.dto.LoginResponse;
import org.practica.controller.dto.MensajeDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
    private final String USERNAME = "lautigrz";
    private final String PASSWORD = "123";

    public ChatController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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

    @PostMapping("/login-chat-control")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        if(USERNAME.equals(loginRequest.getUsuario()) && PASSWORD.equals(loginRequest.getPassword())) {
            String token = jwtTokenProvider.crearToken(loginRequest.getUsuario());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña invalidos");
    }
}
