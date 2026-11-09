package com.ejercicio.pedidos.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DineroTest {

    @Test
    void testMontoNegativo() {
        assertThatThrownBy(() -> dinero("-0.01"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testMontoNulo() {
        assertThatThrownBy(() -> Dinero.of(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testSumar() {
        assertThat(dinero("0.10").sumar(dinero("0.20"))).isEqualTo(dinero("0.30"));
    }

    @Test
    void testMultiplicar() {
        assertThat(dinero("10.10").multiplicar(3)).isEqualTo(dinero("30.30"));
    }

    @Test
    void testRedondeo() {
        assertThat(dinero("10.005").monto()).isEqualTo(new BigDecimal("10.01"));
        assertThat(dinero("0.125").monto()).isEqualTo(new BigDecimal("0.13"));
    }

    private static Dinero dinero(String monto) {
        return Dinero.of(new BigDecimal(monto));
    }
}