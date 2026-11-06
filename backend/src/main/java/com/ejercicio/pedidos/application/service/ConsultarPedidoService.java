package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.UUID;

public class ConsultarPedidoService implements ConsultarPedidoUseCase {

    private final PedidoRepositoryPort pedidoRepositoryPort;

    public ConsultarPedidoService(PedidoRepositoryPort pedidoRepositoryPort) {
        this.pedidoRepositoryPort = pedidoRepositoryPort;
    }

    @Override
    public Pedido consultarPorId(UUID id) {
        return pedidoRepositoryPort.consultarPorId(id);
    }

    @Override
    public List<Pedido> consultarPorClienteYEstado(String idCliente, EstadoPedido estadoPedido) {
        return pedidoRepositoryPort.consultarPorClienteYEstado(idCliente, estadoPedido);
    }
}
