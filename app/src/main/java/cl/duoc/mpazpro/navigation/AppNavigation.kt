package cl.duoc.mpazpro.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.mpazpro.ui.components.Page
import cl.duoc.mpazpro.ui.screens.*
import cl.duoc.mpazpro.viewmodel.MPAZViewModel

/** Rutas con NavHost/NavController, siguiendo Guías 10 y 11. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(vm: MPAZViewModel) {
    val nav = rememberNavController()
    val state by vm.state.collectAsState()
    // Prototipo local: control de acceso a vistas, no autenticación institucional.
    var role by remember { mutableStateOf("") }
    val current by nav.currentBackStackEntryAsState()
    val onHome = current?.destination?.route == "home"

    Scaffold(topBar = {
        TopAppBar(title = { Text("MPAZ PRO 2.º") },
            navigationIcon = {
                if (!onHome) TextButton(onClick = { nav.popBackStack() }) { Text("Atrás") }
            }, actions = {
                if (!onHome) TextButton(onClick = {
                    role = ""
                    nav.navigate("home") { popUpTo("home") { inclusive = true } }
                }) { Text("Perfiles") }
            })
    }) { padding ->
        if (!state.loaded) {
            Page { CircularProgressIndicator(); Text("Cargando progreso local...") }
        } else {
            NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(padding)) {
                composable("home") {
                    HomeScreen(
                        onUTP = { role = "utp"; nav.navigate("utp") },
                        onTeacher = { role = "teacher"; nav.navigate("teacher") },
                        onStudent = { role = "student"; nav.navigate("student") }
                    )
                }
                composable("utp") {
                    if (role == "utp") UTPScreen(state, vm,
                        onEdit = { nav.navigate("unit_editor/$it") },
                        onNew = { nav.navigate("unit_editor/new") })
                    else Restricted()
                }
                composable("unit_editor/{unitId}") {
                    if (role == "utp") UnitFormScreen(vm) { nav.popBackStack() }
                    else Restricted()
                }
                composable("teacher") {
                    if (role == "teacher") TeacherScreen(state, vm, onReports = { nav.navigate("reports") })
                    else Restricted()
                }
                composable("reports") {
                    if (role == "teacher") ReportScreen(state) else Restricted()
                }
                composable("student") {
                    if (role == "student") StudentScreen(state) { nav.navigate("lesson/$it") }
                    else Restricted()
                }
                composable("lesson/{unitId}") { entry ->
                    if (role == "student") {
                        val id = entry.arguments?.getString("unitId")?.toIntOrNull() ?: -1
                        LessonScreen(state.units.firstOrNull { it.id == id }, state, vm) { nav.navigate("quiz/$id") }
                    } else Restricted()
                }
                composable("quiz/{unitId}") { entry ->
                    if (role == "student") {
                        val id = entry.arguments?.getString("unitId")?.toIntOrNull() ?: -1
                        QuizScreen(state.units.firstOrNull { it.id == id }, vm) {
                            nav.navigate("result/$id") { popUpTo("quiz/{unitId}") { inclusive = true } }
                        }
                    } else Restricted()
                }
                composable("result/{unitId}") { entry ->
                    if (role == "student") {
                        val id = entry.arguments?.getString("unitId")?.toIntOrNull() ?: -1
                        ResultScreen(state.units.firstOrNull { it.id == id }, state) {
                            nav.navigate("student") { popUpTo("student") { inclusive = true } }
                        }
                    } else Restricted()
                }
            }
        }
    }
}

@Composable
private fun Restricted() {
    Page { Text("Esta pantalla no está disponible para el perfil seleccionado.") }
}
