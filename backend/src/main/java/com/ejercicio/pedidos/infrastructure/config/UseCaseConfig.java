package com.ejercicio.pedidos.infrastructure.config;

import com.ejercicio.pedidos.application.port.in.ActualizarEstadoPedidoUseCase;
import com.ejercicio.pedidos.application.port.in.ConsultarPedidoUseCase;
import com.ejercicio.pedidos.application.port.in.CrearPedidoUseCase;
import com.ejercicio.pedidos.application.port.out.PedidoPublisherPort;
import com.ejercicio.pedidos.application.port.out.PedidoRepositoryPort;
import com.ejercicio.pedidos.application.service.ActualizarEstadoPedidoService;
import com.ejercicio.pedidos.application.service.ConsultarPedidoService;
import com.ejercicio.pedidos.application.service.CrearPedidoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    CrearPedidoUseCase crearPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort) {
        return new CrearPedidoService(pedidoRepositoryPort);
    }

    @Bean
    ConsultarPedidoUseCase consultarPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort) {
        return new ConsultarPedidoService(pedidoRepositoryPort);
    }

    @Bean
    ActualizarEstadoPedidoUseCase actualizarEstadoPedidoUseCase(PedidoRepositoryPort pedidoRepositoryPort,
                                                                PedidoPublisherPort pedidoPublisherPort) {
        return new ActualizarEstadoPedidoService(pedidoRepositoryPort, pedidoPublisherPort);
    }
}
