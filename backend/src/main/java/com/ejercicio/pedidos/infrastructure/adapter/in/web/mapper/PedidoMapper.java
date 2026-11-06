package com.ejercicio.pedidos.infrastructure.adapter.in.web.mapper;

import com.ejercicio.pedidos.domain.model.LineaPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request.LineaPedidoRequest;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.CambiarEstadoPedidoResponse;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.ConsultarPedidoResponse;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.CrearPedidoResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    LineaPedido toLineaPedido(LineaPedidoRequest request);

    List<LineaPedido> toLineasPedido(List<LineaPedidoRequest> requests);

    CrearPedidoResponse toPedidoResponse(Pedido pedido);

    ConsultarPedidoResponse toConsultarPedidoResponse(Pedido pedido);

    List<ConsultarPedidoResponse> toConsultarPedidoResponses(List<Pedido> pedidos);

    CambiarEstadoPedidoResponse toCambiarEstadoPedidoResponse(Pedido pedido);

}
