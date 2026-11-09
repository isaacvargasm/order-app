package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineaPedidoRequest {

    @NotBlank(message = "SKU es requerido")
    private String sku;

    @NotNull(message = "Cantidad es requerida")
    @Min(value = 1, message = "Cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "Precio es requerido")
    @PositiveOrZero(message = "Precio no puede ser negativo")
    private BigDecimal precio;

}
