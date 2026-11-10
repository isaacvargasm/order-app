import type {
  CambioEstadoRespuesta,
  ErrorApiBody,
  EstadoPedido,
  FiltroPedidos,
  Pedido,
} from './types'

const BASE_URL = import.meta.env.VITE_API_URL ?? ''

export const CODIGO_CONCURRENCIA = 'CONCURRENCIA_PEDIDO'
export const CODIGO_TRANSICION_INVALIDA = 'TRANSICION_INVALIDA'

export class ApiError extends Error {
  readonly status: number
  readonly codigo: string | null

  constructor(status: number, mensaje: string, codigo: string | null) {
    super(mensaje)
    this.name = 'ApiError'
    this.status = status
    this.codigo = codigo
  }
}

function esErrorApiBody(valor: unknown): valor is ErrorApiBody {
  return (
    typeof valor === 'object' &&
    valor !== null &&
    'codigo' in valor &&
    typeof valor.codigo === 'string' &&
    'mensaje' in valor &&
    typeof valor.mensaje === 'string'
  )
}

async function solicitar<T>(ruta: string, init: RequestInit = {}): Promise<T> {
  let respuesta: Response
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      ...init,
      headers: {
        Accept: 'application/json',
        ...(init.body !== undefined ? { 'Content-Type': 'application/json' } : {}),
      },
    })
  } catch (error: unknown) {
    if (init.signal?.aborted) throw error
    throw new ApiError(0, 'No se pudo conectar con el servidor. Intentar de nuevo.', null)
  }

  const cuerpo: unknown = await respuesta.json().catch(() => null)

  if (!respuesta.ok) {
    if (esErrorApiBody(cuerpo)) {
      throw new ApiError(respuesta.status, cuerpo.mensaje, cuerpo.codigo)
    }
    throw new ApiError(respuesta.status, `El servidor respondió con un error (${respuesta.status}).`, null)
  }

  return cuerpo as T
}

export function listarPedidos(filtro: FiltroPedidos, signal?: AbortSignal): Promise<Pedido[]> {
  const params = new URLSearchParams({ idCliente: filtro.idCliente })
  if (filtro.estado !== null) params.set('estado', filtro.estado)
  return solicitar<Pedido[]>(`/pedidos?${params.toString()}`, { signal })
}

export function obtenerPedido(idPedido: string, signal?: AbortSignal): Promise<Pedido> {
  return solicitar<Pedido>(`/pedidos/${encodeURIComponent(idPedido)}`, { signal })
}

export function actualizarEstadoPedido(idPedido: string, estado: EstadoPedido): Promise<CambioEstadoRespuesta> {
  return solicitar<CambioEstadoRespuesta>(`/pedidos/${encodeURIComponent(idPedido)}/estado`, {
    method: 'PATCH',
    body: JSON.stringify({ estado }),
  })
}