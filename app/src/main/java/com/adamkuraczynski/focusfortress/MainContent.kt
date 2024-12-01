package com.adamkuraczynski.focusfortress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(navController: NavController) {
    val backgroundImage = painterResource(id = R.drawable.courtyard2)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Focus Fortress",
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
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Black,
                ),
                modifier = Modifier.background(Color.Transparent),
                windowInsets = WindowInsets.statusBars
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Image(
                    painter = backgroundImage,
                    contentDescription = "Background image of a medieval courtyard",
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
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Usage Statistics
                    Box(
                        modifier = Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Usage Statistics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Golden,
                                fontFamily = MedievalFont,
                                fontSize = 32.sp
                            )
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StyledIconButton(
                            text = "Screen Time Per App",
                            onClick = { navController.navigate("screenTime") },
                            iconRes = R.drawable.horse,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                        StyledIconButton(
                            text = "Launch Count Per App",
                            onClick = { navController.navigate("launchCount") },
                            iconRes = R.drawable.hay,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Blocking Options
                    Box(
                        modifier = Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Blocking Options",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Golden,
                                fontFamily = MedievalFont,
                                fontSize = 32.sp
                            )
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StyledIconButton(
                            text = "Block App",
                            onClick = { navController.navigate("blockApp") },
                            iconRes = R.drawable.campfire,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                        StyledIconButton(
                            text = "Block Website",
                            onClick = { navController.navigate("blockWebsite") },
                            iconRes = R.drawable.food,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // spacers fix to center the third button in the middle - could not find other way
                        Spacer(modifier = Modifier.weight(1f))
                        StyledIconButton(
                            text = "Block Keyword",
                            onClick = { navController.navigate("blockKeyword") },
                            iconRes = R.drawable.water_bucket,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier
                                .weight(2f)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Other Options
                    Box(
                        modifier = Modifier
                            .background(Color.Black, shape = RoundedCornerShape(16.dp))
                            .padding(8.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Other Options",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Golden,
                                fontFamily = MedievalFont,
                                fontSize = 32.sp
                            )
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StyledIconButton(
                            text = "Pick from Schedules",
                            onClick = { navController.navigate("schedules") },
                            iconRes = R.drawable.bed,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                        StyledIconButton(
                            text = "Select Strictness Level",
                            onClick = { navController.navigate("selectStrictness") },
                            iconRes = R.drawable.armor,
                            contentDescription = "Decorative Icon",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun StyledIconButton(
    text: String,
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBrown,
            contentColor = Color.White
        ),
        shape = shape,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = modifier
            .wrapContentHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = MedievalFont,
                    fontSize = 22.sp
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}