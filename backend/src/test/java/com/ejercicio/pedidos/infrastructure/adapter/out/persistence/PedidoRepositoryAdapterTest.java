package com.ejercicio.pedidos.infrastructure.adapter.out.persistence;

import com.ejercicio.pedidos.domain.exception.ConcurrenciaPedidoException;
import com.ejercicio.pedidos.domain.exception.PedidoNoEncontradoException;
import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.Dinero;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.LineaPedidoDocument;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document.PedidoDocument;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.mapper.PedidoDocumentMapper;
import com.ejercicio.pedidos.infrastructure.adapter.out.persistence.repository.PedidoMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryAdapterTest {

    private static final UUID ID = UUID.randomUUID();
    private static final Instant FECHA = Instant.parse("2022-01-01T00:00:00Z");

    @Mock
    private PedidoMongoRepository repository;

    private PedidoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new PedidoRepositoryAdapter(repository, new PedidoDocumentMapper());
    }

    @Test
    void testGuardarPedido() {
        when(repository.save(any())).thenReturn(documento(3L));
        Pedido resultadoPedido = adapter.guardar(pedido(2L));

        ArgumentCaptor<PedidoDocument> captor = ArgumentCaptor.forClass(PedidoDocument.class);
        verify(repository).save(captor.capture());
        PedidoDocument pedidoDocumentEnviado = captor.getValue();
        assertThat(pedidoDocumentEnviado.getId()).isEqualTo(ID.toString());
        assertThat(pedidoDocumentEnviado.getVersion()).isEqualTo(2L);
        assertThat(pedidoDocumentEnviado.getEstado()).isEqualTo(EstadoPedido.PAGADO);

        assertThat(resultadoPedido.getId()).isEqualTo(ID);
        assertThat(resultadoPedido.getVersion()).isEqualTo(3L);
    }

    @Test
    void testGuardarConcurrenciaLanzaExcepcion() {
        when(repository.save(any())).thenThrow(new OptimisticLockingFailureException("Versión antigua"));

        assertThatThrownBy(() -> adapter.guardar(pedido(2L)))
                .isInstanceOf(ConcurrenciaPedidoException.class)
                .hasMessageContaining(ID.toString());
    }

    @Test
    void testConsultarPorId() {
        when(repository.findById(ID.toString())).thenReturn(Optional.of(documento(5L)));
        Pedido resultado = adapter.consultarPorId(ID);
        assertThat(resultado.getId()).isEqualTo(ID);
        assertThat(resultado.getVersion()).isEqualTo(5L);
    }

    @Test
    void testConsultarPorIdInexistenteLanzaExcepcion() {
        when(repository.findById(ID.toString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adapter.consultarPorId(ID))
                .isInstanceOf(PedidoNoEncontradoException.class);
    }

    @Test
    void testConsultarPorClienteSinEstado() {
        when(repository.findByIdCliente("cliente-1")).thenReturn(List.of(documento(1L)));
        List<Pedido> resultado = adapter.consultarPorClienteYEstado("cliente-1", null);
        assertThat(resultado).hasSize(1);
        verify(repository).findByIdCliente("cliente-1");
    }

    @Test
    void testConsultarPorClienteConEstado() {
        when(repository.findByIdClienteAndEstado("cliente-1", EstadoPedido.PAGADO))
                .thenReturn(List.of(documento(1L)));
        List<Pedido> resultado = adapter.consultarPorClienteYEstado("cliente-1", EstadoPedido.PAGADO);
        assertThat(resultado).hasSize(1);
        verify(repository).findByIdClienteAndEstado("cliente-1", EstadoPedido.PAGADO);
    }

    private static Pedido pedido(Long version) {
        return new Pedido(ID, "cliente-1", Canal.APP,
                List.of(new LineaPedido("SKU-1", 3, Dinero.of(new BigDecimal("10.10")))),
                "tienda-1", EstadoPedido.PAGADO, FECHA, FECHA, version);
    }

    private static PedidoDocument documento(Long version) {
        return new PedidoDocument(ID.toString(), "cliente-1", Canal.APP,
                List.of(new LineaPedidoDocument("SKU-1", 3, new BigDecimal("10.10"))),
                "tienda-1", EstadoPedido.PAGADO, FECHA, FECHA, version);
    }
}