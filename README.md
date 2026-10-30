# MPAZ PRO 2.º – Evaluación Parcial 2 (DSY1105)

**Proyecto Android Studio / Kotlin / Jetpack Compose / MVVM / DataStore Preferences.**

Prototipo didáctico local para segundo básico, basado en el caso *Escuela Escritora Marcela Paz*.
Todos los alumnos, docentes, identificadores y objetivos con código `DEMO-` son ficticios.

> **Estado:** Código fuente integrado en la rama `mvnuuu`. No se ha compilado ni ejecutado aquí porque este entorno de trabajo no tiene SDK Android ni emulador. Se requiere validar compilación y flujos en Android Studio antes de entregar.

## Configurar y ejecutar

1. Abrir Android Studio y elegir **Open**, seleccionando esta carpeta (contiene `settings.gradle.kts`).
2. Esperar la sincronización de Gradle con internet. Instalar SDK Platform **Android API 36** si Android Studio lo solicita.
3. Para AGP 8.x configurar **Gradle JDK 17**. La versión de Gradle Wrapper es 8.13; la compatibilidad de código Java es 11.
4. Crear un dispositivo virtual Android API **33 o superior**, o conectar un equipo Android físico.
5. Ejecutar módulo **app**. Para probar cámara y galería, se recomienda un dispositivo físico con aplicaciones disponibles.

## Flujo de demostración recomendado

1. **UTP:** ingresar al perfil UTP. Las cuatro unidades demo empiezan en **Borrador**. Autorizar una unidad y luego publicarla. También se puede crear una unidad completando título, OA, explicación, ejemplo, pregunta, cuatro alternativas y alternativa correcta. Si se edita, vuelve a Borrador.
2. **UTP/recursos nativos:** editar una unidad y añadir una imagen didáctica propia desde la **galería** o capturar una fotografía mediante la **cámara**. Guardar la unidad y volver a autorizar/publicar. El archivo se copia al almacenamiento privado de la app.
3. **Docente:** ingresar al perfil docente; se muestran únicamente las unidades publicadas. Elegir unidad, ingresar límite de intentos entre 1 y 5 y asignar al usuario ficticio `EST-001`.
4. **Estudiante:** ingresar como `EST-001`. Ver únicamente unidades publicadas y asignadas, leer el contenido, marcarlo como leído, contestar el desafío y ver la retroalimentación. La app impide seguir respondiendo una vez alcanzado el límite de intentos.
5. **Docente:** consultar **Informe pedagógico** para ver lectura completada, intentos, aciertos y último envío por unidad/OA.
6. Cerrar totalmente la aplicación y abrirla nuevamente; las publicaciones, asignaciones y avances deben conservarse gracias a **DataStore**.

## Estructura

- `model/`: modelos, estado y contenido educativo de prueba.
- `repository/`: DataStore Preferences, lectura y guardado local; importación de imagen.
- `viewmodel/`: reglas, validaciones y lógica de aplicación.
- `ui/screens/`: selección de perfil, UTP, formularios, docente, informes y estudiante.
- `ui/components/`: componentes reutilizables, mensajes de validación y animación.
- `navigation/`: `NavHost` y rutas de la aplicación.

## Alcance y limitaciones de EP2

**Incluye a nivel de código:** cuatro asignaturas, aprobación UTP, publicación, asignación, perfil de estudiante ficticio, lecturas, actividades de selección, control de intentos, informe resumido, formularios validados, feedback animado, cámara y galería, guardado local.

**No incluye:** autenticación real, múltiples cuentas simultáneas, sincronización entre teléfonos, API REST, SQLite/Room, audio instruccional, reportes PDF exportables, historial de cambios/aprobaciones, edición de pruebas con varias preguntas, calendario de habilitación, Trello ni APK firmado. No interpretar la UI de perfiles como seguridad real: el control de acceso es demostrativo y local.

La cámara es opcional y solo para UTP, para generar material educativo original. Nunca tomar fotografías reales de niños ni de documentos escolares.

## Distribución del equipo

- Integrante 1 (compañero): modelos, datos de demostración, componentes Compose, pantalla inicial, docente, estudiante e informes.
- Integrante 2: repositorio DataStore, ViewModel, validaciones, pantallas UTP, recursos nativos, navegación e integración.
- Grupo: [NÚMERO POR COMPLETAR]

Ambos deben revisar y probar el código de sus respectivos módulos, documentar cambios verdaderos y participar en la defensa.

El proyecto se encuentra en GitHub (`mvnuuu/ExploraPaz-Android`). La participación se evidencia por commits en ramas de cada integrante; sigue pendiente la verificación en Android Studio y la planificación colaborativa exigida por la rúbrica.

## Materiales docentes usados

- Caso MPAZ PRO.
- Encargo y rúbrica EP2.
- Guía 7: estructura MVVM.
- Guía 8: componentes Compose.
- Guía 9: diseño adaptable.
- Guía 10: Navigation Compose.
- Guía 11: formularios, ViewModel, `StateFlow`.
- Guía 12: DataStore y animaciones.
- Guía 13: cámara, galería y FileProvider.

Las versiones base de Gradle y Android se tomaron del proyecto de la Guía 11. La dependencia de DataStore Preferences se agregó para implementar el uso indicado en la Guía 12.
