import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError, actualizarEstadoPedido } from '../api'
import { AccionesEstado } from './AccionesEstado'

vi.mock('../api', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../api')>()),
  actualizarEstadoPedido: vi.fn(),
}))

const actualizarEstadoMock = vi.mocked(actualizarEstadoPedido)
const ID = '9b2d4f6a-1c3e-4a5b-8d7f-0e1a2b3c4d5e'

describe('AccionesEstado', () => {
  beforeEach(() => {
    actualizarEstadoMock.mockReset()
  })

  it('Muestra las transiciones válidas para el estado actual', () => {
    render(<AccionesEstado pedido={{ id: ID, estado: 'PAGADO' }} onCambiado={vi.fn()} onRecargar={vi.fn()} />)

    const botones = screen.getAllByRole('button').map((boton) => boton.textContent)
    expect(botones).toEqual(['Iniciar preparación', 'Cancelar pedido'])
    expect(screen.queryByRole('button', { name: 'Marcar como pagado' })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Marcar como entregado' })).not.toBeInTheDocument()
  })

  it('No se permiten transiciones cuando el pedido está en un estado final', () => {
    render(<AccionesEstado pedido={{ id: ID, estado: 'ENTREGADO' }} onCambiado={vi.fn()} onRecargar={vi.fn()} />)

    expect(screen.queryByRole('button')).not.toBeInTheDocument()
    expect(screen.getByText(/estado final/i)).toBeInTheDocument()
  })

  it('Envía la transición elegida y notifica el cambio', async () => {
    const usuario = userEvent.setup()
    const onCambiado = vi.fn()
    const respuesta = { id: ID, estado: 'PAGADO' as const, fechaActualizacion: '2026-11-09T10:00:00Z' }
    actualizarEstadoMock.mockResolvedValue(respuesta)

    render(<AccionesEstado pedido={{ id: ID, estado: 'CREADO' }} onCambiado={onCambiado} onRecargar={vi.fn()} />)
    await usuario.click(screen.getByRole('button', { name: 'Marcar como pagado' }))

    expect(actualizarEstadoMock).toHaveBeenCalledWith(ID, 'PAGADO')
    expect(onCambiado).toHaveBeenCalledWith(respuesta)
  })

  it('Permite recargar cuando la API responde 409 por concurrencia', async () => {
    const usuario = userEvent.setup()
    const onRecargar = vi.fn()
    const onCambiado = vi.fn()
    actualizarEstadoMock.mockRejectedValue(
      new ApiError(409, `El pedido ${ID} fue modificado por otro usuario, intente de nuevo`, 'CONCURRENCIA_PEDIDO'),
    )

    render(<AccionesEstado pedido={{ id: ID, estado: 'CREADO' }} onCambiado={onCambiado} onRecargar={onRecargar} />)
    await usuario.click(screen.getByRole('button', { name: 'Cancelar pedido' }))

    const alerta = await screen.findByRole('alert')
    expect(alerta).toHaveTextContent('Otro usuario cambió este pedido')
    expect(onCambiado).not.toHaveBeenCalled()

    expect(screen.getByRole('button', { name: 'Cancelar pedido' })).toBeEnabled()

    await usuario.click(screen.getByRole('button', { name: 'Recargar pedido' }))
    expect(onRecargar).toHaveBeenCalledTimes(1)
    expect(screen.queryByRole('alert')).not.toBeInTheDocument()
  })

  it('Transición inválida por un estado desactualizado', async () => {
    const usuario = userEvent.setup()
    actualizarEstadoMock.mockRejectedValue(new ApiError(409, 'Transición no válida', 'TRANSICION_INVALIDA'))

    render(<AccionesEstado pedido={{ id: ID, estado: 'CREADO' }} onCambiado={vi.fn()} onRecargar={vi.fn()} />)
    await usuario.click(screen.getByRole('button', { name: 'Marcar como pagado' }))

    expect(await screen.findByRole('alert')).toHaveTextContent('El pedido ya cambió de estado')
    expect(screen.getByRole('button', { name: 'Recargar pedido' })).toBeInTheDocument()
  })
})