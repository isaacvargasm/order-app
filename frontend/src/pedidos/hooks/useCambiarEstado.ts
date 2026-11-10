import { useCallback, useRef, useState } from 'react'
import { actualizarEstadoPedido } from '../api'
import { clasificarErrorCambio, type ErrorCambioEstado } from '../errores'
import type { CambioEstadoRespuesta, EstadoPedido } from '../types'

export interface CambiarEstado {
  cambiarEstado: (destino: EstadoPedido) => Promise<void>
  enviando: boolean
  /** Estado al que se está intentando llevar el pedido (para el texto "Guardando…"). */
  estadoSolicitado: EstadoPedido | null
  error: ErrorCambioEstado | null
  limpiarError: () => void
}

export function useCambiarEstado(
  idPedido: string,
  onCambiado: (respuesta: CambioEstadoRespuesta) => void,
): CambiarEstado {
  // El ref bloquea el doble envío de inmediato; el estado `enviando` solo se refleja tras re-renderizar,
  // así que dos clics muy seguidos podrían colarse si dependiéramos solo de él.
  const enVuelo = useRef(false)
  const [enviando, setEnviando] = useState(false)
  const [estadoSolicitado, setEstadoSolicitado] = useState<EstadoPedido | null>(null)
  const [error, setError] = useState<ErrorCambioEstado | null>(null)

  const cambiarEstado = useCallback(
    async (destino: EstadoPedido) => {
      if (enVuelo.current) return
      enVuelo.current = true
      setEnviando(true)
      setEstadoSolicitado(destino)
      setError(null)

      let respuesta: CambioEstadoRespuesta
      try {
        respuesta = await actualizarEstadoPedido(idPedido, destino)
      } catch (e: unknown) {
        setError(clasificarErrorCambio(e))
        return
      } finally {
        enVuelo.current = false
        setEnviando(false)
        setEstadoSolicitado(null)
      }
      onCambiado(respuesta)
    },
    [idPedido, onCambiado],
  )

  const limpiarError = useCallback(() => setError(null), [])

  return { cambiarEstado, enviando, estadoSolicitado, error, limpiarError }
}