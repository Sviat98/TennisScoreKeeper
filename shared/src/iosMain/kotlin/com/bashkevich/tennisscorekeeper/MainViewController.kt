package com.bashkevich.tennisscorekeeper

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// Entry point consumed by iosApp/iosApp/ContentView.swift.
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
