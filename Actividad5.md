## Code Review

### Fragmento 1 (Java) — Cambio de estado

```
@Service
public class CambiarEstadoService {
    private final PedidoRepository repo;
    private final EventPublisher publisher

    public void cambiar(String pedidoId, EstadoPedido nuevo) {
        Pedido p = repo.findById(pedidoId).orElseThrow();
        publisher.publicar(new EstadoCambiado(pedidoId, p.getEstado(), nuevo));
        p.setEstado(nuevo);
        repo.save(p);
    }
}
```

En este ejemplo, mis observaciones son las siguientes: 
- Cuando el pedido no se encuentra por id, se debería lanzar una excepción específica de negocio que indique que no se encontró el pedido, p. ej. `PedidoNoEncontradoException`.
- El evento se está publicando antes de actualizar el estado en el pedido. Si hay un error al momento de guardar el pedido actualizado, los consumidores del evento reaccionarán a un pedido inconsistente.
- No se está validando el nuevo estado del pedido.
- Para mayor claridad, renombrar `p` a pedido. 

La secuencia debería ser la siguiente:

- Consultar el pedido. Lanzar `PedidoNoEncontradoException` si no se encontró.
- Setear el nuevo estado en el pedido con `pedido.cambiarEstado(nuevo)` para que el dominio valide si la transición es válida. 
- Guardar el pedido actualizado
- Publicar el evento con el nuevo estado
- Para resolver el posible issue de que falle la publicación del evento después de guardar el pedido, se podría implementar el patrón Transactional Outbox, donde en una sola transacción se guarde el estado del pedido y un nuevo registro que contenga el payload del evento a enviar, para que posteriormente otro proceso (worker) tome ese registro y envíe el evento a Pub/Sub o donde corresponda.


### Fragmento 2 (Java) — Consumidor de Pub/Sub. Hay clientes que reciben el mismo correo dos veces y otros que no lo reciben.

```
@Component
public class EstadoCambiadoListener {
    public void onMessage(BasicAcknowledgeablePubsubMessage msg) {
        EstadoCambiado e = parse(msg);
        msg.ack();
        notificaciones.enviarCorreo(e.clienteId(), e.pedidoId());
    }
}
```

Observaciones:
- Se está haciendo el ack antes de invocar a `notificaciones.enviarCorreo`. Si algo falla durante el envío del correo, el mensaje ya no se podrá procesar y por ello habrá clientes que no reciban el correo.
- No hay idempotencia en el consumidor, por ello, al recibir dos veces el mismo mensaje, el cliente puede recibir dos veces el mismo correo.
- Renombrar la variable `e` a `evento`

Propuesta de solución:

- El evento debería tener un eventId para que el consumidor pueda ser idempotente. Propongo crear una nueva colección para guardar los eventos recibidos: Si no existe el eventId se guarda, si ya existe, no se invoca a `notificaciones.enviarCorreo` y se puede hacer ack con seguridad.

```
@Component
public class EstadoCambiadoListener {
    private final NotificacionService notificaciones;
    private final EventosRecibidosRepository recibidosRepo;

    public void onMessage(BasicAcknowledgeablePubsubMessage msg) {
        EstadoCambiado evento;
        try {
            evento = parse(msg);
        } catch (InvalidMessageException ex) {
            log.error("Mensaje inválido {}", msg, ex);
            return;
        }

        try {
            if (recibidosRepo.existePorEventId(evento.eventId())) {
                msg.ack();
                return;
            }
            notificaciones.enviarCorreo(evento.clienteId(), evento.pedidoId());
            recibidosRepo.guardar(evento.eventId());
            msg.ack();
        } catch (Exception ex) {
            log.error("Fallo procesando evento {}", evento.eventId(), ex);
        }
    }
}
```

### Fragmento 3 (React) — Filtros. Al cambiar rápido de filtro, a veces la lista muestra pedidos que no corresponden al filtro seleccionado.

```
function usePedidos(filtros: Filtros) {
    const [pedidos, setPedidos] = useState<Pedido[]>([]);
    useEffect(() => {
        fetch(`/api/pedidos?estado=${filtros.estado}&cliente=${filtros.cliente}`)
            .then(r => r.json())
            .then(setPedidos);
    }, [filtros]);
    return pedidos;
}
```

Observaciones:
- Cada vez que se cambia el filtro, se lanza un nuevo fetch, por lo que las respuestas pueden llegar en cualquier orden.
- No se valida que la respuesta sea siempre 200 OK

Propuesta de solución:
- Validar la respuesta, si es diferente de 200 lanzar error
- Utilizar una bandera `ignorar=false` dentro del efecto para que cada ejecución tenga su bandera. Cuando finalice el efecto anterior, colocar `ignorar=true`. Los pedidos se actualizarán solamente cuando el flag esté en `false`.

