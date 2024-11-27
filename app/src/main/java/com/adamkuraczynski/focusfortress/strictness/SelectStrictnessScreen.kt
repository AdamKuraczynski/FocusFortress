package com.adamkuraczynski.focusfortress.strictness

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectStrictnessScreen(
    navController: NavController,
    strictnessViewModel: StrictnessViewModel
) {
    val backgroundImage = painterResource(id = R.drawable.armory)

    val currentStrictness by strictnessViewModel.strictnessLevel.collectAsState()
    var selectedStrictness by remember(currentStrictness) { mutableStateOf(currentStrictness) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Select Strictness",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Golden,
                                    fontFamily = MedievalFont,
                                    fontSize = 40.sp,
                                    shadow = Shadow(
                                        color = Color.Black,
                                        offset = Offset(2f, 2f),
                                        blurRadius = 4f
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Black,
                    ),
                    modifier = Modifier.background(Color.Transparent)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBrown, shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                ) {
                    Text(
                        text = "Select strictness level:\n\n" +
                                "Normal - no strictness (default)\n" +
                                "Protected - password block\n\n" +
                                "Keep password safe as there is no way to recover it.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Golden,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = MedievalFont
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize())
            {
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OptionItem(
                        description = "Normal",
                        isSelected = selectedStrictness == "Normal",
                        onSelect = { selectedStrictness = "Normal" }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OptionItem(
                        description = "Protected",
                        isSelected = selectedStrictness == "Protected",
                        onSelect = { selectedStrictness = "Protected" }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (selectedStrictness == "Protected") {
                                navController.navigate("passcodeSetup")
                            } else {
                                strictnessViewModel.setStrictnessLevel(selectedStrictness)
                                navController.navigateUp()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBrown
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .border(2.dp, Golden, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 25.sp,
                                fontFamily = MedievalFont,
                                color = Golden
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    )
}