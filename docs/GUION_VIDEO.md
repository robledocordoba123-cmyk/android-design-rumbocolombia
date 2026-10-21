# 🎬 Guion del video · Rumbo Colombia

Duración máxima: **5 minutos**. Hablan los dos integrantes. Se graba la pantalla mostrando este repositorio en GitHub, el prototipo de Stitch y Android Studio.

| Tiempo | Quién habla | Qué se muestra | Qué se dice (idea principal) |
|---|---|---|---|
| 0:00 – 0:30 | **Manuela** | README, arriba (título y tabla de integrantes) | Saludo, nombres, ficha 3229209, grupo 2. "Rumbo Colombia es una guía turística para el celular que funciona incluso sin señal." |
| 0:30 – 1:15 | **Manuela** | Sección 1 (pitch y API) | El problema (información repartida y sin señal en carretera) y la solución. Por qué es app móvil: GPS, mapas, modo sin internet. Por qué API Colombia: datos reales, en español, gratis y sin API key. |
| 1:15 – 2:00 | **Davier** | Sección 2 (diagrama ER) | Las 5 entidades: Región, Departamento, Ciudad, Atracción y Aeropuerto. Explicar las relaciones 1:N y la 1:1 de la capital. Mencionar los campos que pueden venir vacíos (`Int?`). |
| 2:00 – 3:00 | **Manuela** | Prototipo en Google Stitch (link de la sección 3) | Recorrer las 3 pantallas: Explorar → tocar una tarjeta → Detalle con "Cómo llegar" (aeropuerto más cercano) → Buscar. Decir que solo viaja el `attractionId` entre pantallas. |
| 3:00 – 4:00 | **Davier** | Sección 4 (diagrama de arquitectura y tabla de endpoints) | MVVM: UI → ViewModel → Repositorio → Retrofit → API. Corrutinas, estados Loading / Success / Error, Room para ver sin señal, Coil para las fotos. La API no usa clave; si un día se usa una, va en `local.properties`. |
| 4:00 – 4:40 | **Davier** | Android Studio con el emulador abierto | Ejecutar el proyecto base: aparece "Hello Android!". Aclarar que el proyecto se dejó sin código propio, como pide la actividad. |
| 4:40 – 5:00 | **Manuela** | Pestaña *Pull requests* (cerrados) de GitHub | Cómo trabajamos: una rama y un Pull Request por hito, Davier revisa y aprueba antes de unir a `main`. Despedida. |

## Antes de grabar

- [ ] Abrir en pestañas: el README en GitHub, el prototipo de Stitch y Android Studio con el emulador ya encendido (para no esperar en el video).
- [ ] Cronometrar un ensayo: si pasa de 5 minutos, acortar las partes de los diagramas.
- [ ] Revisar que se escuche bien la voz de los dos.

## Cómo grabar y subir

1. Grabar con la grabadora de pantalla de Windows (`Win + Alt + R`), Loom o una llamada de Meet/Teams grabada con los dos.
2. Subir el video a **YouTube como "No listado"** o a **Google Drive** con acceso "Cualquier persona con el enlace".
3. Pegar el enlace en la sección 6 del README, donde dice *pendiente*.
