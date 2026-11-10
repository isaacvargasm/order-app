import { ETIQUETA_ESTADO } from '../transiciones'
import type { EstadoPedido } from '../types'

export function EstadoBadge({ estado }: { estado: EstadoPedido }) {
  return <span className={`badge badge-${estado.toLowerCase()}`}>{ETIQUETA_ESTADO[estado]}</span>
}