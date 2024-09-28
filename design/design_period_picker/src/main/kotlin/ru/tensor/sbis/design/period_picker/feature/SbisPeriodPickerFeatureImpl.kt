package ru.tensor.sbis.design.period_picker.feature

import android.content.Context
import android.view.View
import androidx.annotation.IntRange
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.container.createParcelableFragmentContainer
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.TagAnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.VerticalLocator
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerAnchor
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerHeaderMask
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.view.content_creator.CompactPeriodPickerContainerContentCreator
import ru.tensor.sbis.design.period_picker.view.content_creator.CompactPeriodPickerContentCreator
import ru.tensor.sbis.design.period_picker.view.content_creator.PeriodPickerContainerContentCreator
import ru.tensor.sbis.design.period_picker.view.content_creator.PeriodPickerContentCreator
import ru.tensor.sbis.design.period_picker.view.content_creator.ShortPeriodPickerContainerContentCreator
import ru.tensor.sbis.design.period_picker.view.content_creator.ShortPeriodPickerContentCreator
import ru.tensor.sbis.design.period_picker.view.utils.getFirstDisplayedRange
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.VerticalAlignment
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight.Percent
import ru.tensor.sbis.design_dialogs.movablepanel.PanelWidth
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightParams
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightType.EXPANDED
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightType.HIDDEN
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl.PeekHeightType.INIT
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment
import timber.log.Timber
import java.util.Calendar
import ru.tensor.sbis.design.container.locator.HorizontalAlignment as ContainerHorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment as ContainerVerticalAlignment

/**
 * Реализация фичи компонента Выбор периода [SbisPeriodPickerFeature].
 *
 * @author mb.kruglova
 */
class SbisPeriodPickerFeatureImpl : SbisPeriodPickerFeature {

    @TestOnly
    var isTablet: Boolean? = null

    companion object {
        internal const val shortPeriodPickerFragmentTag = "shortPeriodPickerFragmentTag"
        internal const val compactPeriodPickerFragmentTag = "compactPeriodPickerFragmentTag"
        internal const val periodPickerFragmentTag = "periodPickerFragmentTag"

        internal const val ARG_VISUAL_PARAMS = "VISUAL_PARAMS"
        internal const val ARG_DISPLAYED_RANGE = "DISPLAYED_RANGE"
        internal const val ARG_IS_BOTTOM_POSITION = "IS_BOTTOM_POSITION"
        internal const val ARG_START_VALUE = "START_VALUE"
        internal const val ARG_END_VALUE = "END_VALUE"
        internal const val ARG_IS_ENABLED = "IS_ENABLED"
        internal const val ARG_IS_ONE_DAY_SELECTION = "IS_ONE_DAY_SELECTION"
        internal const val ARG_SELECTION_TYPE = "SELECTION_TYPE"
        internal const val ARG_DAY_TYPE = "DAY_TYPE"
        internal const val ARG_CUSTOM_VIEW = "CUSTOM_VIEW"
        internal const val ARG_IS_COMPACT = "IS_COMPACT"
        internal const val ARG_HEADER_MASK = "HEADER_MASK"
        internal const val ARG_IS_DAY_AVAILABLE = "IS_DAY_AVAILABLE"
        internal const val ARG_PRESET_START_VALUE = "PRESET_START_VALUE"
        internal const val ARG_PRESET_END_VALUE = "PRESET_END_VALUE"
        internal const val ARG_ANCHOR_DATE = "ANCHOR_DATE"
        internal const val ARG_IS_FRAGMENT = "IS_FRAGMENT"
        internal const val ARG_DAY_CUSTOM_THEME = "ARG_DAY_CUSTOM_THEME"
        internal const val ARG_HEIGHT_PERCENT = "HEIGHT_PERCENT"
        internal const val ARG_REQUEST_KEY = "REQUEST_KEY"
        internal const val ARG_RESULT_KEY = "RESULT_KEY"
        internal const val ARG_MODE = "MODE"
        internal const val ARG_DAY_COUNTERS = "DAY_COUNTERS"
    }

