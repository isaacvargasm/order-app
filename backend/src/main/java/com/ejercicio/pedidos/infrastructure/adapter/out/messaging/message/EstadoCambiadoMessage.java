package com.ejercicio.pedidos.infrastructure.adapter.out.messaging.message;

/**
 * Payload del evento EstadoCambiado en asyncapi
 */
public record EstadoCambiadoMessage(
        String idEvento,
        String fechaEvento,
        String idPedido,
        String idCliente,
        String estadoAnterior,
        String estadoNuevo) {
}