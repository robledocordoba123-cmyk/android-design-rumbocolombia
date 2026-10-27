package com.rumbocolombia.android.data.repository

import com.rumbocolombia.android.data.dto.Airport
import com.rumbocolombia.android.data.dto.City
import com.rumbocolombia.android.data.dto.Department
import com.rumbocolombia.android.data.dto.Region
import com.rumbocolombia.android.data.dto.TouristicAttraction

// Datos de ejemplo copiados de API Colombia (api-colombia.com/api/v1).
// Por ahora la app trabaja con esta lista local; en la siguiente fase
// el Repository los pedirá a la API con Retrofit.
object DatosDeEjemplo {

    val regiones = listOf(
        Region(1, "Caribe", "La región Caribe hospeda en sus costas la Sierra Nevada de Santa Marta y ciudades históricas como Cartagena."),
        Region(5, "Andina", "La región Andina es la de mayor desarrollo económico y social del país; en ella está la capital.")
    )

    val departamentos = listOf(
        Department(2, "Antioquia", "Su capital es Medellín, la segunda ciudad más poblada del país.", 6887306, 63612, 125, "4", 5, 12),
        Department(5, "Bogotá", "Bogotá, Distrito Capital, es la capital de la República de Colombia.", 8906342, 1139, 1, "1", 5, 167),
        Department(6, "Bolívar", "Su capital es Cartagena de Indias.", 2236603, 25978, 45, "5", 1, 210),
        Department(20, "Magdalena", "Su capital es Santa Marta.", 1463427, 23188, 30, "5", 1, 709),
        Department(25, "Quindío", "Departamento del Eje Cafetero; su capital es Armenia.", 569569, 1845, 12, "6", 5, 885)
    )

    val ciudades = listOf(
        City(12, "Medellín", "Capital del departamento de Antioquia.", 2533424, 382, "50001", 2),
        City(167, "Bogotá D.C.", "Capital de la República de Colombia.", 7901653, 1636, "110110", 5),
        City(210, "Cartagena", "Distrito Turístico y Cultural, capital de Bolívar.", 1028736, 609, "130000", 6),
        City(709, "Santa Marta", "Distrito Turístico, Cultural e Histórico, capital de Magdalena.", 538612, 2393, "470001", 20),
        City(885, "Armenia", "Capital del departamento del Quindío.", 316926, 121, "630001", 25),
        City(894, "Salento", "", null, null, "", 25) // En la API viene sin población ni superficie
    )

    val atracciones = listOf(
        TouristicAttraction(
            1, "Museo del Oro",
            "Museo del Banco de la República que conserva y expone piezas de orfebrería y alfarería de las culturas indígenas precolombinas de Colombia.",
            listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dc/BOG_Museo_del_Oro.JPG/800px-BOG_Museo_del_Oro.JPG"),
            4.601919, -74.072, 167
        ),
        TouristicAttraction(
            20, "Cerro de Monserrate",
            "El más conocido de los cerros Orientales de Bogotá, con una altitud de 3152 m sobre la cordillera Oriental.",
            listOf("https://www.colombia.co/wp-content/uploads/2018/04/shutterstock_645025201-1024x683.jpg"),
            4.605833, -74.056389, 167
        ),
        TouristicAttraction(
            10, "Castillo San Felipe de Barajas",
            "Fortificación construida en 1657 sobre el cerro San Lázaro, en Cartagena de Indias.",
            listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/71_-_Carthag%C3%A8ne_-_D%C3%A9cembre_2008.jpg/1024px-71_-_Carthag%C3%A8ne_-_D%C3%A9cembre_2008.jpg"),
            10.42222, -75.53806, 210
        ),
        TouristicAttraction(
            32, "Ciudad Amurallada",
            "Centro histórico de Cartagena de Indias, patrimonio nacional y Patrimonio de la Humanidad.",
            listOf("https://2.bp.blogspot.com/-zH6Y3DoW8zQ/UgZ1Pun_CwI/AAAAAAAABD4/KgvMRBexcJE/s640/DSC01928+(1).JPG"),
            10.423611, -75.525278, 210
        ),
        TouristicAttraction(
            9, "Parque Nacional Natural Tayrona",
            "Ubicado en las estribaciones de la Sierra Nevada de Santa Marta, a 34 km del centro urbano de la ciudad.",
            listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Cabo_San_Juan%2C_Colombia.jpg/800px-Cabo_San_Juan%2C_Colombia.jpg"),
            11.283333, -74.183333, 709
        ),
        TouristicAttraction(
            7, "Guatapé",
            "Municipio de la subregión Oriente de Antioquia, conocido por sus zócalos de colores y la Piedra del Peñol.",
            listOf("https://cdn.colombia.com/images/v2/turismo/sitios-turisticos/medellin/guatape-paisaje-antioquia-800.webp"),
            6.2325, -75.15861, 12
        ),
        TouristicAttraction(
            27, "Valle del Cocora",
            "Paisaje natural de la cordillera Central, en el Quindío, con poblaciones de la palma de cera, árbol nacional de Colombia.",
            listOf("https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Cocora_valley.JPG/800px-Cocora_valley.JPG"),
            4.641111, -75.541389, 894
        )
    )

    // En el JSON de aeropuertos la API trae latitude y longitude al revés;
    // aquí ya van corregidas.
    val aeropuertos = listOf(
        Airport(3, "Aeropuerto Internacional El Dorado", "BOG", "SKBO", "Internacional", 4.698402, -74.160003, 167),
        Airport(25, "Aeropuerto Enrique Olaya Herrera", "EOH", "SKMD", "Nacional", 6.220358, -75.590447, 12),
        Airport(9, "Aeropuerto Internacional José María Córdova", "MDE", "SKRG", "Internacional", 6.164533, -75.423110, 91),
        Airport(7, "Aeropuerto Internacional Rafael Núñez", "CTG", "SKCG", "Internacional", 10.444942, -75.515334, 203),
        Airport(12, "Aeropuerto Internacional Simón Bolívar", "SMR", "SKSM", "Internacional", 11.119716, -74.230800, 709),
        Airport(1, "Aeropuerto Internacional El Edén", "AXM", "SKAR", "Internacional", 4.453243, -75.765981, 885)
    )
}
