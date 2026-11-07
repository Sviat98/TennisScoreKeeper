package com.bashkevich.tennisscorekeeper.screens.settings.settemplatelist

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.mvi.UiEvent
import com.bashkevich.tennisscorekeeper.mvi.UiState
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Immutable
data class SetTemplateListState(
    val setTemplateComponentState: SetComponentState,
) : UiState {
    companion object {
        fun initial() = SetTemplateListState(
            setTemplateComponentState = SetComponentState(
                selectedSetState = SetComponentState.SelectedSetState.Idle(setTemplate = null),
                setOptionsState = SetComponentState.SetTemplateOptionsState.Idle(options = emptyList()),
            )
        )
    }
}

@Immutable
sealed class SetTemplateListUiEvent : UiEvent {
    data object FetchSetTemplates : SetTemplateListUiEvent()

    data class SelectSetTemplate(val setTemplate: SetTemplate) : SetTemplateListUiEvent()
}
