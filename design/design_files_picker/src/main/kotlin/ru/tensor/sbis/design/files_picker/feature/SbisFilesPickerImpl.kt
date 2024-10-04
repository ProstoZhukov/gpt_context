package ru.tensor.sbis.design.files_picker.feature

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.createParcelableFragmentContainer
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenVerticalLocator
import ru.tensor.sbis.design.files_picker.SbisFilesPickerPlugin
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerPresentationParams
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerEvent
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerTab
import ru.tensor.sbis.design.files_picker.decl.SbisPickedItem
import ru.tensor.sbis.design.files_picker.feature.content_creator.SbisFilesPickerContainerContentCreator
import ru.tensor.sbis.design.files_picker.feature.content_creator.SbisFilesPickerMovablePanelContentCreator
import ru.tensor.sbis.design.files_picker.view.logAttachProcess
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanelPeekHeight
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDelegateImpl
import ru.tensor.sbis.modalwindows.movable_container.ContainerMovableDialogFragment

/**
 * Реализация фичи компонента Выбор файла.
 *
 * @author ai.abramenko
 */

internal class SbisFilesPickerImpl(
    private val key: String?,
    private val storeOwnerClass: Class<Any>
) : ViewModel(), SbisFilesPicker {

    companion object {
        private const val MOVABLE_PANEL_TAG = "SbisFilesPickerImpl.MOVABLE_PANEL_TAG"
        private const val CONTAINER_TAG = "SbisFilesPickerImpl.CONTAINER_TAG"
    }

    override val events = MutableSharedFlow<SbisFilesPickerEvent>(extraBufferCapacity = 1)

    private val defaultPresentationParams
        get() = SbisFilesPickerPresentationParams(
            horizontalLocator = ScreenHorizontalLocator(),
            verticalLocator = ScreenVerticalLocator()
        )

    fun onUnitsSelected(
        selectedItems: List<SbisPickedItem>,
        compressImages: Boolean
    ) {
        viewModelScope.launch {
            logAttachProcess("onUnitsSelected, items - $selectedItems")
            events.waitingEmit(
                SbisFilesPickerEvent.OnItemsSelected(
                    selectedItems = selectedItems,
                    compressImages = compressImages
                )
            )
        }
    }

    override fun show(
        fragmentManager: FragmentManager,
        tabs: Set<SbisFilesPickerTab>,
        presentationParams: SbisFilesPickerPresentationParams?
    ) {
        when {
            DeviceConfigurationUtils.isTablet(SbisFilesPickerPlugin.application) ->
                showContainer(
                    fragmentManager = fragmentManager,
                    tabs = tabs,
                    presentationParams = presentationParams ?: defaultPresentationParams,
                    featureKey = key
                )
            else ->
                showMovablePanel(
                    fragmentManager = fragmentManager,
                    tabs = tabs,
                    featureKey = key
                )
        }
    }

    /**
     * Показать компонент в шторке.
     */
    private fun showMovablePanel(
        fragmentManager: FragmentManager,
        tabs: Set<SbisFilesPickerTab>,
        featureKey: String?
    ) {
        if (fragmentManager.findFragmentByTag(MOVABLE_PANEL_TAG) != null) {
            return
        }

        val creator = SbisFilesPickerMovablePanelContentCreator(tabs, featureKey, storeOwnerClass)
        val dialogFragment =
            ContainerMovableDialogFragment.Builder()
                .instant(true)
                .setPeekHeightParams(
                    listOf(
                        ContainerMovableDelegateImpl.PeekHeightParams(
                            ContainerMovableDelegateImpl.PeekHeightType.HIDDEN,
                            MovablePanelPeekHeight.Percent(0F)
                        ),
                        ContainerMovableDelegateImpl.PeekHeightParams(
                            ContainerMovableDelegateImpl.PeekHeightType.INIT,
                            MovablePanelPeekHeight.Percent(0.75F)
                        ),
                        ContainerMovableDelegateImpl.PeekHeightParams(
                            ContainerMovableDelegateImpl.PeekHeightType.EXPANDED,
                            MovablePanelPeekHeight.Percent(1F)
                        ),
                    )
                )
                .setAutoCloseable(true)
                .setDefaultHeaderPaddingEnabled(true)
                .setContentCreator(creator)
                .setIgnoreLock(true)
                .build()

        fragmentManager.beginTransaction()
            .add(dialogFragment, MOVABLE_PANEL_TAG)
            .commitAllowingStateLoss()
    }

    /**
     * Показать компонент в контейнере.
     */
    private fun showContainer(
        fragmentManager: FragmentManager,
        tabs: Set<SbisFilesPickerTab>,
        presentationParams: SbisFilesPickerPresentationParams,
        featureKey: String?
    ) {
        if (fragmentManager.findFragmentByTag(CONTAINER_TAG) != null) {
            return
        }
        createParcelableFragmentContainer(
            contentCreator = SbisFilesPickerContainerContentCreator(
                tabs = tabs,
                featureKey = featureKey,
                storeOwnerClass = storeOwnerClass
            ),
            tag = CONTAINER_TAG
        )
            .apply {
                isAnimated = true
                dimType = DimType.SOLID
                isCloseOnTouchOutside = true
            }
            .show(
                fragmentManager = fragmentManager,
                horizontalLocator = presentationParams.horizontalLocator,
                verticalLocator = presentationParams.verticalLocator
            )
    }
}