package com.ejercicio.pedidos;

import com.google.cloud.spring.pubsub.PubSubAdmin;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.mongodb.uri=mongodb://localhost:27017/pedidos-test")
class PedidosApplicationTests {

	@MockitoBean
	PubSubAdmin pubSubAdmin;

	@MockitoBean
	PubSubTemplate pubSubTemplate;

	@Test
	void contextLoads() {
	}

}