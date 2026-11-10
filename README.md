# order-app

Aplicación para manejo de pedidos.

## Ejecución del proyecto de forma local

### Requisitos

- Java 25 (Maven no es necesario: se usa el Maven Wrapper `./mvnw`, que descarga Maven 3.10.0 la primera vez)
- Node.js 20.19+ o 22.12+ (con npm)
- Docker (para el emulador de Pub/Sub y MongoDB)

### 1. Levantar la infraestructura

Desde la raíz del proyecto:

```bash
# Emulador de Pub/Sub en localhost:8085
docker compose up -d pubsub-emulator

# MongoDB en localhost:27017 (no está incluido en docker-compose.yml)
docker run -d --name pedidos-mongo -p 27017:27017 mongo:8
```

### 2. Backend (Spring Boot, puerto 8080)

```bash
cd backend
export MONGODB_URI=mongodb://localhost:27017/pedidos
./mvnw spring-boot:run
```

- Al arrancar se crea automáticamente el topic `pedidos.estado-cambiado` en el emulador.
- La API queda en `http://localhost:8080/pedidos` (contrato en `backend/openapi/openapi.yaml`).

### 3. Frontend (React + Vite, puerto 5173)

```bash
cd frontend
npm install
npm run dev
```

Abrir `http://localhost:5173`. Las llamadas a `/pedidos` se redirigen hacia 
`http://localhost:8080`.

### 4. Datos de prueba

La pantalla muestra los pedidos por cliente, así que primero hay que crear uno:

```bash
curl -X POST http://localhost:8080/pedidos \
  -H 'Content-Type: application/json' \
  -d '{"idCliente":"CLI-98765","canal":"WEB","tiendaEntrega":"T-01","lineasPedido":[{"sku":"SKU-00123","cantidad":2,"precio":19.9}]}'
```

Luego, en la pantalla, buscar por el cliente `CLI-98765`.

### 5. Ver los eventos publicados

```bash
# Crear una suscripción al topic
curl -X PUT http://localhost:8085/v1/projects/pedidos-local/subscriptions/estado-sub \
  -H 'Content-Type: application/json' \
  -d '{"topic":"projects/pedidos-local/topics/pedidos.estado-cambiado"}'

# Después de cambiar el estado de un pedido, leer los mensajes (el campo data viene en base64)
curl -X POST http://localhost:8085/v1/projects/pedidos-local/subscriptions/estado-sub:pull \
  -H 'Content-Type: application/json' -d '{"maxMessages":10}'
```

### 6. Pruebas

```bash
# Backend (PedidosApplicationTests necesita MongoDB en localhost:27017)
cd backend && ./mvnw test

# Frontend (Vitest + Testing Library)
cd frontend && npm test
```
## Arquitectura hexagonal del proyecto.

- `domain`. Contiene los objetos de dominio: `Pedido`, `LineaPedido`, `EstadoPedido`, `Dinero` y `Canal`, así como reglas de negocio. Además, contiene excepciones y el evento EstadoCambiado.
- `application`. Contiene los puertos de entrada que corresponden a los casos de uso para crear un pedido (`CrearPedidoUseCase`), consultar pedido (`ConsultarPedidoUseCase`) y actualizar el estado de un pedido (`ActualizarEstadoPedidoUseCase`). Tiene también los puertos de salida: `PedidoRepositoryPort` y `PedidoPublisherPort`. Aquí también se encuentran los servicios que implementan los casos de uso `CrearPedidoService`, `ConsultarPedidoService` y `ActualizarEstadoPedidoService`.
- `infrastructure`. Contiene el `PedidoController`, adaptador de entrada que utiliza los casos de uso mencionados. Tiene también los adaptadores de salida `PedidoRepositoryAdapter` y `PedidoPubSubPublisherAdapter`, así como clases de configuración de beans de Spring. Contiene el `GlobalExceptionHandler`.

## Contratos.

Los contratos se encuentran en la carpeta `backend`:
- Carpeta `openapi`: Contiene `openapi.yaml`
- Carpeta `asyncapi`: Contiene `asyncapi.yaml`

# Actividad 3.

## Publicación de eventos en Pub/Sub

### Problema

Si el backend se cae entre guardar un pedido y publicar el evento, el evento se pierde.

### Solución: patrón Transactional Outbox

1. Crear una nueva colección `pedidos-eventos` con los atributos:
   - `idPedido`
   - `payload`: contiene los atributos del evento que se publica en Pub/Sub:
     `idEvento`, `fechaEvento`, `idPedido`, `idCliente`, `estadoAnterior`, `estadoNuevo`.
   - `fechaCreacion`
   - `fechaActualizacion`
2. Usar una transacción que guarde de forma atómica en las colecciones `pedidos` y `pedidos-eventos`.
3. Implementar un worker que lea de la colección `pedidos-eventos` y publique los eventos en Pub/Sub.

## Evitar que un consumidor procese dos veces el mismo mensaje

Del lado del consumidor, considerando que los eventos tienen el campo único `idEvento`:

### Pasos

1. Crear una colección, por ejemplo `eventos_procesados`, con un índice único sobre el campo `idEvento`.
2. Según el tipo de tarea que realice el consumidor:
   - **Llamada a una API externa:** usar el `idEvento` como clave de idempotencia (si la API lo permite).
   - **Otro tipo de tarea:** guardar la operación a realizar en la colección `tareas_pendientes`,
     usando una transacción que escriba en ambas colecciones (`eventos_procesados` y `tareas_pendientes`).
     Después, otro worker toma la operación y la procesa (similar al Outbox).

### Acknowledge

| Situación | Acción |
|---|---|
| El procesamiento terminó correctamente | Hacer ack |
| El `idEvento` ya existe en `eventos_procesados` (duplicado) | Hacer ack (sin reprocesar) |
| Ocurrió un error | No hacer ack, para que Pub/Sub vuelva a enviar el evento |