package com.bashkevich.tennisscorekeeper.components.add_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.components.set_template.SetTemplateComponent
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponent
import com.bashkevich.tennisscorekeeper.components.theme.ThemeComponentState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.theme.domain.ScoreboardTheme
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.deciding_set_template
import tennisscorekeeper.shared.generated.resources.regular_set_template

@Composable
fun MatchScoringAndThemeSettingsBlock(
    modifier: Modifier = Modifier,
    setsToWin: Int,
    regularSetComponentState: SetComponentState,
    decidingSetComponentState: SetComponentState,
    themeComponentState: ThemeComponentState,
    onSetsToWinChange: (Int) -> Unit,
    onSetTemplatesFetch: (SetTemplateTypeFilter) -> Unit,
    onRegularSetTemplateChange: (SetTemplate) -> Unit,
    onDecidingSetTemplateChange: (SetTemplate) -> Unit,
    onThemeSelected: (ScoreboardTheme) -> Unit,
    onThemesFetch: () -> Unit,
    onRetrySelectedTheme: (Int) -> Unit = {},
    onRetrySelectedRegularSet: (Int) -> Unit = {},
    onRetrySelectedDecidingSet: (Int) -> Unit = {},
    onPreviewClick: () -> Unit = {},
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isWideScreen = windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Box(modifier = Modifier.then(modifier)) {
        if (isWideScreen) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Row 1: Theme + SetsToWin
                Row(
                    modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ThemeComponent(
                        modifier = Modifier.weight(1f),
                        themeComponentState = themeComponentState,
                        onThemeSelected = onThemeSelected,
                        onThemesFetch = onThemesFetch,
                        onRetrySelectedTheme = onRetrySelectedTheme,
                        onPreviewClick = onPreviewClick,
                    )
                    SetsToWinBlock(
                        modifier = Modifier.weight(1f),
                        setsToWin = setsToWin,
                        onValueChange = onSetsToWinChange,
                    )
                }

                // Row 2: Regular + Deciding set templates
                Row(
                    modifier = Modifier.widthIn(max = 1000.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SetTemplateComponent(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.regular_set_template),
                        enabled = setsToWin > 1,
                        setComponentState = regularSetComponentState,
                        onSetTemplatesFetch = { onSetTemplatesFetch(SetTemplateTypeFilter.REGULAR) },
                        onSetTemplateChange = onRegularSetTemplateChange,
                        onRetrySelectedSet = onRetrySelectedRegularSet,
                    )
                    SetTemplateComponent(
                        modifier = Modifier.weight(1f),
                        label = stringResource(Res.string.deciding_set_template),
                        enabled = true,
                        setComponentState = decidingSetComponentState,
                        onSetTemplatesFetch = { onSetTemplatesFetch(SetTemplateTypeFilter.DECIDER) },
                        onSetTemplateChange = onDecidingSetTemplateChange,
                        onRetrySelectedSet = onRetrySelectedDecidingSet,
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ThemeComponent(
                    modifier = Modifier.fillMaxWidth(),
                    themeComponentState = themeComponentState,
                    onThemeSelected = onThemeSelected,
                    onThemesFetch = onThemesFetch,
                    onRetrySelectedTheme = onRetrySelectedTheme,
                    onPreviewClick = onPreviewClick,
                )

                SetsToWinBlock(
                    modifier = Modifier.fillMaxWidth(),
                    setsToWin = setsToWin,
                    onValueChange = onSetsToWinChange,
                )

                SetTemplateComponent(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Regular Set Template",
                    enabled = setsToWin > 1,
                    setComponentState = regularSetComponentState,
                    onSetTemplatesFetch = { onSetTemplatesFetch(SetTemplateTypeFilter.REGULAR) },
                    onSetTemplateChange = onRegularSetTemplateChange,
                    onRetrySelectedSet = onRetrySelectedRegularSet,
                )

                SetTemplateComponent(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Deciding Set Template",
                    enabled = true,
                    setComponentState = decidingSetComponentState,
                    onSetTemplatesFetch = { onSetTemplatesFetch(SetTemplateTypeFilter.DECIDER) },
                    onSetTemplateChange = onDecidingSetTemplateChange,
                    onRetrySelectedSet = onRetrySelectedDecidingSet,
                )
            }
        }
    }
}
