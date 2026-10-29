# Alcance funcional — caso MPAZ PRO 2.º (EP2)

## Qué resuelve la versión académica

**UTP**
- Consulta las cuatro asignaturas y cuatro unidades de demostración.
- Crea y edita unidades con código OA de prueba, explicaciones, ejemplos y una pregunta de cuatro alternativas.
- Autoriza, publica o deshabilita unidades; editar obliga a autorizar nuevamente.
- Adjunta imagen educativa propia desde galería o cámara (opcional).

**Docente**
- Consulta exclusivamente unidades publicadas.
- Asigna contenidos al estudiante ficticio `EST-001`.
- Configura entre 1 y 5 intentos de prueba y consulta informe por asignatura/objetivo.

**Estudiante**
- Ve solo contenido publicado y asignado.
- Consulta explicación y ejemplo, marca el contenido como leído y responde selección múltiple.
- Visualiza corrección de su respuesta; la app controla el límite de intentos.
- No ve en la navegación informes ni edición de material.

**Datos locales**
- `DataStore Preferences` almacena unidades, estados, asignaciones, avance, último resultado e imagen local.
- El prototipo usa un solo alumno simulado y un solo docente simulado; no es un servicio institucional multiusuario.

## Requerimientos del caso diferidos, sin pretender que están resueltos

| Requisito del caso | Situación EP2 |
|---|---|
| Audios explicativos | No implementado; no aparece como indicador independiente en EP2 |
| Reportes descargables y observación docente persistida | Se presenta reporte local; exportación y observaciones se difieren |
| Varias preguntas por prueba, calendario de habilitación | Se ofrece una pregunta por unidad y control de intentos |
| Historial de revisiones/aprobaciones | Solo el estado actual |
| Autenticación real y permisos seguros multiusuario | Simulación de navegación por perfil; sin backend no hay aislamiento robusto |
| Estado «En revisión» | Flujo simplificado Borrador → Autorizado → Publicado → Deshabilitado |
| OA oficiales MINEDUC | Se reemplazan por códigos DEMO para evitar atribuir contenidos sin fuente |
| API REST y Retrofit | Fuera de esta entrega, reservado a etapa posterior |
| Room / SQLite | Sustituido por DataStore Preferences para una persistencia local académica |
| GitHub y planificación de equipo | Diferidos operativamente, pero obligatorios para cerrar EP2 según rúbrica |

## Precauciones académicas

1. El caso declara que la cámara y la galería no son necesarias para que funcione la experiencia de los niños. Se ofrece su uso solamente al perfil UTP como apoyo opcional en la creación de material **propio o con permiso**. Es una decisión para evidenciar exactamente los dos recursos nativos practicados en la Guía 13.
2. No usar datos personales reales ni fotografías de estudiantes reales.
3. Los resultados numéricos no son diagnósticos pedagógicos automáticos: corresponde interpretarlos al docente.
4. DataStore en un dispositivo **no sincroniza** estados entre el teléfono de UTP, docente y estudiante. Los tres roles se prueban en el mismo emulador/teléfono.
5. Para la defensa, no describir las pantallas como autenticación segura: son perfiles de simulación.
