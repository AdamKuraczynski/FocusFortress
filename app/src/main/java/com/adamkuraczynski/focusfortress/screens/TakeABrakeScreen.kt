package com.adamkuraczynski.focusfortress.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.adamkuraczynski.focusfortress.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TakeABreakScreen(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.room)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take A Break") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Black,
                ),
                modifier = Modifier.background(Color.Transparent)
            )
        },
        content = { paddingValues ->
            Box(

            ){
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Text(
                    text = "TakeABreakScreen",
                    modifier = Modifier.padding(paddingValues).padding(16.dp)
                )
            }
        }
    )
}