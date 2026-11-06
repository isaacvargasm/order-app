package com.ejercicio.pedidos.application.port.in;

import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;

import java.util.List;
import java.util.UUID;

public interface CrearPedidoUseCase {

    Pedido crearPedido(String idCliente, Canal canal, List<LineaPedido> lineasPedido, String tiendaEntrega);

}
