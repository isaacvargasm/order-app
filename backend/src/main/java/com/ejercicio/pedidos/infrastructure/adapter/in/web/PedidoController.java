package com.ejercicio.pedidos.infrastructure.adapter.in.web;

import com.ejercicio.pedidos.application.port.in.ActualizarEstadoPedidoUseCase;
import com.ejercicio.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.ejercicio.pedidos.application.port.in.CrearPedidoUseCase;
import com.ejercicio.pedidos.domain.model.EstadoPedido;
import com.ejercicio.pedidos.domain.model.Pedido;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request.ActualizarEstadoRequest;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.request.CrearPedidoRequest;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.CambiarEstadoPedidoResponse;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.ConsultarPedidoResponse;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.dto.response.CrearPedidoResponse;
import com.ejercicio.pedidos.infrastructure.adapter.in.web.mapper.PedidoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CrearPedidoUseCase crearPedidoUseCase;

    private final ConsultarPedidoUseCase consultarPedidoUseCase;

    private final ActualizarEstadoPedidoUseCase actualizarEstadoPedidoUseCase;

    private final PedidoMapper pedidoMapper;

    public PedidoController(CrearPedidoUseCase crearPedidoUseCase, ConsultarPedidoUseCase consultarPedidoUseCase,
                            ActualizarEstadoPedidoUseCase actualizarEstadoPedidoUseCase, PedidoMapper pedidoMapper) {
        this.crearPedidoUseCase = crearPedidoUseCase;
        this.consultarPedidoUseCase = consultarPedidoUseCase;
        this.actualizarEstadoPedidoUseCase = actualizarEstadoPedidoUseCase;
        this.pedidoMapper = pedidoMapper;
    }

    @PostMapping
    public ResponseEntity<CrearPedidoResponse> crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
        Pedido pedido = crearPedidoUseCase.crearPedido(
                request.getIdCliente(),
                request.getCanal(),
                pedidoMapper.toLineasPedido(request.getLineasPedido()),
                request.getTiendaEntrega());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pedidoMapper.toPedidoResponse(pedido));
    }

    @GetMapping
    public ResponseEntity<List<ConsultarPedidoResponse>> consultarPedidoPorClienteYEstado(
            @RequestParam String idCliente,
            @RequestParam(required = false) EstadoPedido estado) {
        List<Pedido> pedidos = consultarPedidoUseCase.consultarPorClienteYEstado(idCliente, estado);

        return ResponseEntity.ok(pedidoMapper.toConsultarPedidoResponses(pedidos));
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<ConsultarPedidoResponse> consultarPedidoPorId(@PathVariable UUID idPedido) {
        Pedido pedido = consultarPedidoUseCase.consultarPorId(idPedido);

        return ResponseEntity.ok(pedidoMapper.toConsultarPedidoResponse(pedido));
    }

    @PatchMapping("/{idPedido}/estado")
    public ResponseEntity<CambiarEstadoPedidoResponse> cambiarEstadoPedido(
            @PathVariable UUID idPedido,
            @Valid @RequestBody ActualizarEstadoRequest request) {
        Pedido pedido = actualizarEstadoPedidoUseCase.actualizarEstado(idPedido, request.getEstado());

        return ResponseEntity.ok(pedidoMapper.toCambiarEstadoPedidoResponse(pedido));
    }

}
