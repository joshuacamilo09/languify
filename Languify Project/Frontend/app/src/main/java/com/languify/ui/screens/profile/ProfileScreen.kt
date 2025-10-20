package com.languify.ui.screens.profile

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.languify.R
import com.languify.core.localization.LocaleManager
import com.languify.ui.viewmodel.ProfileViewModel
import com.languify.ui.viewmodel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))

    // Observe current values
    val isDarkMode by profileViewModel.darkModeEnabled.collectAsState()
    val selectedLang by profileViewModel.selectedLanguage.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.profile_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🔹 Dark mode toggle
            Text(stringResource(R.string.profile_theme), style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = isDarkMode,
                onCheckedChange = { profileViewModel.toggleDarkMode(context, it) }
            )

            Divider()

            // 🔹 Language selection
            Text(stringResource(R.string.profile_language), style = MaterialTheme.typography.titleMedium)
            LanguageDropdown(
                selected = selectedLang,
                onChange = { newLang ->
                    profileViewModel.setLanguage(context, newLang)
                    LocaleManager.setLocale(context, newLang)
                    // Restart the activity to apply changes instantly
                    (context as? Activity)?.recreate()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageDropdown(selected: String, onChange: (String) -> Unit) {
    val options = listOf("en", "pt", "fr", "de", "zh")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt.uppercase()) },
                    onClick = { onChange(opt); expanded = false }
                )
            }
        }
    }
}
