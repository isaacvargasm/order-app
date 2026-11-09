package com.ejercicio.pedidos.domain.model;

import com.ejercicio.pedidos.domain.exception.TransicionInvalidaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoTest {

    private static final Instant FECHA = Instant.parse("2022-01-01T00:00:00Z");

    @Test
    void testCrearNuevoPedido() {
        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.APP,
                List.of(linea("SKU-1", 1, "5.00")), "tienda-1");

        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(pedido.getVersion()).isNull();
        assertThat(pedido.getFechaCreacion()).isNotNull();
        assertThat(pedido.getFechaActualizacion()).isNotNull();
    }

    @Test
    void testTotalPedido() {
        LineaPedido linea = linea("SKU-1", 3, "10.10");

        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.WEB, List.of(linea), "tienda-1");

        assertThat(pedido.getTotal()).isEqualTo(linea.obtenerTotalLinea());
    }

    @Test
    void testTotalPedidoConVariasLineas() {
        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.WEB,
                List.of(linea("SKU-1", 3, "10.10"), linea("SKU-2", 2, "0.10")), "tienda-1");

        assertThat(pedido.getTotal()).isEqualTo(Dinero.of(new BigDecimal("30.50")));
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "CREADO, PAGADO",
            "CREADO, CANCELADO",
            "PAGADO, CANCELADO",
            "PAGADO, EN_PREPARACION",
            "EN_PREPARACION, LISTO_PARA_RECOGER",
            "LISTO_PARA_RECOGER, ENTREGADO"
    })
    void testTransicionesValidas(EstadoPedido origen, EstadoPedido destino) {

        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.APP,
                List.of(linea("SKU-1", 1, "10.00")), "tienda-1",
                origen, FECHA, FECHA, 3L);

        pedido.actualizarEstado(destino);

        assertThat(pedido.getEstado()).isEqualTo(destino);
        assertThat(pedido.getFechaActualizacion()).isAfter(FECHA);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "CREADO, EN_PREPARACION",
            "CREADO, ENTREGADO",
            "PAGADO, PAGADO",
            "EN_PREPARACION, CANCELADO",
            "LISTO_PARA_RECOGER, CANCELADO",
            "ENTREGADO, CANCELADO",
            "CANCELADO, PAGADO",
            "ENTREGADO, PAGADO"
    })
    void testTransicionesInvalidasLanzanExcepcion(EstadoPedido origen, EstadoPedido destino) {

        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.APP,
                List.of(linea("SKU-1", 1, "10.00")), "tienda-1",
                origen, FECHA, FECHA, 3L);

        assertThatThrownBy(() -> pedido.actualizarEstado(destino))
                .isInstanceOf(TransicionInvalidaException.class);

        assertThat(pedido.getEstado()).isEqualTo(origen);
        assertThat(pedido.getFechaActualizacion()).isEqualTo(FECHA);
    }

    @Test
    void testEstadoNuloLanzaExcepcion() {

        Pedido pedido = new Pedido(UUID.randomUUID(), "cliente-1", Canal.APP,
                List.of(linea("SKU-1", 1, "10.00")), "tienda-1",
                EstadoPedido.CREADO, FECHA, FECHA, 3L);

        assertThatThrownBy(() -> pedido.actualizarEstado(null))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    private static LineaPedido linea(String sku, int cantidad, String precio) {
        return new LineaPedido(sku, cantidad, Dinero.of(new BigDecimal(precio)));
    }
}