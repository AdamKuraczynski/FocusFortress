package com.adamkuraczynski.focusfortress.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * Displays the main menu of the FocusFortress application.
 *
 * This composable function sets up the primary user interface of the app after all necessary permissions have been granted.
 *
 * @author Adam Kuraczyński
 * @version 1.1
 *
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FocusFortress") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // First Section: Usage Statistics
                Text(
                    text = "Usage Statistics",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.navigate("screenTime") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Screen Time Per App")
                    }
                    Button(
                        onClick = { navController.navigate("launchCount") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Launch Count Per App")
                    }
                }

                // Second Section: Blocking Options
                Text(
                    text = "Blocking Options",
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("blockApp") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Block App")
                    }
                    Button(
                        onClick = { navController.navigate("blockWebsite") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Block Website")
                    }
                    Button(
                        onClick = { navController.navigate("blockKeyword") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Block Keyword")
                    }
                }

                // Third Section: Take a Break
                Text(
                    text = "Take a Break",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = { navController.navigate("takeABreak") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Take a Break")
                }

                // Fourth Section: Strictness Levels
                Text(
                    text = "Strictness Levels",
                    style = MaterialTheme.typography.titleMedium
                )
                Button(
                    onClick = { navController.navigate("selectStrictness") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Strictness Level")
                }
            }
        }
    )
}
