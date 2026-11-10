import { useCallback } from 'react'
import { listarPedidos, obtenerPedido } from '../api'
import type { FiltroPedidos, Pedido } from '../types'
import { useConsulta, type Consulta } from './useConsulta'

export function usePedidos({ idCliente, estado }: FiltroPedidos): Consulta<Pedido[]> {
  const cargar = useCallback(
    (signal: AbortSignal) => listarPedidos({ idCliente, estado }, signal),
    [idCliente, estado],
  )
  const clave = idCliente === '' ? null : `${idCliente}|${estado ?? 'TODOS'}`
  return useConsulta(clave, cargar)
}

export function usePedido(idPedido: string | null): Consulta<Pedido> {
  const cargar = useCallback(
    (signal: AbortSignal) => {
      if (idPedido === null) return Promise.reject(new Error('Sin pedido seleccionado'))
      return obtenerPedido(idPedido, signal)
    },
    [idPedido],
  )
  return useConsulta(idPedido, cargar)
}