package com.ejercicio.pedidos.infrastructure.adapter.out.persistence;

import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.exception.PedidoNoEncontradoException;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.PedidoDocument;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.mapper.PedidoDocumentMapper;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.repository.PedidoMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {

    private final PedidoMongoRepository repository;
    private final PedidoDocumentMapper mapper;

    public PedidoRepositoryAdapter(PedidoMongoRepository repository, PedidoDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoDocument pedidoDocument = mapper.toDocument(pedido);
        PedidoDocument savedPedidoDocument = repository.save(pedidoDocument);
        return mapper.toDomain(savedPedidoDocument);
    }

    @Override
    public Pedido consultarPorId(UUID id) {
        return repository.findById(id.toString())
                .map(mapper::toDomain)
                .orElseThrow(() -> new PedidoNoEncontradoException(id));
    }

    @Override
    public List<Pedido> consultarPorClienteYEstado(String idCliente, EstadoPedido estadoPedido) {
        List<PedidoDocument> documentosPedido = estadoPedido == null
                ? repository.findByIdCliente(idCliente)
                : repository.findByIdClienteAndEstado(idCliente, estadoPedido);
        return documentosPedido.stream().map(mapper::toDomain).toList();
    }
}
