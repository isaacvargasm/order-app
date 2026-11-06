package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoRequest {

    @NotNull(message = "Estado es requerido")
    private EstadoPedido estado;

}
