package org.practica.controller;

import org.practica.config.JwtTokenProvider;
import org.practica.controller.dto.LoginRequest;
import org.practica.controller.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ControllerLogin {
    private final JwtTokenProvider jwtTokenProvider;

    public ControllerLogin(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping("/login")
    public String mostrarVistaLogin() {
        return "login";
    }

    @PostMapping("/login-chat-control")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        String token = jwtTokenProvider.crearToken(loginRequest.getUsuario());

        return ResponseEntity.ok(new LoginResponse(token));

    }
}
