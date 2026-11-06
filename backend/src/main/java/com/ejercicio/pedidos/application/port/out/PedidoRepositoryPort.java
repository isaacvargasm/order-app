package com.ejercicio.pedidos.application.port.out;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.UUID;

public interface PedidoRepositoryPort {

    Pedido guardar(Pedido pedido);

    Pedido consultarPorId(UUID id);

    List<Pedido> consultarPorClienteYEstado(String idCliente, EstadoPedido estadoPedido);

}
