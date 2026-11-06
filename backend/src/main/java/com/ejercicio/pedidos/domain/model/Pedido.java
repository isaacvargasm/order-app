package com.ejercicio.pedidos.domain.model;

import com.ejercicio.pedidos.domain.exception.TransicionInvalidaException;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Pedido {
    private UUID id;
    private String idCliente;
    private Canal canal;
    private List<LineaPedido> lineasPedido;
    private String tiendaEntrega;
    private EstadoPedido estado;
    private Double total;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;

    public Pedido(UUID id, String idCliente, Canal canal, List<LineaPedido> lineasPedido, String tiendaEntrega) {
        this(id, idCliente, canal, lineasPedido, tiendaEntrega, EstadoPedido.CREADO, Instant.now(), Instant.now());
    }

    public Pedido(UUID id, String idCliente, Canal canal, List<LineaPedido> lineasPedido, String tiendaEntrega,
                  EstadoPedido estado, Instant fechaCreacion, Instant fechaActualizacion) {
        this.id = id;
        this.idCliente = idCliente;
        this.canal = canal;
        this.lineasPedido = lineasPedido;
        this.total = calcularTotal();
        this.tiendaEntrega = tiendaEntrega;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public void actualizarEstado(EstadoPedido nuevoEstado) {

        if (nuevoEstado == null) {
            throw new TransicionInvalidaException("El nuevo estado no puede ser nulo");
        }

        if (nuevoEstado.equals(EstadoPedido.CANCELADO) && !(this.estado.equals(EstadoPedido.CREADO) || this.estado.equals(EstadoPedido.PAGADO)) ) {
            throw new TransicionInvalidaException("No es posible cancelar el pedido, transición no válida");
        }

        if (nuevoEstado.equals(EstadoPedido.PAGADO) && !this.estado.equals(EstadoPedido.CREADO)) {
            throw new TransicionInvalidaException("Transición no válida");
        }

        if (nuevoEstado.equals(EstadoPedido.EN_PREPARACION) && !this.estado.equals(EstadoPedido.PAGADO)) {
            throw new TransicionInvalidaException("Transición no válida");
        }

        if (nuevoEstado.equals(EstadoPedido.LISTO_PARA_RECOGER) && !this.estado.equals(EstadoPedido.EN_PREPARACION)) {
            throw new TransicionInvalidaException("Transición no válida");
        }

        if (nuevoEstado.equals(EstadoPedido.ENTREGADO) && !this.estado.equals(EstadoPedido.LISTO_PARA_RECOGER)) {
            throw new TransicionInvalidaException("Transición no válida");
        }

        this.estado = nuevoEstado;
        this.fechaActualizacion = Instant.now();
    }

    public double calcularTotal() {
        double suma = lineasPedido.stream()
                .mapToDouble(lineaPedido -> lineaPedido.cantidad() * lineaPedido.precio())
                .sum();
        return BigDecimal.valueOf(suma).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
