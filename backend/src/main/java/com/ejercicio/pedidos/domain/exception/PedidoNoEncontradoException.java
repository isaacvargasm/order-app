package com.ejercicio.pedidos.domain.exception;

import java.util.UUID;

public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException(UUID id) {
        super("No se encontró el pedido " + id);
    }
}
