package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.in.CrearPedidoUseCase;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.UUID;

public class CrearPedidoService implements CrearPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    public CrearPedidoService(PedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido crearPedido(String idCliente, Canal canal, List<LineaPedido> lineasPedido, String tiendaEntrega) {
        Pedido pedido = new Pedido(UUID.randomUUID(), idCliente, canal, lineasPedido, tiendaEntrega);
        return pedidoRepositoryPort.guardar(pedido);
    }
}
