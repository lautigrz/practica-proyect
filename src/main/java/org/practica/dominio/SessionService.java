package org.practica.dominio;

import java.util.Map;

public interface SessionService {
    String agregarSesion(String sessionId, String username);
    void quitarSesion(String sessionId);
    String obtenerSessionPorUsername(String sessionId);
    Map<String, String> obtenerTodasLasSesiones();
}