```
useEffect(() => {
    let ignorar = false;

    fetch(`/api/pedidos?estado=${filtros.estado}&cliente=${filtros.cliente}`)
        .then(r => r.json())
        .then(data => {
            if (!ignorar) setPedidos(data);
        });

    return () => {
        ignorar = true;
    };
}
```

### Fragmento 4 (React) — Botón de avanzar. Los usuarios reportan pedidos que "saltan" dos estados y errores que aparecen como éxito.
```
function BotonAvanzar({ pedido, onCambio }: Props) {
    const [enviando, setEnviando] = useState(false);
    const avanzar = async () => {
        setEnviando(true);
        const r = await fetch(`/api/pedidos/${pedido.id}/estado`, {
            method: "PUT",
            body: JSON.stringify({ estado: siguiente(pedido.estado) }),
        });
        onCambio(await r.json());
        setEnviando(false);
    };
    return <button onClick={avanzar}>Avanzar</button>;
}
```

Observaciones:
- El botón nunca se deshabilita, lo que puede provocar que el usuario haga más de un clic y el estado se avance más de lo debido
- No se valida que la respuesta de la api sea 200 OK, por lo que un error puede ser tratado como éxito

Propuesta de solución:
- Agregar `disabled={enviando}` al botón
- Si la respuesta del api es diferente de 200 OK, lanzar error
```
if (!r.ok) {
    throw new Error("Hubo un error en el pedido.");
}
...
return <button onClick={avanzar} disabled={enviando}>Avanzar</button>;
```


## Guía técnica.


### Cómo se organiza el código en arquitectura hexagonal.

El código en el backend debe estar estructurado principalmente con las siguientes carpetas:

- `domain`. Debe contener los objetos de dominio: Aggregates, Entities, Value Objects y reglas de negocio. Así como excepciones y eventos requeridos.
- `application`. Puertos de entrada (casos de uso), puertos de salida (repositories, publishers) así como servicios (implementaciones de los casos de uso que utilizan los puertos de salida).
- `infrastructure`. Todo lo relacionado al framework i.e. Spring Boot. Adaptadores de entrada (controllers) que utilizan los casos de uso. Adaptadores de salida que implementan los puertos de salida (repositoriyAdapters, publisherAdapters) así como clases de configuración de beans de Spring.

Algunos aspectos no permitidos en arquitectura hexagonal son:
- Que `domain` dependa de `application` o `infrastructure`, o que `application` dependa de `infrastructure`. Las dependencias siempre apuntan hacia el dominio.
- Uso de anotaciones de frameworks (Spring Boot, JPA) en `domain` o en `application`
- Reglas de negocio en los controllers, listeners o adaptadores de salida
- Exponer el modelo de dominio directamente en la API REST
- Organizar el código por capas al estilo MVC tradicional

### Definition of Done. Qué se revisa en un pull request.

Para mí, un Definition of Done debe cumplir con lo siguiente:
- Pull request aprobado y mergeado
- Que se cumpla con los criterios de aceptación establecidos
- Contar con evidencia de funcionamiento correcto en ambientes bajos
- Dependiendo de los acuerdos realizados con el equipo, si el Definition of Done no requiere despliegue a producción, entonces se debe contar con todo lo necesario para poder desplegar a producción. 


Lo que siempre reviso en un pull request:
- Validación de datos de entrada
- Clean code
- Commit message descriptivo de la funcionalidad que se está agregando o actualizando
- Debe contar con suficientes pruebas unitarias
- No se deben introducir vulnerabilidades

### Cómo usar la IA en el día a día.

Qué se le puede delegar a la IA:
- Acelerar el proceso de desarrollo cuando el tiempo es limitado
- Generación de contratos OpenAPI y Asyncapi, siempre validando que lo que se haya generado sea correcto y esté alineado al negocio, seguridad, etc.
- Generación de documentación, por ejemplo, archivos markdown, diagramas de secuencia, documentación de servicios
- Generación de pruebas unitarias para acelerar el proceso de pruebas

Siempre debemos revisar:
- Que el código generado por IA esté alineado con la funcionalidad que se quiere implementar
- Que el código generado siga las mejores prácticas de seguridad, tanto de la organización como de la industria
- Que la IA no tenga alucinaciones
- Que la documentación que nos haya generado, esté alineada con el estatus del código y/o del proyecto

Qué es lo que no se acepta:
- Generar pull request con código generado 100% con IA sin revisar
- Delegar a la IA la toma de decisiones (por ejemplo, de negocio o para solucionar issues en Producción)
- Entregar datos sensibles como parte del input de la IA
