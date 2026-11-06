package com.ejercicio.pedidos.application.port.in;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.UUID;

public interface ActualizarEstadoPedidoUseCase {

    Pedido actualizarEstado(UUID id, EstadoPedido estadoPedido);

}
