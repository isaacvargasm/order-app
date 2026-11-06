package com.ejercicio.pedidos.domain.exception;

public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
