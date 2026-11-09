package com.ejercicio.pedidos.infrastructure.adapter.out.persistence.document;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;

public record LineaPedidoDocument(
        String sku,
        int cantidad,
        @Field(targetType = FieldType.DECIMAL128) BigDecimal precio) {
}