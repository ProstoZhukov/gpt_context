package ru.tensor.sbis.localfeaturetoggle.presentation.store

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.localfeaturetoggle.data.Feature
import com.arkivanov.mvikotlin.core.store.Store

/**
 * Описание событий (Intent), сайд-эффектов и состояний.
 *
 * @author mb.kruglova
 */
internal interface LocalFeatureToggleStore :
    Store<LocalFeatureToggleStore.Intent, LocalFeatureToggleStore.State, LocalFeatureToggleStore.Label> {

    sealed interface Intent {
        /**
         * Событие переключение тогла у фичи
         */
        data class SwitchItem(val feature: Feature, val isActivated: Boolean) : Intent

        /**
         * Событие получения списка фич
         */
        object GetFeatureList : Intent
    }

    sealed interface Label

    @Parcelize
    data class State(
        /**
         * Состояние списка фич
         */
        val listItems: List<Feature> = emptyList()
    ) : Parcelable
}