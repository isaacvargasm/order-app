package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.out.PedidoPublisherPort;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.event.EstadoCambiado;
import com.ejercicio.pedidos.domain.exception.ConcurrenciaPedidoException;
import com.ejercicio.pedidos.domain.exception.PedidoNoEncontradoException;
import com.ejercicio.pedidos.domain.exception.TransicionInvalidaException;
import com.ejercicio.pedidos.domain.model.Canal;
import com.ejercicio.pedidos.domain.model.Dinero;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarEstadoPedidoServiceTest {

    private static final UUID ID = UUID.randomUUID();

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private PedidoPublisherPort pedidoPublisherPort;

    @InjectMocks
    private ActualizarEstadoPedidoService service;

    @Test
    void testActualizarEstado() {
        when(pedidoRepositoryPort.consultarPorId(ID)).thenReturn(crearPedido(EstadoPedido.CREADO));
        when(pedidoRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = service.actualizarEstado(ID, EstadoPedido.PAGADO);

        ArgumentCaptor<Pedido> captorPedido = ArgumentCaptor.forClass(Pedido.class);
        ArgumentCaptor<EstadoCambiado> captorEstadoCambiado = ArgumentCaptor.forClass(EstadoCambiado.class);

        verify(pedidoRepositoryPort).guardar(captorPedido.capture());
        verify(pedidoPublisherPort).publicarEstadoCambiado(captorEstadoCambiado.capture());

        assertThat(captorPedido.getValue().getEstado()).isEqualTo(EstadoPedido.PAGADO);
        assertThat(captorPedido.getValue().getVersion()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.PAGADO);

        EstadoCambiado evento = captorEstadoCambiado.getValue();
        assertThat(evento.idEvento()).isNotNull();
        assertThat(evento.fechaEvento()).isNotNull();
        assertThat(evento.idPedido()).isEqualTo(ID);
        assertThat(evento.idCliente()).isEqualTo("cliente-1");
        assertThat(evento.estadoAnterior()).isEqualTo(EstadoPedido.CREADO);
        assertThat(evento.estadoNuevo()).isEqualTo(EstadoPedido.PAGADO);
    }

    @Test
    void testNoHayEventoCuandoEstadoNoCambia() {
        when(pedidoRepositoryPort.consultarPorId(ID)).thenReturn(crearPedido(EstadoPedido.CREADO));
        when(pedidoRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.actualizarEstado(ID, EstadoPedido.CREADO);

        verify(pedidoRepositoryPort).guardar(any());
        verifyNoInteractions(pedidoPublisherPort);
    }

    @Test
    void testPedidoNoEncontradoLanzaExcepcion() {
        when(pedidoRepositoryPort.consultarPorId(ID)).thenThrow(new PedidoNoEncontradoException(ID));
        assertThatThrownBy(() -> service.actualizarEstado(ID, EstadoPedido.PAGADO))
                .isInstanceOf(PedidoNoEncontradoException.class);
        verify(pedidoRepositoryPort, never()).guardar(any());
        verifyNoInteractions(pedidoPublisherPort);
    }

    @Test
    void testTransicionInvalidaLanzaExcepcion() {
        when(pedidoRepositoryPort.consultarPorId(ID)).thenReturn(crearPedido(EstadoPedido.ENTREGADO));
        assertThatThrownBy(() -> service.actualizarEstado(ID, EstadoPedido.CANCELADO))
                .isInstanceOf(TransicionInvalidaException.class);
        verify(pedidoRepositoryPort, never()).guardar(any());
        verifyNoInteractions(pedidoPublisherPort);
    }

    @Test
    void testConcurrenciaLanzaExcepcion() {
        when(pedidoRepositoryPort.consultarPorId(ID)).thenReturn(crearPedido(EstadoPedido.CREADO));
        when(pedidoRepositoryPort.guardar(any())).thenThrow(new ConcurrenciaPedidoException(ID));
        assertThatThrownBy(() -> service.actualizarEstado(ID, EstadoPedido.PAGADO))
                .isInstanceOf(ConcurrenciaPedidoException.class);
        verifyNoInteractions(pedidoPublisherPort);
    }

    private static Pedido crearPedido(EstadoPedido estado) {
        Instant fecha = Instant.parse("2022-01-01T00:00:00Z");
        return new Pedido(ID, "cliente-1", Canal.APP,
                List.of(new LineaPedido("SKU-1", 1, Dinero.of(new BigDecimal("10.00")))),
                "tienda-1", estado, fecha, fecha, 1L);
    }
}