package com.adamkuraczynski.focusfortress.blocking

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.adamkuraczynski.focusfortress.R
import com.adamkuraczynski.focusfortress.database.BlockedWebsite
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockWebsiteScreen(
    navController: NavController,
    viewModel: BlockWebsiteViewModel = viewModel()
) {
    val backgroundImage = painterResource(id = R.drawable.kitchen)
    val context = LocalContext.current
    var websiteInput by remember { mutableStateOf("") }
    var blockedWebsites by remember { mutableStateOf(emptyList<BlockedWebsite>()) }

    LaunchedEffect(Unit) {
        viewModel.blockedWebsites.collectLatest { blockedList ->
            blockedWebsites = blockedList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Website") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box{
                Image(
                    painter = backgroundImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier.padding(paddingValues).padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = websiteInput,
                            onValueChange = { websiteInput = it },
                            label = { Text("Enter website URL or domain") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (websiteInput.isNotBlank()) {
                                viewModel.blockWebsite(websiteInput.trim())
                                websiteInput = ""
                                Toast.makeText(
                                    context,
                                    "Website added to block list",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text("Add")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn {
                        items(blockedWebsites) { blockedWebsite ->
                            WebsiteItem(
                                blockedWebsite = blockedWebsite,
                                onUnblock = { viewModel.unblockWebsite(blockedWebsite) }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun WebsiteItem(blockedWebsite: BlockedWebsite, onUnblock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = blockedWebsite.domain,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = onUnblock) {
            Icon(Icons.Default.Delete, contentDescription = "Unblock")
        }
    }
}