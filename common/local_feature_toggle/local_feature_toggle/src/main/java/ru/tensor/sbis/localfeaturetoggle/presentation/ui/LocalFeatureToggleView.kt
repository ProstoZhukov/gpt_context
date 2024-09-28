package ru.tensor.sbis.localfeaturetoggle.presentation.ui

import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.localfeaturetoggle.data.Feature

/**
 * Описание событий и модели.
 *
 * @author mb.kruglova
 */
internal interface LocalFeatureToggleView : MviView<LocalFeatureToggleView.Model, LocalFeatureToggleView.Event> {

    sealed interface Event {
        data class ClickSwitchItem(val feature: Feature, val isActivated: Boolean) : Event
    }

    data class Model(
        val listItems: List<Feature> = emptyList()
    )
}