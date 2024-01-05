package dev.ricknout.rugbyranker.core.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RugbyRankerSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    Snackbar(
        modifier = modifier.padding(all = 16.dp),
        content = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                Text(text = snackbarData.visuals.message)
            }
        },
    )
}
