# Matriz de trazabilidad EP2 — MPAZ PRO

| Indicador | Evidencia en código | Estado |
|---|---|---|
| IE 2.1.1 (15%): UI y navegación | `HomeScreen`, `AppNavigation`, `Page`, pantallas por perfil | Implementado, falta ejecución visual |
| IE 2.1.2 (15%): formularios validados | `UnitFormScreen`, `TeacherScreen`, `QuizScreen`, `ValidationField` | Implementado, falta prueba manual |
| IE 2.2.1 (10%): desacoplamiento | `MPAZViewModel.validateUnit`, `MPAZViewModel.assign`, reglas de intentos | Implementado |
| IE 2.2.2 (10%): animaciones | `AnimatedSuccess`, `AnimatedVisibility`, `animateColorAsState` | Implementado, falta comprobar visualmente |
| IE 2.3.1 (15%): MVVM + persistencia | `model`, `repository`, `viewmodel`, DataStore Preferences | Implementado, falta prueba de cierre/apertura |
| IE 2.3.2 (20%): colaboración | GitHub/Trello, commits, ramas e issues | **PENDIENTE** (acordado con el equipo, pero obligatorio para la rúbrica) |
| IE 2.4.1 (15%): dos recursos nativos | `GetContent`, `TakePicture`, `FileProvider` en UTP | Implementado, falta prueba en dispositivo |

## Pendientes de control antes de EP2

- [ ] Compilación limpia y ejecución en Android Studio.
- [ ] Recorrer flujo completo UTP → Docente → Estudiante → Docente.
- [ ] Probar campos vacíos, título corto, alternativas repetidas, intentos `0`, `6` y texto.
- [ ] Confirmar guardado entre reinicios de la app.
- [ ] Probar que perfil Estudiante no navega a Informes o al editor de UTP.
- [ ] Probar galería y cámara: imagen elegida o capturada, guardar y volver a abrir.
- [ ] Verificar responsividad en móvil y tablet.
- [ ] Completar nombres y grupo en README.
- [ ] Crear y evidenciar GitHub y planificación colaborativa antes de entregar.
- [ ] Entregar AVA grupal y activar AVA individual en el plazo del docente.

## Fuera de este avance

Sin API REST ni servicios externos. Sin Room/SQLite porque DataStore cumple almacenamiento local de prototipo. No incluir información personal institucional. Las unidades se alimentan de ejemplos originales; sus códigos `DEMO-*` **no son OA oficiales Mineduc**.
