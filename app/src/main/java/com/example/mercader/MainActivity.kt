package com.example.mercader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mercader.ui.screen.game.CollectionScreen
import com.example.mercader.ui.screen.game.CollectionViewModel
import com.example.mercader.ui.screen.game.GameFormScreen
import com.example.mercader.ui.screen.game.GameFormViewModel
import com.example.mercader.ui.theme.MercaderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MercaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: GameFormViewModel = hiltViewModel()
                   GameFormScreen(
                       viewModel = viewModel,
                       onEventSaved = {
                      }
                  )
               }
                /*{
                    /*val viewModel: CollectionViewModel = hiltViewModel()
                    //CollectionScreen(
                    //    viewModel = viewModel
                    )*/

                }*/

           }
       }
   }
}
