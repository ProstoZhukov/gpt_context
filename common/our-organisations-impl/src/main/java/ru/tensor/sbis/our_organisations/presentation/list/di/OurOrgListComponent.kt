package ru.tensor.sbis.our_organisations.presentation.list.di

import android.view.View
import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Subcomponent
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.our_organisations.feature.data.OurOrgParams
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListController
import ru.tensor.sbis.our_organisations.presentation.list.ui.OurOrgListView
import javax.inject.Named

const val DISCARD_RESULT_AFTER_CLICK_KEY = "DISCARD_RESULT_AFTER_CLICK_KEY"

/**
 *  Dagger компонент модуля нашей организации.
 *
 *  @author mv.ilin
 */
@Subcomponent(modules = [OurOrgListModule::class])
internal interface OurOrgListComponent {

    fun injector(): Injector

    @Subcomponent.Factory
    interface Factory {

        fun create(
            @BindsInstance initParams: OurOrgParams,
            @BindsInstance @Named(DISCARD_RESULT_AFTER_CLICK_KEY) discardResultAfterClick: Boolean,
            @BindsInstance viewFactory: (View) -> OurOrgListView
        ): OurOrgListComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): OurOrgListController
    }
}
