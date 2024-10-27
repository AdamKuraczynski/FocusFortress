package com.adamkuraczynski.focusfortress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamkuraczynski.focusfortress.ui.theme.FocusFortressTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusFortressTheme {
                Column {

                    
                        Joke1(
                            question = "Why did the chicken cross the road?",
                            answer = "To get to the other side."
                        )
                }

                }
            }
        }
    }

@Composable
fun Joke1(question: String, answer: String) {

    Column {
        Text(
            text = question,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleLarge,
            textAlign =  androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = answer,
            modifier = Modifier.padding(32.dp)
        )

    }

}