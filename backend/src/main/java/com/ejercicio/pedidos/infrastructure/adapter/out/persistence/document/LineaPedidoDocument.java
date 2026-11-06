package com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document;

public record LineaPedidoDocument(String sku, int cantidad, double precio) {
}
