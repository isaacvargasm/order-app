package com.ejercicio.pedidos.domain.model;

public record LineaPedido(String sku, int cantidad, Dinero precio) {

    public LineaPedido {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El sku es requerido");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (precio == null) {
            throw new IllegalArgumentException("El precio es requerido");
        }
    }

    public Dinero obtenerTotalLinea() {
        return precio.multiplicar(cantidad);
    }
}