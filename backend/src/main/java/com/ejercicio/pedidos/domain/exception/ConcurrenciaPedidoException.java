package com.ejercicio.pedidos.domain.exception;

import java.util.UUID;

public class ConcurrenciaPedidoException extends RuntimeException {

    public ConcurrenciaPedidoException(UUID idPedido) {
        super("El pedido " + idPedido + " fue modificado por otro usuario, intente de nuevo");
    }
}