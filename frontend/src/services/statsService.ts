export const registrarPartida = async (nombreJuego: string, puntos: number) => {
    try {
      await fetch(`http://localhost:8080/api/estadisticas/${encodeURIComponent(nombreJuego)}/registrar?bytesCambiados=${puntos}`, {
        method: 'PUT'
      });
    } catch (error) {
      console.error("Error al guardar estadística:", error);
    }
  };