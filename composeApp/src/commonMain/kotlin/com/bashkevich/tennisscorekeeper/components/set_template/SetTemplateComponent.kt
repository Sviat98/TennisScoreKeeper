package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun SetTemplateComponent(
    modifier: Modifier = Modifier,
    label: String,
    setTemplateOptions: List<SetTemplate>,
    currentSetTemplate: SetTemplate,
    enabled: Boolean,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    Box(modifier = Modifier.then(modifier)) {
        if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            Row(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = label, modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
                SetTemplateCombobox(
                    modifier = Modifier.weight(1f),
                    setTemplateOptions = setTemplateOptions,
                    enabled = enabled,
                    currentSetTemplate = currentSetTemplate,
                    onSetTemplatesFetch = onSetTemplatesFetch,
                    onSetTemplateChange = onSetTemplateChange
                )
            }
        } else {
            Column(
                modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(label)
                    SetTemplateCombobox(
                        modifier = Modifier.fillMaxWidth(),
                        setTemplateOptions = setTemplateOptions,
                        enabled = enabled,
                        currentSetTemplate = currentSetTemplate,
                        onSetTemplatesFetch = onSetTemplatesFetch,
                        onSetTemplateChange = onSetTemplateChange
                    )
                }
            }

        }
    }

}