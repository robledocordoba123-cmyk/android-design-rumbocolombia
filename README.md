# 🧭 Rumbo Colombia

**Tu guía para recorrer Colombia, incluso sin señal.**

Diseño y arquitectura de una aplicación móvil Android que muestra los atractivos turísticos de Colombia organizados por región, departamento y ciudad, consumiendo la API pública [API Colombia](https://api-colombia.com).

> Proyecto Integrador · Aplicaciones Móviles Android · SENA, Tecnología en Análisis y Desarrollo de Software · Ficha 3229209 · Grupo 2
>
> Este repositorio contiene **solo el diseño** de la app y el **proyecto base** generado por Android Studio. Todavía no tiene código de la aplicación: todo lo que se planificó está documentado en este README.

## Integrantes

| Integrante | Rol en el equipo |
|---|---|
| Manuela Córdoba Robledo | Líder del equipo |
| Davier Andrés Quinto Bejarano | Integrante |

## Contenido

1. [Idea y API elegida](#1-idea-y-api-elegida) (Hito 1)
2. [Modelo de datos](#2-modelo-de-datos) (Hito 2)
3. [Pantallas y flujo de navegación](#3-pantallas-y-flujo-de-navegación) (Hito 3)
4. Arquitectura y endpoints (Hito 4) · *pendiente*
5. Proyecto base en Android Studio (Hito 5) · *pendiente*
6. Video de presentación (Hito 6) · *pendiente*
7. Cómo trabajamos (GitHub Flow) · *pendiente*

---

## 1. Idea y API elegida

### Elevator pitch

**Rumbo Colombia** es una guía turística para el celular que ayuda a viajeros nacionales y extranjeros a descubrir los atractivos turísticos de Colombia, organizados por **región, departamento y ciudad**.

**El problema:** la información turística está repartida en muchas páginas, y en las carreteras y los pueblos casi nunca hay buena señal. El viajero termina sin saber qué visitar ni cómo llegar.

**La solución:** una app que muestra cada atracción con fotos, descripción y ubicación en el mapa, indica el **aeropuerto más cercano** para planear la llegada y **guarda la información en el celular** para consultarla sin internet.

### ¿Por qué una app móvil y no una página web?

1. **El viajero está en movimiento.** El celular es lo único que lleva siempre consigo durante el viaje.
2. **Ubicación y mapas.** Cada atracción tiene latitud y longitud, así que la app puede abrirla en el mapa del celular y calcular qué tan cerca queda.
3. **Sin señal no hay web.** En zonas rurales la conexión falla; la app guarda la región que el usuario va a visitar para consultarla offline.
4. **Avisos.** Una app puede recordarle al viajero los lugares que guardó, algo que una página web no hace bien.

### API elegida: API Colombia

| | |
|---|---|
| **Nombre** | API Colombia |
| **URL base** | `https://api-colombia.com/api/v1/` |
| **Documentación** | [api-colombia.com/swagger](https://api-colombia.com/swagger) |
| **Autenticación** | No necesita API key: es pública y gratuita |
| **Formato** | JSON, con los textos en español |

**Por qué la elegimos:** tiene datos reales de Colombia, en español y sin costo, y sus recursos están relacionados entre sí (una región tiene departamentos, un departamento tiene ciudades y una ciudad tiene atracciones y aeropuertos). Eso nos permite diseñar un modelo de datos completo y una navegación natural de lo general a lo particular.

### Recursos de la API que usará la app

| Recurso | Endpoint base | Cantidad aprox. | Para qué lo usa la app |
|---|---|---|---|
| **Región** | `/Region` | 6 | Explorar el país por regiones (Caribe, Pacífico, Andina…) |
| **Departamento** | `/Department` | 33 | Agrupar las ciudades de cada región |
| **Ciudad** | `/City` | 1.123 | Ubicar cada atracción y buscar destinos por nombre |
| **Atracción turística** | `/TouristicAttraction` | 41 | Lista principal y detalle: fotos, descripción y coordenadas |
| **Aeropuerto** | `/Airport` | 52 | Mostrar el aeropuerto más cercano para llegar al destino |

*Cantidades consultadas en la API el 19 de octubre de 2026.*

---

## 2. Modelo de datos

Diagrama entidad-relación de los 5 recursos que llegan de API Colombia, con los atributos que usará la app. Los nombres de las entidades y los campos son los mismos del JSON de la API, para que el mapeo a las clases de Kotlin sea directo.

```mermaid
erDiagram
    REGION ||--|{ DEPARTMENT : "agrupa"
    DEPARTMENT ||--|{ CITY : "tiene"
    DEPARTMENT |o--|| CITY : "tiene como capital"
    CITY ||--o{ TOURISTIC_ATTRACTION : "tiene"
    CITY ||--o{ AIRPORT : "tiene"

    REGION {
        int id PK
        string name "Caribe, Pacífico, Andina..."
        string description
    }

    DEPARTMENT {
        int id PK
        string name
        string description
        int population "habitantes"
        int surface "km²"
        int municipalities "número de municipios"
        string phonePrefix "indicativo telefónico"
        int regionId FK "Región a la que pertenece"
        int cityCapitalId FK "Ciudad capital"
    }

    CITY {
        int id PK
        string name
        string description
        int population "puede venir vacío"
        int surface "km², puede venir vacío"
        string postalCode
        int departmentId FK
    }

    TOURISTIC_ATTRACTION {
        int id PK
        string name
        string description
        string[] images "URLs de las fotos"
        double latitude
        double longitude
        int cityId FK
    }

    AIRPORT {
        int id PK
        string name
        string iataCode "código de 3 letras, ej. BOG"
        string oaciCode "código de 4 letras, ej. SKBO"
        string type "Nacional, Internacional, Militar..."
        double latitude
        double longitude
        int cityId FK
    }
```

### Relaciones

| Relación | Tipo | Cómo se lee |
|---|---|---|
| Región → Departamento | **1 : N** | Una región agrupa uno o varios departamentos; cada departamento pertenece a una sola región (`regionId`). |
| Departamento → Ciudad | **1 : N** | Un departamento tiene una o varias ciudades; cada ciudad pertenece a un solo departamento (`departmentId`). |
| Departamento → Ciudad capital | **1 : 1** | Cada departamento tiene exactamente una ciudad capital (`cityCapitalId`); una ciudad es capital de máximo un departamento. |
| Ciudad → Atracción turística | **1 : N** | Una ciudad puede tener cero o varias atracciones; cada atracción queda en una sola ciudad (`cityId`). |
| Ciudad → Aeropuerto | **1 : N** | Una ciudad puede tener cero o varios aeropuertos; cada aeropuerto queda en una sola ciudad (`cityId`). |

### Decisiones del modelo

- **Solo los campos que la app necesita.** La API trae más información (presidentes, emisoras, reservas indígenas…), pero se dejó por fuera porque ninguna pantalla la usa.
- **Coordenadas como `double`.** Atracciones y aeropuertos traen `latitude` y `longitude`; con ellas la app abre el mapa y calcula cuál es el aeropuerto más cercano a cada atracción.
- **Campos que pueden venir vacíos.** La mayoría de las ciudades (1.060 de 1.123) no traen `population` ni `surface`; en Kotlin se modelarán como tipos *nullable* (`Int?`) y la pantalla solo los mostrará cuando existan, para que la app no falle.
- **Un detalle de la API:** en el JSON de aeropuertos el campo del departamento se llama `deparmentId` (sin la "t"). La app usará el `cityId`, que es el dato confiable, y llega al departamento a través de la ciudad.

---

## 3. Pantallas y flujo de navegación

Las pantallas se diseñaron en **Google Stitch** como un prototipo interactivo: se puede tocar una tarjeta para abrir el detalle, usar la barra inferior para cambiar de pantalla y volver atrás.

🔗 **Prototipo en Google Stitch:** [stitch.withgoogle.com/projects/10235196442344271062](https://stitch.withgoogle.com/projects/10235196442344271062)

**Sistema de diseño:** Material Design 3 con los colores de la bandera de Colombia usados como paleta: azul profundo como color principal, amarillo como acento y rojo para estados importantes, sobre fondo claro.

### Flujo de navegación

```mermaid
flowchart LR
    A["🧭 Explorar<br/>(lista de atracciones)"] -- "toca una tarjeta<br/>viaja: attractionId" --> B["📍 Detalle de la atracción"]
    C["🔍 Buscar<br/>(ciudades y atracciones)"] -- "toca una atracción<br/>viaja: attractionId" --> B
    A -- "barra inferior: Buscar" --> C
    C -- "barra inferior: Explorar" --> A
    B -- "botón volver" --> A
    B -- "botón volver" --> C
    B -- "Abrir en Maps<br/>viaja: latitude, longitude" --> M["🗺️ App de mapas del celular"]
```

El único dato que viaja entre pantallas es el **`attractionId`**: la pantalla de detalle lo recibe y con él pide la información completa a la API. Así cada pantalla es independiente y el detalle se puede abrir desde Explorar o desde Buscar.

### Pantalla 1 · Explorar (lista principal)

<img src="docs/stitch/01-explorar.png" alt="Pantalla Explorar" width="300">

| | |
|---|---|
| **Qué muestra** | Las atracciones turísticas en tarjetas con foto, nombre, ciudad y departamento. Arriba, una barra de búsqueda rápida y chips para filtrar por región. Cada tarjeta indica si ya está guardada para ver sin internet. |
| **De dónde salen los datos** | `GET /TouristicAttraction` (lista con la ciudad de cada atracción). `GET /Department` y `GET /Region` para mostrar el departamento y filtrar por región. |
| **Acciones del usuario** | Tocar una tarjeta → abre el **Detalle**. Tocar un chip de región → filtra la lista. Tocar el ícono de guardar → la guarda en el celular. Barra inferior → cambia a **Buscar**. |
| **Datos que envía** | `attractionId` al Detalle. |

### Pantalla 2 · Detalle de la atracción

<img src="docs/stitch/02-detalle.png" alt="Pantalla Detalle" width="300"> <img src="docs/stitch/02-detalle-como-llegar.png" alt="Detalle: mapa y cómo llegar" width="300">

| | |
|---|---|
| **Qué muestra** | Foto grande, nombre, ciudad, departamento y región, descripción completa, un mapa con la ubicación y la tarjeta **"Cómo llegar"** con el aeropuerto más cercano (nombre, código IATA, tipo y distancia en km). |
| **De dónde salen los datos** | `GET /TouristicAttraction/{id}` (detalle con fotos y coordenadas). `GET /Airport` para calcular el aeropuerto más cercano a partir de la latitud y longitud de la atracción y de cada aeropuerto. |
| **Acciones del usuario** | Volver → regresa a la pantalla anterior. **Abrir en Maps** → abre la app de mapas del celular con las coordenadas. **Guardar sin internet** → guarda la atracción en el celular. Compartir. |
| **Datos que recibe** | `attractionId`. |

### Pantalla 3 · Buscar

<img src="docs/stitch/03-buscar.png" alt="Pantalla Buscar" width="300">

| | |
|---|---|
| **Qué muestra** | Un campo de búsqueda grande, filtros por región y departamento, y los resultados separados en **ciudades** (con departamento, población cuando existe y código postal) y **atracciones** (con foto pequeña). |
| **De dónde salen los datos** | `GET /City/search/{texto}` y `GET /TouristicAttraction/search/{texto}`. Para el filtro por departamento: `GET /Region/{id}/departments`. |
| **Acciones del usuario** | Escribir → busca ciudades y atracciones. Elegir región o departamento → filtra. Tocar una atracción → abre el **Detalle**. Barra inferior → vuelve a **Explorar**. |
| **Datos que envía** | `attractionId` al Detalle. |

### Notas del prototipo

- **Todo lo que muestran las pantallas sale de API Colombia**, menos lo que calcula la app (la distancia al aeropuerto y el espacio que ocupa lo guardado en el celular). Al diseñar se quitaron a propósito las estrellas, reseñas, precios y audioguías que Stitch proponía, porque la API no tiene esos datos.
- Algunos textos de apoyo del prototipo, como las indicaciones de transporte terrestre en "Cómo llegar" o etiquetas como "Mejor temporada", son de ejemplo visual. En la app se reemplazarán por datos de la API o no se mostrarán.
- La tercera opción de la barra inferior, **Guardados**, queda planeada para una siguiente versión: mostraría las atracciones guardadas para ver sin internet.
