package com.ejercicio.pedidos.infrastructure.adapter.out.persistence.repository;

import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.PedidoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PedidoMongoRepository extends MongoRepository<PedidoDocument, String> {

    List<PedidoDocument> findByIdCliente(String idCliente);

    List<PedidoDocument> findByIdClienteAndEstado(String idCliente, EstadoPedido estado);
}
