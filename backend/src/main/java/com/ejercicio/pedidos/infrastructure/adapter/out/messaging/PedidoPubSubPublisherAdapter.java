package com.ejercicio.pedidos.infrastructure.adapter.out.messaging;

import com.ejercicio.pedidos.application.port.out.PedidoPublisherPort;
import com.ejercicio.pedidos.domain.event.EstadoCambiado;
import com.ejercicio.pedidos.infrastructure.adapter.out.messaging.mapper.EstadoCambiadoMessageMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

@Slf4j
@Component
public class PedidoPubSubPublisherAdapter implements PedidoPublisherPort {

    static final String ATRIBUTO_TIPO_EVENTO = "tipoEvento";
    static final String TIPO_ESTADO_CAMBIADO = "EstadoCambiado";

    private final PubSubTemplate pubSubTemplate;
    private final EstadoCambiadoMessageMapper mapper;
    private final JsonMapper jsonMapper;
    private final String topicEstadoCambiado;

    public PedidoPubSubPublisherAdapter(PubSubTemplate pubSubTemplate,
                                        EstadoCambiadoMessageMapper mapper,
                                        JsonMapper jsonMapper,
                                        @Value("${pedidos.pubsub.topic-estado-cambiado}") String topicEstadoCambiado) {
        this.pubSubTemplate = pubSubTemplate;
        this.mapper = mapper;
        this.jsonMapper = jsonMapper;
        this.topicEstadoCambiado = topicEstadoCambiado;
    }

    @Override
    public void publicarEstadoCambiado(EstadoCambiado evento) {
        String payload = jsonMapper.writeValueAsString(mapper.toMessage(evento));
        
        pubSubTemplate.publish(topicEstadoCambiado, payload, Map.of(ATRIBUTO_TIPO_EVENTO, TIPO_ESTADO_CAMBIADO))
                .whenComplete((messageId, ex) -> {
                    if (ex != null) {
                        log.error("Error publicando EstadoCambiado, idEvento={} idPedido={}",
                                evento.idEvento(), evento.idPedido(), ex);
                    } else {
                        log.info("EstadoCambiado publicado: messageId={} idEvento={} idPedido={} {} -> {}",
                                messageId, evento.idEvento(), evento.idPedido(),
                                evento.estadoAnterior(), evento.estadoNuevo());
                    }
                });
    }
}