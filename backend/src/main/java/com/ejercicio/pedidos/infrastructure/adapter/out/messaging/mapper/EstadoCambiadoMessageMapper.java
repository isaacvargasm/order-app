package com.ejercicio.pedidos.infrastructure.adapter.out.messaging.mapper;

import com.ejercicio.pedidos.domain.event.EstadoCambiado;
import com.ejercicio.pedidos.infrastructure.adapter.out.messaging.message.EstadoCambiadoMessage;
import org.springframework.stereotype.Component;

@Component
public class EstadoCambiadoMessageMapper {

    public EstadoCambiadoMessage toMessage(EstadoCambiado evento) {
        return new EstadoCambiadoMessage(
                evento.idEvento().toString(),
                evento.fechaEvento().toString(),
                evento.idPedido().toString(),
                evento.idCliente(),
                evento.estadoAnterior().name(),
                evento.estadoNuevo().name());
    }
}