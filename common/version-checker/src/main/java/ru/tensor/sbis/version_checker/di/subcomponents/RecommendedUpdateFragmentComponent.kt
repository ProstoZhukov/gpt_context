package ru.tensor.sbis.version_checker.di.subcomponents

import dagger.Subcomponent
import ru.tensor.sbis.version_checker.di.VersioningFragmentScope
import ru.tensor.sbis.version_checker.ui.recommended.RecommendedUpdateFragment

@VersioningFragmentScope
@Subcomponent
internal interface RecommendedUpdateFragmentComponent {
    fun inject(fragment: RecommendedUpdateFragment)
}