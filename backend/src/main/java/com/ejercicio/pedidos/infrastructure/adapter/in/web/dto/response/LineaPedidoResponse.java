package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response;

import java.math.BigDecimal;

public record LineaPedidoResponse(
        String sku,
        int cantidad,
        BigDecimal precio) {
}