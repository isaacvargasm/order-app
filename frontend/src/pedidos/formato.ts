const formatoImporte = new Intl.NumberFormat('es', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const formatoFecha = new Intl.DateTimeFormat('es', { dateStyle: 'medium', timeStyle: 'short' })

export function formatearImporte(valor: number): string {
  return formatoImporte.format(valor)
}

export function formatearFecha(iso: string): string {
  const fecha = new Date(iso)
  return Number.isNaN(fecha.getTime()) ? iso : formatoFecha.format(fecha)
}