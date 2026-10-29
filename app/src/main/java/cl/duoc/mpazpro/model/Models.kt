package cl.duoc.mpazpro.model

// Datos completamente ficticios para fines académicos.
enum class Subject(val label: String, val emoji: String) {
    LENGUAJE("Lenguaje y Comunicación", "📖"),
    MATEMATICA("Matemática", "🔢"),
    CIENCIAS("Ciencias Naturales", "🌱"),
    HISTORIA("Historia y Geografía", "🌎")
}

enum class UnitStatus(val label: String) {
    BORRADOR("Borrador"), AUTORIZADO("Autorizado"), PUBLICADO("Publicado"), DESHABILITADO("Deshabilitado")
}

data class DidacticUnit(
    val id: Int,
    val subject: Subject,
    val title: String,
    val objectiveCode: String,
    val objective: String,
    val explanation: String,
    val example: String,
    val question: String,
    val choices: List<String>,
    val correctChoice: Int,
    val imagePath: String = "",
    val status: UnitStatus = UnitStatus.BORRADOR
)

data class Assignment(val unitId: Int, val maxAttempts: Int, val assigned: Boolean)

data class Progress(
    val unitId: Int,
    val read: Boolean,
    val attempts: Int,
    val correctCount: Int,
    val lastChoice: Int,
    val lastEpoch: Long
)

data class AppState(
    val loaded: Boolean = false,
    val units: List<DidacticUnit> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val progress: List<Progress> = emptyList()
) {
    fun assignment(id: Int): Assignment? = assignments.firstOrNull { it.unitId == id && it.assigned }
    fun progress(id: Int): Progress = progress.firstOrNull { it.unitId == id } ?: Progress(id, false, 0, 0, -1, 0L)
}

data class UnitForm(
    val id: Int = 0,
    val subject: Subject = Subject.LENGUAJE,
    val title: String = "",
    val objectiveCode: String = "",
    val objective: String = "",
    val explanation: String = "",
    val example: String = "",
    val question: String = "",
    val choice1: String = "",
    val choice2: String = "",
    val choice3: String = "",
    val choice4: String = "",
    val correctChoice: Int = 0,
    val imagePath: String = ""
) {
    companion object {
        fun from(unit: DidacticUnit) = UnitForm(unit.id, unit.subject, unit.title,
            unit.objectiveCode, unit.objective, unit.explanation, unit.example,
            unit.question, unit.choices[0], unit.choices[1], unit.choices[2], unit.choices[3],
            unit.correctChoice, unit.imagePath)
    }

    fun toUnit(status: UnitStatus = UnitStatus.BORRADOR) = DidacticUnit(
        id, subject, title.trim(), objectiveCode.trim(), objective.trim(),
        explanation.trim(), example.trim(), question.trim(),
        listOf(choice1.trim(), choice2.trim(), choice3.trim(), choice4.trim()), correctChoice,
        imagePath, status
    )
}

data class UnitErrors(
    val title: String = "", val code: String = "", val objective: String = "",
    val explanation: String = "", val example: String = "", val question: String = "",
    val choices: String = ""
) {
    val isValid get() = listOf(title, code, objective, explanation, example, question, choices).all { it.isEmpty() }
}

object DemoContent {
    // Los OA son identificadores ficticios de demostración, no se atribuyen al MINEDUC.
    val units = listOf(
        DidacticUnit(1, Subject.LENGUAJE, "Comprendemos una historia", "DEMO-LEN-01",
            "Reconocer información explícita de un relato breve.",
            "Lee con calma: Ana tiene una bicicleta roja. Cada tarde va al parque con su papá.",
            "Ejemplo: Si preguntan de qué color es la bicicleta, buscamos la palabra 'roja'.",
            "¿De qué color es la bicicleta de Ana?", listOf("Azul", "Roja", "Verde", "Amarilla"), 1),
        DidacticUnit(2, Subject.MATEMATICA, "Sumamos hasta 20", "DEMO-MAT-01",
            "Resolver sumas simples con números naturales hasta 20.",
            "Para sumar, juntamos cantidades. Si tienes 7 lápices y agregas 5, obtienes 12.",
            "Ejemplo: 4 + 3 = 7, porque contamos cuatro y luego tres más.",
            "¿Cuánto es 8 + 6?", listOf("12", "13", "14", "16"), 2),
        DidacticUnit(3, Subject.CIENCIAS, "Seres vivos y su entorno", "DEMO-CIE-01",
            "Distinguir características generales de los seres vivos.",
            "Las plantas necesitan agua y luz para crecer. También son seres vivos.",
            "Ejemplo: Una semilla puede germinar y convertirse en una planta.",
            "¿Qué necesita una planta para crecer?", listOf("Solo una piedra", "Agua y luz", "Solo plástico", "Nada"), 1),
        DidacticUnit(4, Subject.HISTORIA, "Mi comunidad", "DEMO-HIS-01",
            "Reconocer servicios y lugares de la comunidad.",
            "En nuestra comunidad hay lugares donde las personas reciben ayuda, como la biblioteca y el consultorio.",
            "Ejemplo: A una biblioteca vamos a leer o pedir libros prestados.",
            "¿Dónde podemos pedir libros prestados?", listOf("Biblioteca", "Semáforo", "Estacionamiento", "Paradero"), 0)
    )
}
