package com.bashkevich.tennisscorekeeper.components.sandbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.bashkevich.tennisscorekeeper.components.refreshByKeyboard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiplatformRefreshScreen() {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var itemsList by remember { mutableStateOf(List(20) { "Original Item $it" }) }

    // Lambda to trigger asynchronous backend updates safely
    val triggerRefresh = {
        if (!isRefreshing) {
            isRefreshing = true
            coroutineScope.launch {
                delay(2000.milliseconds) // Simulate network call on Dispatchers.IO
                itemsList = List(20) { "Refreshed Item $it (Updated!)" }
                isRefreshing = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .refreshByKeyboard { triggerRefresh() }
    ) {
        // Material 3 container handles both mobile touch pulls and trackpad overscrolls
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { triggerRefresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            // Lazy layouts natively pass scroll events up to PullToRefreshBox
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(itemsList) { item ->
                    Text(text = item)
                }
            }
        }
    }
}
