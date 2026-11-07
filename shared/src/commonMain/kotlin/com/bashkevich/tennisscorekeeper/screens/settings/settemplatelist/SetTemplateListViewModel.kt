package com.bashkevich.tennisscorekeeper.screens.settings.settemplatelist

import androidx.lifecycle.viewModelScope
import com.bashkevich.tennisscorekeeper.components.set_template.SetComponentState
import com.bashkevich.tennisscorekeeper.core.remote.LoadResult
import com.bashkevich.tennisscorekeeper.mvi.BaseViewModel
import com.bashkevich.tennisscorekeeper.mvi.UiAction
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SET_TEMPLATE_DEFAULT
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplate
import com.bashkevich.tennisscorekeeper.model.set_template.domain.SetTemplateTypeFilter
import com.bashkevich.tennisscorekeeper.model.set_template.repository.SetTemplateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Выбранный шаблон отдельным сетевым запросом не догружаем: он всегда приходит из списка,
// который при открытии дропдауна свежий целиком сохранён в БД. Повтор при ошибке списка =
// повторное открытие дропдауна (запрос идёт при каждом открытии).
@OptIn(ExperimentalCoroutinesApi::class)
class SetTemplateListViewModel(
    private val setTemplateRepository: SetTemplateRepository,
) : BaseViewModel<SetTemplateListState, SetTemplateListUiEvent, UiAction>() {

    private val _selectedSetId = MutableStateFlow<Int?>(null)

    private val _optionsFetchResult = MutableStateFlow<LoadResult<Unit, Throwable>?>(null)

    private val setTemplatesFromDb: StateFlow<List<SetTemplate>> =
        setTemplateRepository.observeSetTemplates(SetTemplateTypeFilter.ALL)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val setOptionsState: StateFlow<SetComponentState.SetTemplateOptionsState> =
        combine(
            _optionsFetchResult,
            setTemplatesFromDb,
        ) { result: LoadResult<Unit, Throwable>?, dbData: List<SetTemplate> ->
            when {
                result == null && dbData.isEmpty() -> SetComponentState.SetTemplateOptionsState.Loading
                result is LoadResult.Error && dbData.isEmpty() ->
                    SetComponentState.SetTemplateOptionsState.Error(result.result.message ?: "Error")
                else -> SetComponentState.SetTemplateOptionsState.Idle(dbData)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetComponentState.SetTemplateOptionsState.Idle(options = emptyList()),
        )

    private val selectedSetTemplate: StateFlow<SetTemplate> =
        _selectedSetId.filterNotNull().flatMapLatest { id ->
            setTemplateRepository.observeSetTemplateById(id)
        }.stateIn(viewModelScope, SharingStarted.Lazily, SET_TEMPLATE_DEFAULT)

    private val selectedSetState: StateFlow<SetComponentState.SelectedSetState> =
        combine(
            _selectedSetId,
            selectedSetTemplate,
        ) { selectedSetId: Int?, setTemplate: SetTemplate ->
            SetComponentState.SelectedSetState.Idle(
                setTemplate = if (selectedSetId == null) null else setTemplate,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetComponentState.SelectedSetState.Idle(setTemplate = null),
        )

    private val setTemplateComponentState: StateFlow<SetComponentState> =
        combine(
            selectedSetState,
            setOptionsState,
        ) { selectedSetState: SetComponentState.SelectedSetState, setOptionsState: SetComponentState.SetTemplateOptionsState ->
            SetComponentState(
                selectedSetState = selectedSetState,
                setOptionsState = setOptionsState,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetTemplateListState.initial().setTemplateComponentState,
        )

    override val state: StateFlow<SetTemplateListState> =
        setTemplateComponentState.map { componentState ->
            SetTemplateListState(setTemplateComponentState = componentState)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SetTemplateListState.initial(),
        )

    fun onEvent(uiEvent: SetTemplateListUiEvent) {
        when (uiEvent) {
            is SetTemplateListUiEvent.FetchSetTemplates -> fetchSetTemplates()
            is SetTemplateListUiEvent.SelectSetTemplate -> _selectedSetId.value = uiEvent.setTemplate.id
        }
    }

    private fun fetchSetTemplates() {
        viewModelScope.launch {
            _optionsFetchResult.value = null

            _optionsFetchResult.value =
                setTemplateRepository.fetchSetTemplates(SetTemplateTypeFilter.ALL)
        }
    }
}
