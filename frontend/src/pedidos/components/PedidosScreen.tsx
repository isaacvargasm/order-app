import { useState } from 'react'
import { usePedidos } from '../hooks/usePedidos'
import type { FiltroPedidos } from '../types'
import { DetallePedido } from './DetallePedido'
import { FiltroPedidosForm } from './FiltroPedidosForm'
import { ListaPedidos } from './ListaPedidos'

export function PedidosScreen() {
  const [filtro, setFiltro] = useState<FiltroPedidos>({ idCliente: '', estado: null })
  const [idSeleccionado, setIdSeleccionado] = useState<string | null>(null)
  const pedidos = usePedidos(filtro)

  return (
    <main className="pantalla">
      <h1>Operación de pedidos</h1>

      <FiltroPedidosForm
        estado={filtro.estado}
        onBuscar={(idCliente) => {
          setFiltro((actual) => ({ ...actual, idCliente }))
          setIdSeleccionado(null)
        }}
        onCambiarEstado={(estado) => setFiltro((actual) => ({ ...actual, estado }))}
      />

      <div className="columnas">
        <section aria-label="Lista de pedidos" className="panel">
          <ListaPedidos
            consulta={pedidos}
            hayCliente={filtro.idCliente !== ''}
            idSeleccionado={idSeleccionado}
            onSeleccionar={setIdSeleccionado}
          />
        </section>

        <section aria-label="Detalle del pedido" className="panel">
          {idSeleccionado === null ? (
            <p className="vacio">Selecciona un pedido para ver su detalle.</p>
          ) : (
            <DetallePedido key={idSeleccionado} idPedido={idSeleccionado} onPedidoActualizado={pedidos.recargar} />
          )}
        </section>
      </div>
    </main>
  )
}