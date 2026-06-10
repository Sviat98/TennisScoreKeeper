package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Composable
fun SetTemplateComponent(
    modifier: Modifier = Modifier,
    label: String,
    enabled: Boolean,
    setComponentState: SetComponentState,
    onSetTemplatesFetch: () -> Unit,
    onSetTemplateChange: (SetTemplate) -> Unit,
    onRetrySelectedSet: () -> Unit = {},
) {
    Column(
        modifier = Modifier.then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.widthIn(max = 300.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(label)
            SetTemplateCombobox(
                enabled = enabled,
                setComponentState = setComponentState,
                onSetTemplatesFetch = onSetTemplatesFetch,
                onSetTemplateChange = onSetTemplateChange,
                onRetrySelectedSet = onRetrySelectedSet,
            )
        }
    }
}
