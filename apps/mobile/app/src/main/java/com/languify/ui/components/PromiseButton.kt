package com.languify.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.languify.infra.api.data.model.PromiseState

@Composable
fun PromiseButton(
    state: PromiseState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Button(onClick, modifier) {
        when (state) {
            PromiseState.Pending ->
                CircularProgressIndicator(
                    color = LocalContentColor.current,
                    modifier = Modifier.size(24.dp),
                )

            else -> content()
        }
    }
}
