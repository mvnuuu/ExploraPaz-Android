# ExploraPaz — Estado de entrega EP2

Este proyecto **es el plan de código**, no un resultado de pruebas. Se debe ejecutar en Android Studio y registrar los resultados reales.

- Repositorio: https://github.com/mvnuuu/ExploraPaz-Android
- Rama inicial: `main`, antes de los nuevos commits (merge de PR #1: `2ac0826`).
- Primera etapa: `mvnuuu` registra funcionalidades de modelos, DataStore, validaciones, formularios y navegación.
- Segunda etapa (opcional para que la versión base funcione): `devyelo` agrega interfaz adaptable y mejoras de retroalimentación; debe trabajar sobre la versión de mvnuuu.
- Herramientas: Kotlin, Jetpack Compose, Material 3, MVVM, Navigation Compose, DataStore, recursos cámara y galería.
- Alcance fuera de EP2: REST, Room/SQLite y autenticación institucional.

## Antes de entregar

1. Sincronizar Gradle y hacer `Build > Make Project` en Android Studio.
2. Probar el flujo: UTP autoriza/publica > docente asigna > estudiante responde > docente consulta informes.
3. Cerrar y abrir la app para verificar persistencia, límites de intentos, fechas y observaciones.
4. Probar cámara y galería en un dispositivo que disponga de esos recursos.
5. Mantener las evidencias de pruebas reales, responsables y fechas en `docs/` y en Trello o equivalente.
6. Subir a GitHub y entregar código en AVA **antes** de la fecha límite; el encargo penaliza cambios fuera de plazo.

**No afirmamos que compila en Android ni que obtuvo 100%: esas comprobaciones quedan pendientes en el equipo.**
