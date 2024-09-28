package ru.tensor.sbis.red_button.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.red_button.feature.RedButtonFeature
import ru.tensor.sbis.red_button.feature.RedButtonFeatureImpl
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonReRunAppInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonStatesInteractor
import ru.tensor.sbis.red_button.interactor.RedButtonStubInteractor
import ru.tensor.sbis.red_button.repository.data_source.RedButtonPreferences
import ru.tensor.sbis.red_button.repository.data_source.RedButtonPreferences.Companion.RED_BUTTON_PREFS
import ru.tensor.sbis.red_button.utils.RedButtonOpenHelper

/**
 * Основной модуль "Красной кнопки", необходим для реализации фичи модуля
 *
 * @author ra.stepanov
 */
@Module
class RedButtonModule {

    /** @SelfDocumented */
    @Provides
    fun provideRedButtonFeature(
        redButtonOpenHelper: RedButtonOpenHelper,
        preferencesInteractor: RedButtonPreferencesInteractor,
        reRunAppInteractor: RedButtonReRunAppInteractor,
        stubInteractor: RedButtonStubInteractor,
        statesInteractor: RedButtonStatesInteractor
    ): RedButtonFeature = RedButtonFeatureImpl(
        redButtonOpenHelper,
        preferencesInteractor,
        reRunAppInteractor,
        stubInteractor,
        statesInteractor
    )

    @Provides
    fun providePreferences(context: Context) =
        RedButtonPreferences(context.getSharedPreferences(RED_BUTTON_PREFS, Context.MODE_PRIVATE))
}