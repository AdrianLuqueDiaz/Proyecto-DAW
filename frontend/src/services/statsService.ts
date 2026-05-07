const API_URL = import.meta.env.VITE_API_BASE_URL

export const registrarPartida = async (nombreJuego: string, puntos: number) => {
    try {
      await fetch(`${API_URL}/estadisticas/${encodeURIComponent(nombreJuego)}/registrar?bytesCambiados=${puntos}`, {
        method: 'PUT'
      });
    } catch (error) {
      console.error("Error al guardar estadística:", error);
    }
  };