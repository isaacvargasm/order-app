/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** URL base del backend. Vacío en desarrollo: el proxy de Vite redirige /pedidos a localhost:8080. */
  readonly VITE_API_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}