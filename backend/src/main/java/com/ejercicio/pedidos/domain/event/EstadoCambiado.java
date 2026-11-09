package com.ejercicio.pedidos.domain.event;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.time.Instant;
import java.util.UUID;

public record EstadoCambiado(
        UUID idEvento,
        Instant fechaEvento,
        UUID idPedido,
        String idCliente,
        EstadoPedido estadoAnterior,
        EstadoPedido estadoNuevo) {

    public static EstadoCambiado of(Pedido pedido, EstadoPedido estadoAnterior) {
        return new EstadoCambiado(
                UUID.randomUUID(),
                Instant.now(),
                pedido.getId(),
                pedido.getIdCliente(),
                estadoAnterior,
                pedido.getEstado());
    }
}