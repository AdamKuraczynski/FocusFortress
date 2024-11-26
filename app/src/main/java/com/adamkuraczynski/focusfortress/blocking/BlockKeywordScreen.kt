package com.adamkuraczynski.focusfortress.blocking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.adamkuraczynski.focusfortress.database.BlockedKeyword
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockKeywordScreen(
    navController: NavController,
    viewModel: BlockKeywordViewModel = viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.water_well)
    var keywordInput by rememberSaveable { mutableStateOf("") }
    val blockedKeywords by viewModel.blockedKeywords.collectAsState(initial = emptyList())

    val filteredKeywords = remember(blockedKeywords, keywordInput) {
        blockedKeywords.filter {
            it.keyword.contains(keywordInput, ignoreCase = true)
        }
    }

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
                                "Block Keyword",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = Color(0xFFE0C097),
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
                        .background(Color(0xFF3B2F2F), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    TextField(
                        value = keywordInput,
                        onValueChange = { keywordInput = it },
                        placeholder = {
                            Text(
                                text = "Enter keyword to block",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = MedievalFont,
                                    fontSize = 16.sp,
                                    color = Color(0xFFE0C097)
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 3.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = MedievalFont,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF3B2F2F),
                            unfocusedContainerColor = Color(0xFF3B2F2F),
                            disabledContainerColor = Color(0xFF3B2F2F),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                    )

                    Button(
                        onClick = {
                            if (keywordInput.isNotBlank()) {
                                viewModel.addKeyword(keywordInput.trim())
                                keywordInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6D4C41),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = MedievalFont,
                                fontSize = 18.sp
                            )
                        )
                    }
                }

            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
                ) {

                    if (blockedKeywords.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,

                            ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(Color(0xFF6D4C41), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    "No blocked keywords",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontFamily = MedievalFont,
                                        fontSize = 20.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = filteredKeywords,
                                key = { it.id }
                            ) { blockedKeyword ->
                                KeywordItem(
                                    blockedKeyword = blockedKeyword,
                                    onRemove = { viewModel.removeKeyword(blockedKeyword) }
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun KeywordItem(blockedKeyword: BlockedKeyword, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF6D4C41), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = blockedKeyword.keyword,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Remove",
                tint = Color.White
            )
        }
    }
}