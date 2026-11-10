import { useCambiarEstado } from '../hooks/useCambiarEstado'
import { ACCION_HACIA_ESTADO, TRANSICIONES } from '../transiciones'
import type { CambioEstadoRespuesta, Pedido } from '../types'

interface Props {
  pedido: Pick<Pedido, 'id' | 'estado'>
  /** Deshabilita las acciones desde fuera (p. ej. mientras se recarga el pedido). */
  deshabilitado?: boolean
  onCambiado: (respuesta: CambioEstadoRespuesta) => void
  onRecargar: () => void
}

export function AccionesEstado({ pedido, deshabilitado = false, onCambiado, onRecargar }: Props) {
  const { cambiarEstado, enviando, estadoSolicitado, error, limpiarError } = useCambiarEstado(
    pedido.id,
    onCambiado,
  )
  const destinos = TRANSICIONES[pedido.estado]

  if (destinos.length === 0) {
    return <p className="vacio">Este pedido está en un estado final y ya no se puede cambiar.</p>
  }

  return (
    <section aria-label="Acciones del pedido">
      <div className="acciones">
        {destinos.map((destino) => (
          <button
            key={destino}
            type="button"
            className={destino === 'CANCELADO' ? 'peligro' : 'primario'}
            disabled={enviando || deshabilitado}
            onClick={() => void cambiarEstado(destino)}
          >
            {enviando && estadoSolicitado === destino ? 'Guardando…' : ACCION_HACIA_ESTADO[destino]}
          </button>
        ))}
      </div>

      {error !== null && (
        <div role="alert" className={error.tipo === 'conflicto' ? 'alerta alerta-conflicto' : 'alerta'}>
          <p>
            <strong>{error.titulo}</strong>
          </p>
          <p>{error.mensaje}</p>
          {error.tipo === 'conflicto' && (
            <button
              type="button"
              onClick={() => {
                limpiarError()
                onRecargar()
              }}
            >
              Recargar pedido
            </button>
          )}
        </div>
      )}
    </section>
  )
}