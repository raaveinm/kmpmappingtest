package com.example.keymappertest

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.keymappertest.keybinding.IS_APPLE

@Composable
@Preview
fun App() { MaterialTheme { Text(IS_APPLE.toString()) } }