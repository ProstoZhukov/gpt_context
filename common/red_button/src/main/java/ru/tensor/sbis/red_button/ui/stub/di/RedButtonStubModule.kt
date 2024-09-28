package ru.tensor.sbis.red_button.ui.stub.di

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.red_button.interactor.RedButtonPreferencesInteractor
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubActivity
import ru.tensor.sbis.red_button.ui.stub.RedButtonStubViewModel

/**
 * DI модуль для компонента [RedButtonStubActivity]
 *
 * @author ra.stepanov
 */
@Module
class RedButtonStubModule {

    @Provides
    fun provideContext(activity: RedButtonStubActivity): Context = activity.applicationContext

    /** @SelfDocumented */
    @Provides
    fun provideVMFactory(preferencesInteractor: RedButtonPreferencesInteractor): RedButtonStubViewModelFactory =
        RedButtonStubViewModelFactory(preferencesInteractor)

    /**@SelfDocumented */
    @Provides
    fun provideViewModel(activity: RedButtonStubActivity, factory: RedButtonStubViewModelFactory) =
        ViewModelProviders.of(activity, factory).get(RedButtonStubViewModel::class.java)
}