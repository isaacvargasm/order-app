package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response;

import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConsultarPedidoResponse(
        UUID id,
        String idCliente,
        Canal canal,
        EstadoPedido estado,
        List<LineaPedidoResponse> lineasPedido,
        BigDecimal total,
        String tiendaEntrega,
        Instant fechaCreacion,
        Instant fechaActualizacion) {
}