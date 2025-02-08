package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavHostController
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {

    ComposeViewport(document.body!!) {
        val navController: NavHostController = rememberNavController()
        App(navController = navController)

        LaunchedEffect(Unit) {
            window.bindToNavigation(navController)
        }

    }
}