    override fun showPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        selectionType: SbisPeriodPickerSelectionType,
        displayedRanges: List<SbisPeriodPickerRange>?,
        isOneDaySelection: Boolean,
        anchors: SbisPeriodPickerAnchor?,
        headerMask: SbisPeriodPickerHeaderMask,
        isBottomPosition: Boolean,
        presetStartValue: Calendar?,
        presetEndValue: Calendar?,
        mode: SbisPeriodPickerMode,
        anchorDate: Calendar?,
        requestKey: String,
        resultKey: String
    ) {
        // Если прикладник задает выбор только одного дня, то в компоненте нельзя выделять периоды.
        val type = if (isOneDaySelection) SbisPeriodPickerSelectionType.Single else selectionType
        // На смартфоне компонент реализован на базе Шторки, на планшете - на базе Контейнера.
        if (isTablet ?: DeviceConfigurationUtils.isTablet(context)) {
            showContainer(
                fragmentManager,
                getPeriodPickerContainer(
                    startValue,
                    endValue,
                    isEnabled,
                    type,
                    getFirstDisplayedRange(displayedRanges),
                    isOneDaySelection,
                    headerMask,
                    isBottomPosition,
                    presetStartValue,
                    presetEndValue,
                    mode,
                    anchorDate,
                    requestKey,
                    resultKey
                ),
                anchors
            )
        } else {
            val creator = PeriodPickerContentCreator(
                startValue,
                endValue,
                isEnabled,
                type,
                getFirstDisplayedRange(displayedRanges),
                isOneDaySelection,
                headerMask,
                isBottomPosition,
                presetStartValue,
                presetEndValue,
                mode,
                anchorDate,
                requestKey,
                resultKey
            )

            showMovablePanel(context, fragmentManager, creator, periodPickerFragmentTag)
        }
    }

    override fun showShortPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        visualParams: SbisShortPeriodPickerVisualParams,
        displayedRanges: List<SbisPeriodPickerRange>?,
        isBottomPosition: Boolean,
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        anchors: SbisPeriodPickerAnchor?,
        anchorDate: Calendar?,
        requestKey: String,
        resultKey: String
    ) {
        // На смартфоне компонент реализован на базе Шторки, на планшете - на базе Контейнера.
        if (isTablet ?: DeviceConfigurationUtils.isTablet(context)) {
            showContainer(
                fragmentManager,
                getShortPeriodPickerContainer(
                    visualParams,
                    getFirstDisplayedRange(displayedRanges),
                    isBottomPosition,
                    startValue,
                    endValue,
                    isEnabled,
                    anchorDate,
                    requestKey,
                    resultKey
                ),
                anchors
            )
        } else {
            val creator = ShortPeriodPickerContentCreator(
                visualParams,
                getFirstDisplayedRange(displayedRanges),
                isBottomPosition,
                startValue,
                endValue,
                isEnabled,
                anchorDate,
                requestKey,
                resultKey
            )

            showMovablePanel(context, fragmentManager, creator, shortPeriodPickerFragmentTag)
        }
    }

    override fun showCompactPeriodPicker(
        context: Context,
        fragmentManager: FragmentManager,
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        selectionType: SbisPeriodPickerSelectionType,
        dayType: SbisPeriodPickerDayType,
        displayedRanges: List<SbisPeriodPickerRange>?,
        customView: ((Context) -> View)?,
        anchors: SbisPeriodPickerAnchor?,
        isDayAvailable: ((Calendar) -> Boolean)?,
        dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
        isBottomPosition: Boolean,
        @IntRange(from = 1, to = 100) heightPercent: Int,
        anchorDate: Calendar?,
        requestKey: String,
        resultKey: String,
        dayCountersRepFactory: SbisPeriodPickerDayCountersRepository.Factory?
    ) {
        // Если прикладник настраивает доступность дней, то в компоненте можно выделять только день, а не период.
        val type = if (isDayAvailable != null) SbisPeriodPickerSelectionType.Single else selectionType
        // На смартфоне компонент реализован на базе Шторки, на планшете - на базе Контейнера.
        if (isTablet ?: DeviceConfigurationUtils.isTablet(context)) {
            showContainer(
                fragmentManager,
                getCompactPeriodPickerContainer(
                    startValue,
                    endValue,
                    isEnabled,
                    type,
                    dayType,
                    getFirstDisplayedRange(displayedRanges),
                    customView,
                    isDayAvailable,
                    dayCustomTheme,
                    isBottomPosition,
                    heightPercent,
                    anchorDate,
                    requestKey,
                    resultKey,
                    dayCountersRepFactory
                ),
                anchors
            )
        } else {
            val creator = CompactPeriodPickerContentCreator(
                startValue,
                endValue,
                isEnabled,
                type,
                dayType,
                getFirstDisplayedRange(displayedRanges),
                customView,
                isDayAvailable,
                dayCustomTheme,
                isBottomPosition,
                anchorDate,
                requestKey,
                resultKey,
                dayCountersRepFactory
            )

            showMovablePanel(context, fragmentManager, creator, compactPeriodPickerFragmentTag)
        }
    }

    /** @SelfDocumented */
    private fun removeFragmentIfExist(
        fragmentManager: FragmentManager,
        tag: String
    ) {
        val checkFragment = fragmentManager.findFragmentByTag(tag)
        if (checkFragment != null) {
            fragmentManager.beginTransaction().remove(checkFragment).performCommit()
        }
    }

    /** @SelfDocumented */
    private fun showMovablePanel(
        context: Context,
        fragmentManager: FragmentManager,
        creator: ContentCreatorParcelable,
        tag: String
    ) {
        if (fragmentManager.isStateSaved) return
        removeFragmentIfExist(fragmentManager, tag)
        val periodPickerFragment = ContainerMovableDialogFragment.Builder()
            .instant(true)
            .setPeekHeightParams(
                listOf(
                    PeekHeightParams(HIDDEN, Percent(0F)),
                    PeekHeightParams(INIT, Percent(0.66F)),
                    PeekHeightParams(EXPANDED, Percent(1F))
                )
            )
            .setAutoCloseable(true)
            .setDefaultHeaderPaddingEnabled(true)
            .setContentCreator(creator)
            .setContainerBackgroundColor(StyleColor.UNACCENTED.getBackgroundColor(context))
            .setIgnoreLock(true)
            .setPanelWidthForLandscape(PanelWidth.CENTER_HALF)
            .build()

        fragmentManager.beginTransaction()
            .add(periodPickerFragment, tag)
            .performCommit()
    }

    /** @SelfDocumented */
    private fun showContainer(
        fragmentManager: FragmentManager,
        container: SbisContainer,
        anchors: SbisPeriodPickerAnchor?
    ) {
        container.dimType = DimType.SOLID
        container.isCloseOnTouchOutside = true

        container.show(
            fragmentManager,
            createHorizontalLocator(anchors),
            createVerticalLocator(anchors)
        )
    }

    /** @SelfDocumented */
    private fun getPeriodPickerContainer(
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        selectionType: SbisPeriodPickerSelectionType,
        displayedRange: SbisPeriodPickerRange,
        isOneDaySelection: Boolean,
        headerMask: SbisPeriodPickerHeaderMask,
        isBottomPosition: Boolean,
        presetStartValue: Calendar?,
        presetEndValue: Calendar?,
        mode: SbisPeriodPickerMode,
        anchorDate: Calendar? = null,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey
    ): SbisContainer {
        return createParcelableFragmentContainer(
            PeriodPickerContainerContentCreator(
                startValue,
                endValue,
                isEnabled,
                selectionType,
                displayedRange,
                isOneDaySelection,
                headerMask,
                isBottomPosition,
                presetStartValue,
                presetEndValue,
                mode,
                anchorDate,
                requestKey,
                resultKey
            )
        )
    }

    /** @SelfDocumented */
    private fun getCompactPeriodPickerContainer(
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        selectionType: SbisPeriodPickerSelectionType,
        dayType: SbisPeriodPickerDayType,
        displayedRange: SbisPeriodPickerRange,
        customView: ((Context) -> View)?,
        isDayAvailable: ((Calendar) -> Boolean)?,
        customDayTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
        isBottomPosition: Boolean,
        heightPercent: Int,
        anchorDate: Calendar?,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey,
        dayCountersRepFactory: SbisPeriodPickerDayCountersRepository.Factory?
    ): SbisContainer {
        return createParcelableFragmentContainer(
            CompactPeriodPickerContainerContentCreator(
                startValue,
                endValue,
                isEnabled,
                selectionType,
                dayType,
                displayedRange,
                customView,
                isDayAvailable,
                customDayTheme,
                isBottomPosition,
                heightPercent,
                anchorDate,
                requestKey,
                resultKey,
                dayCountersRepFactory
            )
        )
    }

    /** @SelfDocumented */
    private fun getShortPeriodPickerContainer(
        visualParams: SbisShortPeriodPickerVisualParams,
        displayedRanges: SbisPeriodPickerRange,
        isBottomPosition: Boolean,
        startValue: Calendar?,
        endValue: Calendar?,
        isEnabled: Boolean,
        anchorDate: Calendar?,
        requestKey: String = periodPickerRequestKey,
        resultKey: String = periodPickerResultKey
    ): SbisContainer {
        return createParcelableFragmentContainer(
            ShortPeriodPickerContainerContentCreator(
                visualParams,
                displayedRanges,
                isBottomPosition,
                startValue,
                endValue,
                isEnabled,
                anchorDate,
                requestKey,
                resultKey
            )
        )
    }

    /** @SelfDocumented */
    private fun createVerticalLocator(anchor: SbisPeriodPickerAnchor?): VerticalLocator {
        return if (anchor != null) {
            TagAnchorVerticalLocator(
                anchorLocator = AnchorVerticalLocator(anchor.verticalAlignment.toContainerVerticalAlignment()),
                anchorTag = anchor.viewTag
            )
        } else {
            ScreenVerticalLocator()
        }
    }

    /** @SelfDocumented */
    private fun createHorizontalLocator(anchor: SbisPeriodPickerAnchor?): HorizontalLocator {
        return if (anchor != null) {
            TagAnchorHorizontalLocator(
                anchorLocator = AnchorHorizontalLocator(anchor.horizontalAlignment.toContainerHorizontalAlignment()),
                anchorTag = anchor.viewTag
            )
        } else {
            ScreenHorizontalLocator()
        }
    }

    /** @SelfDocumented */
    private fun VerticalAlignment?.toContainerVerticalAlignment() = when (this) {
        VerticalAlignment.TOP -> ContainerVerticalAlignment.TOP
        VerticalAlignment.BOTTOM -> ContainerVerticalAlignment.BOTTOM
        else -> ContainerVerticalAlignment.CENTER
    }

    /** @SelfDocumented */
    private fun HorizontalAlignment?.toContainerHorizontalAlignment() = when (this) {
        HorizontalAlignment.LEFT -> ContainerHorizontalAlignment.LEFT
        HorizontalAlignment.RIGHT -> ContainerHorizontalAlignment.RIGHT
        else -> ContainerHorizontalAlignment.CENTER
    }

    /** @SelfDocumented */
    private fun FragmentTransaction.performCommit() {
        try {
            this.commitNow()
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
        }
    }
}