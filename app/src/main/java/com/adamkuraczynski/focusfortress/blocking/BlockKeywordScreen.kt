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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.database.BlockedKeyword
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown

/**
 * Composable function that displays the screen for blocking keywords.
 *
 * Users can add keywords to block, which will prevent content containing these keywords
 * from being accessed. Users can also remove keywords from the blocked list.
 *
 * @param navController The [NavController] for navigating between screens.
 * @param viewModel The [BlockKeywordViewModel] managing the keyword blocking logic.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.6
 *
 * @see androidx.navigation.NavController
 * @see androidx.lifecycle.viewmodel.compose.viewModel
 */
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

    val textFieldFocusRequester = remember { FocusRequester() }
    val buttonFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Block Keyword",
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
                    modifier = Modifier.background(Color.Transparent)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBrown, shape = RoundedCornerShape(8.dp))
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
                                    color = Golden
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 3.dp,
                                color = Golden,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .focusRequester(textFieldFocusRequester),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = MedievalFont,
                            color = Color.White
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkBrown,
                            unfocusedContainerColor = DarkBrown,
                            disabledContainerColor = DarkBrown,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                buttonFocusRequester.requestFocus()
                            }
                        )
                    )

                    Button(
                        onClick = {
                            if (keywordInput.isNotBlank()) {
                                viewModel.addKeyword(keywordInput.trim())
                                keywordInput = ""
                                textFieldFocusRequester.requestFocus()
                            } else {
                                focusManager.clearFocus()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightBrown,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .focusRequester(buttonFocusRequester)
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
                    contentDescription = "Background image of a water well",
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
                                    .background(LightBrown, shape = RoundedCornerShape(8.dp))
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

/**
 * Composable function representing a single keyword item in the list.
 *
 * Displays the keyword and provides an option to remove it from the blocked list.
 *
 * @param blockedKeyword The [BlockedKeyword] object representing the blocked keyword.
 * @param onRemove Callback function when the keyword is to be removed.
 *
 */
@Composable
fun KeywordItem(blockedKeyword: BlockedKeyword, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(LightBrown, shape = RoundedCornerShape(8.dp))
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