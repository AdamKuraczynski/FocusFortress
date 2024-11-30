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
import com.adamkuraczynski.focusfortress.database.BlockedWebsite
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.adamkuraczynski.focusfortress.ui.theme.DarkBrown
import com.adamkuraczynski.focusfortress.ui.theme.Golden
import com.adamkuraczynski.focusfortress.ui.theme.LightBrown
import com.adamkuraczynski.focusfortress.ui.theme.MedievalFont


/**
 * Composable function that displays the screen for blocking websites.
 *
 * Users can add website URLs or domains to block. Blocked websites will be prevented
 * from being accessed in supported browsers. Users can also remove websites from the blocked list.
 *
 * @param navController The [NavController] for navigating between screens.
 * @param viewModel The [BlockWebsiteViewModel] managing the website blocking logic.
 *
 * **Author:** Adam Kuraczyński
 *
 * **Version:** 1.7
 *
 * @see androidx.navigation.NavController
 * @see androidx.lifecycle.viewmodel.compose.viewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockWebsiteScreen(
    navController: NavController,
    viewModel: BlockWebsiteViewModel = viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.kitchen)
    var searchAndWebsiteInput by rememberSaveable { mutableStateOf("") }
    val blockedWebsites by viewModel.blockedWebsites.collectAsState(initial = emptyList())

    val filteredWebsites = remember(blockedWebsites, searchAndWebsiteInput) {
        blockedWebsites.filter {
            it.domain.contains(searchAndWebsiteInput, ignoreCase = true)
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
                            "Block Website",
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
                ) {

                    TextField(
                        value = searchAndWebsiteInput,
                        onValueChange = { searchAndWebsiteInput = it },
                        placeholder = {
                            Text(
                                text = "Enter website URL or domain",
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
                                color = Color.Black,
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
                            if (searchAndWebsiteInput.isNotBlank()) {
                                viewModel.blockWebsite(searchAndWebsiteInput.trim())
                                searchAndWebsiteInput = ""
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
                    contentDescription = "Background image of a kitchen",
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

                    if (blockedWebsites.isEmpty()) {
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
                            ){
                                Text(
                                    "No blocked websites",
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
                                items = filteredWebsites,
                                key = { it.domain }
                            ) { blockedWebsite ->
                                WebsiteItem(
                                    blockedWebsite = blockedWebsite,
                                    onUnblock = { viewModel.unblockWebsite(blockedWebsite) }
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
 * Composable function representing a single website item in the list.
 *
 * Displays the website domain and provides an option to unblock it.
 *
 * @param blockedWebsite The [BlockedWebsite] object representing the blocked website.
 * @param onUnblock Callback function when the website is to be unblocked.
 *
 */
@Composable
fun WebsiteItem(blockedWebsite: BlockedWebsite, onUnblock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(LightBrown, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = blockedWebsite.domain,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = MedievalFont,
                fontSize = 20.sp,
                color = Color.White
            ),
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onUnblock) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Unblock",
                tint = Color.White
            )
        }
    }
}