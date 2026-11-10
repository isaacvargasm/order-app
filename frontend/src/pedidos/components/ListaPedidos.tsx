import { mensajeDeError } from '../errores'
import { formatearFecha, formatearImporte } from '../formato'
import type { Consulta } from '../hooks/useConsulta'
import type { Pedido } from '../types'
import { EstadoBadge } from './EstadoBadge'

interface Props {
  consulta: Consulta<Pedido[]>
  hayCliente: boolean
  idSeleccionado: string | null
  onSeleccionar: (idPedido: string) => void
}

export function ListaPedidos({ consulta, hayCliente, idSeleccionado, onSeleccionar }: Props) {
  const { datos: pedidos, error, cargando, recargar } = consulta

  if (!hayCliente) {
    return <p className="vacio">Ingresa un ID de cliente para ver sus pedidos.</p>
  }

  if (error !== null && pedidos === null) {
    return (
      <div role="alert" className="alerta">
        <p>No se pudieron cargar los pedidos. {mensajeDeError(error)}</p>
        <button type="button" onClick={recargar}>
          Reintentar
        </button>
      </div>
    )
  }

  if (pedidos === null) {
    return <p role="status">Cargando pedidos...</p>
  }

  if (pedidos.length === 0) {
    return <p className="vacio">No hay pedidos para este filtro.</p>
  }

  return (
    <div>
      <p role="status" className="estado-carga">
        {cargando ? 'Actualizando...' : `${pedidos.length} pedido(s)`}
      </p>
      <ul className="lista-pedidos">
        {pedidos.map((pedido) => (
          <li key={pedido.id}>
            <button
              type="button"
              className="fila-pedido"
              aria-current={pedido.id === idSeleccionado ? 'true' : undefined}
              onClick={() => onSeleccionar(pedido.id)}
            >
              <span className="fila-id">{pedido.id.slice(0, 8)}</span>
              <EstadoBadge estado={pedido.estado} />
              <span className="fila-total">{formatearImporte(pedido.total)}</span>
              <span className="fila-fecha">{formatearFecha(pedido.fechaActualizacion)}</span>
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}