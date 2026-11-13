# Recordatorios — Programación Mobile

Aplicación Android de recordatorios contextuales desarrollada de forma incremental (Kotlin + Jetpack Compose + Material 3).

**Autor:** Alan Gonzalez

| Versión | Tag | Contenido |
|---|---|---|
| Actividad 1 | `actividad-1` | Modelo (data/sealed classes), validaciones, colecciones (List/Set/Map) y pantalla inicial con datos simulados. |
| Actividad 2 | `actividad-2` | Pantallas de listado, creación y detalle; componentes reutilizables; formulario validado; ViewModel + UiState + StateFlow; repositorio en memoria; Navigation Compose. |

## Arquitectura (Actividad 2)
```
Pantalla (Composable) --eventos/lambdas--> ViewModel --suspend--> RecordatorioRepository (interfaz)
        ^                                     |                         ^
        |  collectAsStateWithLifecycle        | MutableStateFlow.update  | InMemoryRecordatorioRepository
        +------------ StateFlow<UiState> <----+
```

| Ruta | Pantalla | Argumentos |
|---|---|---|
| `lista` (inicio) | Listado | — |
| `crear` | Creación | — |
| `detalle/{id}` | Detalle | `id: String` |

### Simular estados
Menú ⋮ del listado: *Simular error de carga*, *Simular error al guardar (próximo)*, *Vaciar recordatorios*, *Restaurar ejemplos*.

## Requisitos
- Android Studio (Ladybug o superior), JDK 17
- compileSdk / targetSdk 35, minSdk 26

## Ejecutar
1. `git clone` del repositorio y abrirlo en Android Studio.
2. Esperar la sincronización de Gradle.
3. Elegir un emulador (Device Manager) o un dispositivo con depuración USB y presionar **Run**.

Pruebas unitarias: `./gradlew testDebugUnitTest`
