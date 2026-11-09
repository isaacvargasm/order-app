package com.ejercicio.pedidos.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Dinero(BigDecimal monto) {

    public static final Dinero CERO = new Dinero(BigDecimal.ZERO);

    public Dinero {
        Objects.requireNonNull(monto, "El monto es requerido");
        if (monto.signum() < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        monto = monto.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero of(BigDecimal monto) {
        return new Dinero(monto);
    }

    public Dinero sumar(Dinero dinero) {
        return new Dinero(monto.add(dinero.monto));
    }

    public Dinero multiplicar(int factor) {
        return new Dinero(monto.multiply(BigDecimal.valueOf(factor)));
    }
}