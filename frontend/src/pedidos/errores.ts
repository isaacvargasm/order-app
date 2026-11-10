import { ApiError, CODIGO_CONCURRENCIA, CODIGO_TRANSICION_INVALIDA } from './api'

export interface ErrorCambioEstado {
  /** 'conflicto': el pedido cambió en el servidor; la acción correcta es recargarlo. */
  tipo: 'conflicto' | 'general'
  titulo: string
  mensaje: string
}

export function mensajeDeError(error: unknown): string {
  if (error instanceof ApiError) return error.message
  return 'Ocurrió un error inesperado. Inténtalo de nuevo.'
}

export function clasificarErrorCambio(error: unknown): ErrorCambioEstado {
  if (error instanceof ApiError && error.status === 409) {
    if (error.codigo === CODIGO_CONCURRENCIA) {
      return {
        tipo: 'conflicto',
        titulo: 'Otro usuario cambió este pedido',
        mensaje:
          'Mientras lo revisabas, otra persona modificó este pedido y tu cambio no se guardó. ' +
          'Recarga el pedido para ver su estado actual y decide si aún quieres aplicar el cambio.',
      }
    }
    if (error.codigo === CODIGO_TRANSICION_INVALIDA) {
      // La pantalla solo ofrece transiciones válidas: si el backend la rechaza, el estado que
      // ve el usuario está desactualizado (otro usuario lo cambió antes).
      return {
        tipo: 'conflicto',
        titulo: 'El pedido ya cambió de estado',
        mensaje:
          'El estado que estás viendo ya no es el actual (probablemente otro usuario lo modificó), ' +
          'así que este cambio ya no es válido. Recarga el pedido para ver su estado actual.',
      }
    }
  }

  if (error instanceof ApiError && error.status === 404) {
    return { tipo: 'general', titulo: 'Pedido no encontrado', mensaje: 'Este pedido ya no existe.' }
  }

  return { tipo: 'general', titulo: 'No se pudo cambiar el estado', mensaje: mensajeDeError(error) }
}