package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.in.ActualizarEstadoPedidoUseCase;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.UUID;

public class ActualizarEstadoPedidoService implements ActualizarEstadoPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    public ActualizarEstadoPedidoService(PedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido actualizarEstado(UUID id, EstadoPedido estadoPedido) {
        Pedido pedido = pedidoRepositoryPort.consultarPorId(id);
        pedido.actualizarEstado(estadoPedido);
        return pedidoRepositoryPort.guardar(pedido);
    }
}
