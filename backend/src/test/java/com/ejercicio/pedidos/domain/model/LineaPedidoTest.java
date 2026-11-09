package com.ejercicio.pedidos.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineaPedidoTest {

    private static final Dinero PRECIO = Dinero.of(new BigDecimal("10.10"));

    @Test
    void creaLineaValida() {
        LineaPedido linea = new LineaPedido("SKU-1", 2, PRECIO);

        assertThat(linea.sku()).isEqualTo("SKU-1");
        assertThat(linea.cantidad()).isEqualTo(2);
        assertThat(linea.precio()).isEqualTo(PRECIO);
    }

    @Test
    void testSkuNoValido() {
        assertThatThrownBy(() -> new LineaPedido(" ", 1, PRECIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testCantidadNoValida() {
        assertThatThrownBy(() -> new LineaPedido("SKU-1", 0, PRECIO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testPrecioNulo() {
        assertThatThrownBy(() -> new LineaPedido("SKU-1", 1, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testTotalLinea() {
        LineaPedido linea = new LineaPedido("SKU-1", 3, PRECIO);

        assertThat(linea.obtenerTotalLinea()).isEqualTo(Dinero.of(new BigDecimal("30.30")));
    }
}