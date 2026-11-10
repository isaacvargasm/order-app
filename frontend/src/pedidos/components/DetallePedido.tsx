import { useCallback, useState } from 'react'
import { mensajeDeError } from '../errores'
import { formatearFecha, formatearImporte } from '../formato'
import { usePedido } from '../hooks/usePedidos'
import { ETIQUETA_ESTADO } from '../transiciones'
import type { CambioEstadoRespuesta } from '../types'
import { AccionesEstado } from './AccionesEstado'
import { EstadoBadge } from './EstadoBadge'

interface Props {
  idPedido: string
  onPedidoActualizado: () => void
}

export function DetallePedido({ idPedido, onPedidoActualizado }: Props) {
  const { datos: pedido, error, cargando, recargar } = usePedido(idPedido)
  const [aviso, setAviso] = useState<string | null>(null)

  const manejarCambio = useCallback(
    (respuesta: CambioEstadoRespuesta) => {
      setAviso(`Estado actualizado a '${ETIQUETA_ESTADO[respuesta.estado]}'.`)
      recargar()
      onPedidoActualizado()
    },
    [recargar, onPedidoActualizado],
  )

  const manejarRecarga = useCallback(() => {
    setAviso(null)
    recargar()
    onPedidoActualizado()
  }, [recargar, onPedidoActualizado])

  if (error !== null && pedido === null) {
    return (
      <div role="alert" className="alerta">
        <p>No se pudo cargar el pedido. {mensajeDeError(error)}</p>
        <button type="button" onClick={recargar}>
          Reintentar
        </button>
      </div>
    )
  }

  if (pedido === null) {
    return <p role="status">Cargando pedido...</p>
  }

  return (
    <article className="detalle" aria-busy={cargando}>
      <header className="detalle-cabecera">
        <h2>Pedido {pedido.id.slice(0, 8)}</h2>
        <EstadoBadge estado={pedido.estado} />
      </header>

      <dl className="datos">
        <dt>ID</dt>
        <dd className="mono">{pedido.id}</dd>
        <dt>Cliente</dt>
        <dd>{pedido.idCliente}</dd>
        <dt>Canal</dt>
        <dd>{pedido.canal}</dd>
        <dt>Tienda de entrega</dt>
        <dd>{pedido.tiendaEntrega}</dd>
        <dt>Creado</dt>
        <dd>{formatearFecha(pedido.fechaCreacion)}</dd>
        <dt>Actualizado</dt>
        <dd>{formatearFecha(pedido.fechaActualizacion)}</dd>
      </dl>

      <table className="lineas">
        <thead>
          <tr>
            <th scope="col">SKU</th>
            <th scope="col">Cantidad</th>
            <th scope="col">Precio</th>
            <th scope="col">Subtotal</th>
          </tr>
        </thead>
        <tbody>
          {pedido.lineasPedido.map((linea) => (
            <tr key={linea.sku}>
              <td>{linea.sku}</td>
              <td>{linea.cantidad}</td>
              <td>{formatearImporte(linea.precio)}</td>
              <td>{formatearImporte(linea.precio * linea.cantidad)}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <th scope="row" colSpan={3}>
              Total
            </th>
            <td>{formatearImporte(pedido.total)}</td>
          </tr>
        </tfoot>
      </table>

      <p role="status" className="aviso">
        {aviso}
      </p>

      <AccionesEstado
        key={`${pedido.id}-${pedido.estado}`}
        pedido={pedido}
        deshabilitado={cargando}
        onCambiado={manejarCambio}
        onRecargar={manejarRecarga}
      />
    </article>
  )
}