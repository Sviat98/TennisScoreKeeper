package com.bashkevich.tennisscorekeeper.components.match.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateComponent
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter

@Composable
fun MatchScoringSettingsBlock(
    modifier: Modifier = Modifier,
    setsToWin: Int,
    setTemplateOptions: List<SetTemplate>,
    regularSetTemplate: SetTemplate,
    decidingSetTemplate: SetTemplate,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplateTypeFilter, SetTemplate) -> Unit
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass

    val regularSetTemplateOptions = setTemplateOptions.filter { it.isRegular }
    val decidingSetTemplateOptions = setTemplateOptions.filter { it.isDeciding }

    Box(modifier = Modifier.then(modifier)){
        if (windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            Row(
                modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth().align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SetTemplateComponent(
                    modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
                    label = "Regular Set Template",
                    setTemplateOptions = regularSetTemplateOptions,
                    enabled = setsToWin > 1,
                    currentSetTemplate = regularSetTemplate,
                    onSetTemplatesFetch = onSetTemplatesFetch,
                    onSetTemplateChange = { setTemplate ->
                        onSetTemplateChange(SetTemplateTypeFilter.REGULAR, setTemplate)
                    }
                )
                SetTemplateComponent(
                    modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
                    label = "Deciding Set Template",
                    setTemplateOptions = decidingSetTemplateOptions,
                    enabled = true,
                    currentSetTemplate = decidingSetTemplate,
                    onSetTemplatesFetch = onSetTemplatesFetch,
                    onSetTemplateChange = { setTemplate ->
                        onSetTemplateChange(SetTemplateTypeFilter.DECIDER, setTemplate)
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SetTemplateComponent(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = "Regular Set Template",
                    setTemplateOptions = regularSetTemplateOptions,
                    enabled = setsToWin > 1,
                    currentSetTemplate = regularSetTemplate,
                    onSetTemplatesFetch = onSetTemplatesFetch,
                    onSetTemplateChange = { setTemplate ->
                        onSetTemplateChange(SetTemplateTypeFilter.REGULAR, setTemplate)
                    }
                )
                SetTemplateComponent(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    label = "Deciding Set Template",
                    setTemplateOptions = decidingSetTemplateOptions,
                    enabled = true,
                    currentSetTemplate = decidingSetTemplate,
                    onSetTemplatesFetch = onSetTemplatesFetch,
                    onSetTemplateChange = { setTemplate ->
                        onSetTemplateChange(SetTemplateTypeFilter.DECIDER, setTemplate)
                    }
                )
            }
        }
    }
}