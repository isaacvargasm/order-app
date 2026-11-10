import { useCallback, useEffect, useState } from 'react'

export interface Consulta<T> {
  datos: T | null
  error: unknown
  cargando: boolean
  recargar: () => void
}

type Resultado<T> =
  | { base: string; clave: string; datos: T; error: null }
  | { base: string; clave: string; datos: null; error: unknown }

export function useConsulta<T>(
  clave: string | null,
  cargar: (signal: AbortSignal) => Promise<T>,
): Consulta<T> {
  const [resultado, setResultado] = useState<Resultado<T> | null>(null)
  const [intento, setIntento] = useState(0)

  const claveActual = clave === null ? null : `${clave}#${intento}`

  useEffect(() => {
    if (clave === null || claveActual === null) return
    const controlador = new AbortController()

    cargar(controlador.signal).then(
      (datos) => {
        if (!controlador.signal.aborted) setResultado({ base: clave, clave: claveActual, datos, error: null })
      },
      (error: unknown) => {
        if (!controlador.signal.aborted) setResultado({ base: clave, clave: claveActual, datos: null, error })
      },
    )

    return () => controlador.abort()
  }, [clave, claveActual, cargar])

  const recargar = useCallback(() => setIntento((n) => n + 1), [])

  const esDeLaClave = resultado !== null && resultado.base === clave
  const esActual = resultado !== null && resultado.clave === claveActual

  return {
    datos: esDeLaClave ? resultado.datos : null,
    error: esActual ? resultado.error : null,
    cargando: claveActual !== null && !esActual,
    recargar,
  }
}