package com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request;

import com.ejercicio.pedidos.domain.model.Canal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CrearPedidoRequest {

    @NotBlank(message = "ID de cliente es requerido")
    private String idCliente;

    @NotNull(message = "Canal es requerido")
    private Canal canal;

    @NotEmpty(message = "Lineas pedido es requerido")
    @Valid
    private List<LineaPedidoRequest> lineasPedido;

    @NotBlank(message = "Tienda de entrega es requerido")
    private String tiendaEntrega;

}
