package com.ejercicio.pedidos.infrastructure.adapter.out.persistence.mapper;

import com.ejercicio.pedidos.domain.model.Dinero;
import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.LineaPedidoDocument;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.PedidoDocument;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PedidoDocumentMapper {

    public PedidoDocument toDocument(Pedido pedido) {
        return new PedidoDocument(
                pedido.getId().toString(),
                pedido.getIdCliente(),
                pedido.getCanal(),
                pedido.getLineasPedido().stream()
                        .map(l -> new LineaPedidoDocument(l.sku(), l.cantidad(), l.precio().monto()))
                        .toList(),
                pedido.getTiendaEntrega(),
                pedido.getEstado(),
                pedido.getFechaCreacion(),
                pedido.getFechaActualizacion(),
                pedido.getVersion());
    }

    public Pedido toDomain(PedidoDocument doc) {
        return new Pedido(
                UUID.fromString(doc.getId()),
                doc.getIdCliente(),
                doc.getCanal(),
                doc.getLineas().stream()
                        .map(l -> new LineaPedido(l.sku(), l.cantidad(), Dinero.of(l.precio())))
                        .toList(),
                doc.getTiendaEntrega(),
                doc.getEstado(),
                doc.getFechaCreacion(),
                doc.getFechaActualizacion(),
                doc.getVersion());
    }
}
