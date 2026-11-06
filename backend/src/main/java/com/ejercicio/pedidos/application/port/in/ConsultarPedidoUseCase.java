package com.ejercicio.pedidos.application.port.in;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.UUID;

public interface ConsultarPedidoUseCase {

    Pedido consultarPorId(UUID id);

    List<Pedido> consultarPorClienteYEstado(String idCliente, EstadoPedido estadoPedido);

}
