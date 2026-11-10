import type { EstadoPedido } from './types'

export const TRANSICIONES: Readonly<Record<EstadoPedido, readonly EstadoPedido[]>> = {
  CREADO: ['PAGADO', 'CANCELADO'],
  PAGADO: ['EN_PREPARACION', 'CANCELADO'],
  EN_PREPARACION: ['LISTO_PARA_RECOGER'],
  LISTO_PARA_RECOGER: ['ENTREGADO'],
  ENTREGADO: [],
  CANCELADO: [],
}

export const ETIQUETA_ESTADO: Readonly<Record<EstadoPedido, string>> = {
  CREADO: 'Creado',
  PAGADO: 'Pagado',
  EN_PREPARACION: 'En preparación',
  LISTO_PARA_RECOGER: 'Listo para recoger',
  ENTREGADO: 'Entregado',
  CANCELADO: 'Cancelado',
}

export const ACCION_HACIA_ESTADO: Readonly<Record<EstadoPedido, string>> = {
  CREADO: 'Volver a creado',
  PAGADO: 'Marcar como pagado',
  EN_PREPARACION: 'Iniciar preparación',
  LISTO_PARA_RECOGER: 'Marcar listo para recoger',
  ENTREGADO: 'Marcar como entregado',
  CANCELADO: 'Cancelar pedido',
}