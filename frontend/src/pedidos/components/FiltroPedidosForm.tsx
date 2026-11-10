import { useState, type FormEvent } from 'react'
import { ETIQUETA_ESTADO } from '../transiciones'
import { ESTADOS_PEDIDO, esEstadoPedido, type EstadoPedido } from '../types'

interface Props {
  estado: EstadoPedido | null
  onBuscar: (idCliente: string) => void
  onCambiarEstado: (estado: EstadoPedido | null) => void
}

export function FiltroPedidosForm({ estado, onBuscar, onCambiarEstado }: Props) {
  const [idCliente, setIdCliente] = useState('')

  const enviar = (evento: FormEvent<HTMLFormElement>) => {
    evento.preventDefault()
    onBuscar(idCliente.trim())
  }

  return (
    <form className="filtros" onSubmit={enviar}>
      <label>
        ID de cliente
        <input
          value={idCliente}
          onChange={(e) => setIdCliente(e.target.value)}
          placeholder="CLI-98765"
          required
        />
      </label>
      <label>
        Estado
        <select
          value={estado ?? ''}
          onChange={(e) => onCambiarEstado(esEstadoPedido(e.target.value) ? e.target.value : null)}
        >
          <option value="">Todos</option>
          {ESTADOS_PEDIDO.map((valor) => (
            <option key={valor} value={valor}>
              {ETIQUETA_ESTADO[valor]}
            </option>
          ))}
        </select>
      </label>
      <button type="submit" className="primario">
        Buscar
      </button>
    </form>
  )
}