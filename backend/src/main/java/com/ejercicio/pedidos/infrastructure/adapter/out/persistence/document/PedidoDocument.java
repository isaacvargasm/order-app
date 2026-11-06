package com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document;

import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Document(collection = "pedidos")
public class PedidoDocument {

    @Id
    private String id;

    private String idCliente;

    private Canal canal;

    private List<LineaPedidoDocument> lineas;

    private String tiendaEntrega;

    private EstadoPedido estado;

    private Instant fechaCreacion;
    
    private Instant fechaActualizacion;

}
