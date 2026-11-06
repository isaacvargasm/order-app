package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String codigo, String mensaje, Map<String, Object> detalles) {

    public ErrorResponse(String codigo, String mensaje) {
        this(codigo, mensaje, null);
    }
}