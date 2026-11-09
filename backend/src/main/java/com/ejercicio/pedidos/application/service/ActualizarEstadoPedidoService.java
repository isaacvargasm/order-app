package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.in.ActualizarEstadoPedidoUseCase;
import com.ejercicio.pedidos.application.port.out.PedidoPublisherPort;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.event.EstadoCambiado;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.UUID;

public class ActualizarEstadoPedidoService implements ActualizarEstadoPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;
    private final PedidoPublisherPort pedidoPublisherPort;

    public ActualizarEstadoPedidoService(PedidoRepositoryPort pedidoRepositoryPort,
                                         PedidoPublisherPort pedidoPublisherPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
        this.pedidoPublisherPort = pedidoPublisherPort;
    }

    @Override
    public Pedido actualizarEstado(UUID id, EstadoPedido estadoPedido) {
        Pedido pedido = pedidoRepositoryPort.consultarPorId(id);
        EstadoPedido estadoAnterior = pedido.getEstado();
        pedido.actualizarEstado(estadoPedido);

        Pedido pedidoGuardado = pedidoRepositoryPort.guardar(pedido);

        if (estadoAnterior != pedidoGuardado.getEstado()) {
            pedidoPublisherPort.publicarEstadoCambiado(EstadoCambiado.of(pedidoGuardado, estadoAnterior));
        }
        return pedidoGuardado;
    }
}