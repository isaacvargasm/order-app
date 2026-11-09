package com.ejercicio.pedidos.application.port.out;

import com.ejercicio.pedidos.domain.event.EstadoCambiado;

public interface PedidoPublisherPort {

    void publicarEstadoCambiado(EstadoCambiado evento);

}