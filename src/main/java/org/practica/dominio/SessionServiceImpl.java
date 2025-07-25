package org.practica.dominio;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionServiceImpl implements SessionService{

    private final Map<String,String> sessiones = new ConcurrentHashMap<>();

    @Override
    public String agregarSesion(String username, String sessionId) {
       return sessiones.put(username,sessionId);
    }

    @Override
    public void quitarSesion(String sessionId) {
        sessiones.entrySet().removeIf(entry -> entry.getValue().equals(sessionId));;
    }

    @Override
    public String obtenerSessionPorUsername(String username) {
        return sessiones.get(username);
    }

    @Override
    public Map<String, String> obtenerTodasLasSesiones() {
        return sessiones;
    }
}
