package ru.tensor.sbis.version_checker.di.subcomponents

import dagger.Subcomponent
import ru.tensor.sbis.version_checker.di.VersioningFragmentScope
import ru.tensor.sbis.version_checker.ui.settings.SettingsVersionUpdateDebugFragment

@VersioningFragmentScope
@Subcomponent
internal interface DebugUpdateFragmentComponent {
    fun inject(fragment: SettingsVersionUpdateDebugFragment)
}