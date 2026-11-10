export const ESTADOS_PEDIDO = [
  'CREADO',
  'PAGADO',
  'EN_PREPARACION',
  'LISTO_PARA_RECOGER',
  'ENTREGADO',
  'CANCELADO',
] as const

export type EstadoPedido = (typeof ESTADOS_PEDIDO)[number]

export type Canal = 'APP' | 'WEB'

export interface LineaPedido {
  sku: string
  cantidad: number
  precio: number
}

export interface Pedido {
  id: string
  idCliente: string
  canal: Canal
  estado: EstadoPedido
  lineasPedido: LineaPedido[]
  total: number
  tiendaEntrega: string
  fechaCreacion: string
  fechaActualizacion: string
}

export interface CambioEstadoRespuesta {
  id: string
  estado: EstadoPedido
  fechaActualizacion: string
}

export interface FiltroPedidos {
  idCliente: string
  estado: EstadoPedido | null
}

export interface ErrorApiBody {
  codigo: string
  mensaje: string
  detalles?: Record<string, unknown>
}

export function esEstadoPedido(valor: string): valor is EstadoPedido {
  return (ESTADOS_PEDIDO as readonly string[]).includes(valor)
}