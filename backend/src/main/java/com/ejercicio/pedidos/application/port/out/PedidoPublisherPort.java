package com.ejercicio.pedidos.application.port.out;

import com.ejercicio.pedidos.domain.model.Pedido;

public interface PedidoPublisherPort {

    void publicarEventoPedido(Pedido pedido);

}
