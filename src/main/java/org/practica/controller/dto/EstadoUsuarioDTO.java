package org.practica.controller.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadoUsuarioDTO {

    private String username;
    private EstadoConexion estado;

}
