package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarPedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private ConsultarPedidoService service;

    @Test
    void testConsultarPorId() {
        UUID id = UUID.randomUUID();
        Pedido pedido = mock(Pedido.class);
        when(pedidoRepositoryPort.consultarPorId(id)).thenReturn(pedido);
        assertThat(service.consultarPorId(id)).isEqualTo(pedido);
    }

    @Test
    void testConsultarPorClienteYEstado() {
        List<Pedido> pedidos = List.of(mock(Pedido.class));
        when(pedidoRepositoryPort.consultarPorClienteYEstado("cliente-1", EstadoPedido.PAGADO)).thenReturn(pedidos);
        assertThat(service.consultarPorClienteYEstado("cliente-1", EstadoPedido.PAGADO)).isEqualTo(pedidos);
    }

    @Test
    void testConsultarPorClienteYSinEstado() {
        List<Pedido> pedidos = List.of(mock(Pedido.class));
        when(pedidoRepositoryPort.consultarPorClienteYEstado("cliente-1", null)).thenReturn(pedidos);
        assertThat(service.consultarPorClienteYEstado("cliente-1", null)).isEqualTo(pedidos);
    }
}