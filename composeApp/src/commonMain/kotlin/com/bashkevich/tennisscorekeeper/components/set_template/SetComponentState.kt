package com.bashkevich.tennisscorekeeper.components.set_template

import androidx.compose.runtime.Immutable
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate

@Immutable
data class SetComponentState(
    val selectedSetState: SelectedSetState,
    val setOptionsState: SetTemplateOptionsState,
) {

    @Immutable
    sealed interface SelectedSetState {

        @Immutable
        data class Loading(val initialSetTemplateId: String) : SelectedSetState

        @Immutable
        data class Error(val initialSetTemplateId: String) : SelectedSetState

        @Immutable
        data class Idle(val setTemplate: SetTemplate?) : SelectedSetState
    }

    sealed interface SetTemplateOptionsState {
        @Immutable
        data object Loading : SetTemplateOptionsState

        @Immutable
        data class Error(val message: String) : SetTemplateOptionsState

        @Immutable
        data class Idle(val options: List<SetTemplate>) : SetTemplateOptionsState
    }
}
