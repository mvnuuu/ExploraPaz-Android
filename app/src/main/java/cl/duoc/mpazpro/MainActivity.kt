package cl.duoc.mpazpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.mpazpro.navigation.AppNavigation
import cl.duoc.mpazpro.repository.MPAZRepository
import cl.duoc.mpazpro.viewmodel.MPAZViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val repository = remember { MPAZRepository(applicationContext) }
            val vm: MPAZViewModel = viewModel(factory = MPAZViewModel.factory(repository))
            MaterialTheme(colorScheme = lightColorScheme(
                primary = Color(0xFF285A64),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCBECED),
                secondaryContainer = Color(0xFFF9E3B9)
            )) { AppNavigation(vm) }
        }
    }
}
