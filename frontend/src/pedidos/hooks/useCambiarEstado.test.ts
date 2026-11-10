import { act, renderHook, waitFor } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError, actualizarEstadoPedido } from '../api'
import type { CambioEstadoRespuesta } from '../types'
import { useCambiarEstado } from './useCambiarEstado'

vi.mock('../api', async (importOriginal) => ({
  ...(await importOriginal<typeof import('../api')>()),
  actualizarEstadoPedido: vi.fn(),
}))

const actualizarEstadoMock = vi.mocked(actualizarEstadoPedido)
const ID = '9b2d4f6a-1c3e-4a5b-8d7f-0e1a2b3c4d5e'
const RESPUESTA: CambioEstadoRespuesta = { id: ID, estado: 'PAGADO', fechaActualizacion: '2026-11-09T10:00:00Z' }

describe('useCambiarEstado', () => {
  beforeEach(() => {
    actualizarEstadoMock.mockReset()
  })

  it('evita el doble envío mientras hay una petición en curso', async () => {
    let resolver: (valor: CambioEstadoRespuesta) => void = () => {}
    actualizarEstadoMock.mockReturnValue(
      new Promise<CambioEstadoRespuesta>((resolve) => {
        resolver = resolve
      }),
    )
    const onCambiado = vi.fn()
    const { result } = renderHook(() => useCambiarEstado(ID, onCambiado))

    // Dos llamadas en el mismo tick (doble clic antes de que React re-renderice).
    let primera: Promise<void> = Promise.resolve()
    act(() => {
      primera = result.current.cambiarEstado('PAGADO')
      void result.current.cambiarEstado('PAGADO')
    })

    expect(result.current.enviando).toBe(true)
    expect(result.current.estadoSolicitado).toBe('PAGADO')
    expect(actualizarEstadoMock).toHaveBeenCalledTimes(1)

    await act(async () => {
      resolver(RESPUESTA)
      await primera
    })

    expect(result.current.enviando).toBe(false)
    expect(onCambiado).toHaveBeenCalledTimes(1)
    expect(onCambiado).toHaveBeenCalledWith(RESPUESTA)
  })

  it('permite reintentar después de un error y expone el conflicto 409', async () => {
    actualizarEstadoMock.mockRejectedValueOnce(new ApiError(409, 'Modificado por otro usuario', 'CONCURRENCIA_PEDIDO'))
    actualizarEstadoMock.mockResolvedValueOnce(RESPUESTA)
    const onCambiado = vi.fn()
    const { result } = renderHook(() => useCambiarEstado(ID, onCambiado))

    await act(() => result.current.cambiarEstado('PAGADO'))

    expect(result.current.error?.tipo).toBe('conflicto')
    expect(result.current.enviando).toBe(false)
    expect(onCambiado).not.toHaveBeenCalled()

    await act(() => result.current.cambiarEstado('PAGADO'))

    await waitFor(() => expect(onCambiado).toHaveBeenCalledWith(RESPUESTA))
    expect(result.current.error).toBeNull()
    expect(actualizarEstadoMock).toHaveBeenCalledTimes(2)
  })
})