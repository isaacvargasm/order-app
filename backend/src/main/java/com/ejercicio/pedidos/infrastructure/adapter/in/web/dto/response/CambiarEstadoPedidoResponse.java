package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response;

import com.ejercicio.pedidos.domain.model.EstadoPedido;

import java.time.Instant;
import java.util.UUID;

public record CambiarEstadoPedidoResponse(
        UUID id,
        EstadoPedido estado,
        Instant fechaActualizacion) {
}
