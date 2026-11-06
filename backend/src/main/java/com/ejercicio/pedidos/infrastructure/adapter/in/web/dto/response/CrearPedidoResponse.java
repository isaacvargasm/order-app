package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response;

import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.LineaPedido;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CrearPedidoResponse(
        UUID id,
        String idCliente,
        Canal canal,
        EstadoPedido estado,
        List<LineaPedido> lineasPedido,
        Double total,
        String tiendaEntrega,
        Instant fechaCreacion,
        Instant fechaActualizacion) {
}
