import { ApiError, CODIGO_CONCURRENCIA, CODIGO_TRANSICION_INVALIDA } from './api'

export interface ErrorCambioEstado {
  tipo: 'conflicto' | 'general'
  titulo: string
  mensaje: string
}

export function mensajeDeError(error: unknown): string {
  if (error instanceof ApiError) return error.message
  return 'Ocurrió un error inesperado.'
}

export function clasificarErrorCambio(error: unknown): ErrorCambioEstado {
  if (error instanceof ApiError && error.status === 409) {
    if (error.codigo === CODIGO_CONCURRENCIA) {
      return {
        tipo: 'conflicto',
        titulo: 'Otro usuario cambió este pedido',
        mensaje:
          'Otro usuario modificó este pedido y tu cambio no se guardó. ' +
          'Recarga el pedido para verlo actualizado.',
      }
    }
    if (error.codigo === CODIGO_TRANSICION_INVALIDA) {
      return {
        tipo: 'conflicto',
        titulo: 'El pedido ya cambió de estado',
        mensaje:
          'El estado que estás viendo ya no es el actual (otro usuario lo modificó), ' +
          'este cambio ya no es válido. Recarga el pedido para verlo actualizado.',
      }
    }
  }

  if (error instanceof ApiError && error.status === 404) {
    return { tipo: 'general', titulo: 'Pedido no encontrado', mensaje: 'Este pedido ya no existe.' }
  }

  return { tipo: 'general', titulo: 'No se pudo cambiar el estado', mensaje: mensajeDeError(error) }
}