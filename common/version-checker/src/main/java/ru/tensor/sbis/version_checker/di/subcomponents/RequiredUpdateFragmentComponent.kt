package ru.tensor.sbis.version_checker.di.subcomponents

import dagger.Subcomponent
import ru.tensor.sbis.version_checker.di.VersioningFragmentScope
import ru.tensor.sbis.version_checker.ui.mandatory.RequiredUpdateFragment

@VersioningFragmentScope
@Subcomponent
internal interface RequiredUpdateFragmentComponent {
    fun inject(fragment: RequiredUpdateFragment)
}