package com.adamkuraczynski.focusfortress.achievements

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.LightGolden
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementScreen(
    navController: NavController,
    achievementsViewModel: AchievementsViewModel = viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.treasury)

    val achievements by achievementsViewModel.allAchievements.collectAsState()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Achievements",
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
                    modifier = Modifier
                        .background(Color.Transparent)
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = backgroundImage,
                    contentDescription = "Background image of a treasury",
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
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBrown)
                    ) {
                        Text(
                            text = "Below you can see all available achievements.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Golden,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = MedievalFont
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        achievements.forEach { achievement ->
                            AchievementItem(
                                title = achievement.title,
                                description = achievement.description,
                                isCompleted = achievement.isCompleted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun AchievementItem(
    title: String,
    description: String,
    isCompleted: Boolean,
    shape: Shape = RoundedCornerShape(16.dp)
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DarkBrown)
            .padding(4.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, shape)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = if (isCompleted) listOf(Golden, LightGolden) else listOf(LightBrown, DarkBrown)
                    ),
                    shape = shape
                )
                .clip(shape)
                .background(
                    color = if (isCompleted) Golden.copy(alpha = 0.2f) else LightBrown,
                    shape = shape
                )
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightBrown
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontFamily = MedievalFont,
                            fontSize = 20.sp,
                            color = if (isCompleted) Golden else Color.White,
                            textAlign = TextAlign.Start
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = TextStyle(
                            fontFamily = MedievalFont,
                            fontSize = 14.sp,
                            color = Golden
                        )
                    )
                }
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Achievement Completed",
                        tint = Golden
                    )
                }
            }
        }
    }
}