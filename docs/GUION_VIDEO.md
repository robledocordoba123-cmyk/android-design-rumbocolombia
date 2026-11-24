# 🎬 Guion del video · Rumbo Colombia

Duración máxima: **5 minutos**. Hablan los dos integrantes. Se graba la pantalla mostrando este repositorio en GitHub, el prototipo de Stitch y Android Studio.

| Tiempo | Quién habla | Qué se muestra | Qué se dice (idea principal) |
|---|---|---|---|
| 0:00 – 0:30 | **Manuela** | README, arriba (título y tabla de integrantes) | Saludo, nombres, ficha 3229209, grupo 2 y roles: Manuela líder y desarrolladora, Davier revisor de código. "Rumbo Colombia es una guía turística para el celular que funciona incluso sin señal." |
| 0:30 – 1:15 | **Manuela** | Sección 1 (pitch y API) | El problema (información repartida y sin señal en carretera) y la solución. Por qué es app móvil: GPS, mapas, modo sin internet. Por qué API Colombia: datos reales, en español, gratis y sin API key. |
| 1:15 – 2:00 | **Davier** | Sección 2 (diagrama ER) | Las 5 entidades: Región, Departamento, Ciudad, Atracción y Aeropuerto. Explicar las relaciones 1:N y la 1:1 de la capital. Mencionar los campos que pueden venir vacíos (`Int?`). |
| 2:00 – 3:00 | **Manuela** | Prototipo en Google Stitch (link de la sección 3) | Recorrer las 3 pantallas: Explorar → tocar una tarjeta → Detalle con "Cómo llegar" (aeropuerto más cercano) → Buscar. Decir que solo viaja el `attractionId` entre pantallas. |
| 3:00 – 3:45 | **Davier** | Sección 4 y sección 15 (arquitectura y tecnologías) | MVVM: UI → ViewModel → Repositorio → Retrofit → API, y Room como fuente local. Por qué cada tecnología (Compose, Retrofit, Room, Coil, navegación type-safe). La API no usa clave. |
| 3:45 – 4:30 | **Davier** | Android Studio con el emulador abierto | Mostrar las carpetas `data` y `presentation`. Correr la app: Explorar, Detalle y Buscar. Apagar el Wi‑Fi, reabrir la app y mostrar que los lugares salen desde Room. |
| 4:30 – 5:00 | **Manuela** | Pestaña *Pull requests* (cerrados) y sección 16 | Cómo trabajamos: una rama y un Pull Request por cambio, Davier revisa y aprueba antes de unir a `main`. Un reto y un aprendizaje (coordenadas al revés de la API, Room). Despedida. |

## Antes de grabar

- [ ] Abrir en pestañas: el README en GitHub, el prototipo de Stitch y Android Studio con el emulador ya encendido (para no esperar en el video).
- [ ] Cronometrar un ensayo: si pasa de 5 minutos, acortar las partes de los diagramas.
- [ ] Revisar que se escuche bien la voz de los dos.

## Cómo grabar y subir

1. Grabar con la grabadora de pantalla de Windows (`Win + Alt + R`), Loom o una llamada de Meet/Teams grabada con los dos.
2. Subir el video a **YouTube como "No listado"** o a **Google Drive** con acceso "Cualquier persona con el enlace".
3. Pegar el enlace en la sección 6 del README, donde dice *pendiente*.
