package ru.tensor.sbis.date_picker.current

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.container.SbisContainerImpl
import ru.tensor.sbis.design.header.createContainerHeaderTitled
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable

/**
 * Фабрика создания контента для шторки и контейнера.
 *
 * @author ps.smirnyh
 */
@Parcelize
internal class CurrentPeriodSelectionContentCreator(
    private val selectedPeriod: Period,
    private val visibleCurrentPeriods: List<CurrentPeriod>
) : ContentCreatorParcelable, ContentCreator<CurrentPeriodSelectionContentCreator.CurrentPeriodSelectionContent> {

    /** Создание фрагмента для шторки. */
    override fun createFragment(): Fragment = CurrentPeriodSelectionFragment.newInstance(
        selectedPeriod, visibleCurrentPeriods
    )

    /** Создание контента для контейнера. */
    override fun createContent(): CurrentPeriodSelectionContent = CurrentPeriodSelectionContent()

    internal inner class CurrentPeriodSelectionContent : FragmentContent {

        override fun getFragment(containerFragment: SbisContainerImpl): Fragment = createFragment()

        override fun onRestoreFragment(containerFragment: SbisContainerImpl, fragment: Fragment) = Unit

        override fun getHeaderView(context: Context, container: SbisContainer): View =
            createContainerHeaderTitled(context, R.string.date_picker_select_current_period_title)

        override fun theme(): Int = R.style.SbisContainer_PeriodPicker

        override fun useDefaultHorizontalOffset(): Boolean = false
    }
}

