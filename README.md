# order-app

Aplicación para manejo de pedidos.

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
| El procesamiento terminó correctamente | Hacer **ack** |
| El `idEvento` ya existe en `eventos_procesados` (duplicado) | Hacer **ack** (sin reprocesar) |
| Ocurrió un error | **No** hacer ack, para que Pub/Sub reentregue el evento |