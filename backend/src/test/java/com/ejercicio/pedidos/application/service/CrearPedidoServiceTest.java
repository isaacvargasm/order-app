package com.ejercicio.pedidos.application.service;

import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPedidoServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @InjectMocks
    private CrearPedidoService service;

    @Test
    void testCrearPedido() {
        List<LineaPedido> lineas = List.of(new LineaPedido("SKU-1", 2, Dinero.of(new BigDecimal("5.00"))));
        when(pedidoRepositoryPort.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.crearPedido("cliente-1", Canal.APP, lineas, "tienda-1");

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepositoryPort, times(1)).guardar(captor.capture());
        Pedido guardado = captor.getValue();
        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(guardado.getIdCliente()).isEqualTo("cliente-1");
        assertThat(guardado.getCanal()).isEqualTo(Canal.APP);
        assertThat(guardado.getLineasPedido()).isEqualTo(lineas);
        assertThat(guardado.getTiendaEntrega()).isEqualTo("tienda-1");
    }
}