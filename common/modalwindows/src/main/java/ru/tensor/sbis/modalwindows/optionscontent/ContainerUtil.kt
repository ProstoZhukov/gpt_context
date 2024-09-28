package ru.tensor.sbis.modalwindows.optionscontent

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.design_dialogs.dialogs.container.base.BaseContainerDialogFragment
import ru.tensor.sbis.design_dialogs.dialogs.container.bottomsheet.ContainerBottomSheet
import ru.tensor.sbis.design_dialogs.dialogs.container.tablet.*
import ru.tensor.sbis.design_dialogs.dialogs.content.BaseContentCreator
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreator
import ru.tensor.sbis.modalwindows.R

/**
 * Утилитный класс-хелпер для создания контейнера в зависимости от конфигурации устройства.
 *
 * @author sr.golovkin
 */
@Suppress("MemberVisibilityCanBePrivate")
object ContainerUtil {

    /**
     * Показать контент внутри диалогового окна в зависимости от конфигурации устройства
     * @param[context] контекст
     * @param[contentCreator] Экземпляр создателя контента
     * @param[fragmentManager] менеджер фрагментов, который будет осуществлять операцию добавления
     * @param[isInstant] true - отображение контента сразу, false - по сигналу через интерфейс [ContentCreator]. По умолчанию true.
     * @param[fragmentTag] тэг фрагмента, с которым он будет показан
     * @param[customVisualParams] **(Опционально)** Визуальные параметры контента на случай, если необходима специфическая настройка вью
     * @param[viewTag] **(Опционально)** тэг вью, к которой мы хотим прицепиться посредством [Anchor]
     * @param[viewId] **(Опционально)** идентификатор вью, к которой мы хотим прицепиться посредством [Anchor]. **МОЖНО ПЕРЕДАВАТЬ ТОЛЬКО ОДИН АТТРИБУТ - [viewId] или [viewTag]**
     * @param[forceStandardWindowSizeOnTablet] - Признак, уведомляющий о необходимости отображения требуемого контента на планшете по стандарту - стандартного размера (320/640px)
     * Для активации тледует указывать [fixedWidthRes] = null
     */
    @JvmStatic
    @JvmOverloads
    fun showInContainer(
        context: Context,
        contentCreator: BaseContentCreator,
        fragmentManager: FragmentManager,
        isInstant: Boolean = true,
        fragmentTag: String?,
        customVisualParams: VisualParams? = null,
        viewTag: String? = null,
        @IdRes viewId: Int = View.NO_ID,
        @DimenRes fixedWidthRes: Int? = null,
        addToBackStack: Boolean = false,
        sheetSoftInputMode: Int? = null,
        forceStandardWindowSizeOnTablet: Boolean = false,
        listenAnchorLayoutAlways: Boolean = false
    ) {
        getInContainer(
            context,
            contentCreator,
            fragmentManager,
            isInstant,
            sheetSoftInputMode,
            fragmentTag,
            customVisualParams,
            viewTag,
            viewId,
            fixedWidthRes,
            forceStandardWindowSizeOnTablet,
            listenAnchorLayoutAlways
        ).apply {
            if (addToBackStack) {
                show(fragmentManager.beginTransaction().addToBackStack(fragmentTag), fragmentTag)
            } else {
                show(fragmentManager, fragmentTag)
            }
        }
    }

    /**
     * Получить готовое диалоговое окно без вызова [DialogFragment.show]
     * Если в указанном [fragmentManager] уже есть диалог с указанным [fragmentTag] - этот диалог будет закрыт вызовом [DialogFragment.dismissAllowingStateLoss]
     * @param[context] контекст
     * @param[contentCreator] Экземпляр создателя контента
     * @param[fragmentManager] менеджер фрагментов, который будет осуществлять операцию добавления
     * @param[isInstant] true - отображение контента сразу, false - по сигналу через интерфейс [ContentCreator]. По умолчанию true.
     * @param[fragmentTag] тэг фрагмента, с которым он будет показан
     * @param[customVisualParams] **(Опционально)** Визуальные параметры контента на случай, если необходима специфическая настройка вью
     * @param[viewTag] **(Опционально)** тэг вью, к которой мы хотим прицепиться посредством [Anchor]
     * @param[viewId] **(Опционально)** идентификатор вью, к которой мы хотим прицепиться посредством [Anchor]. **МОЖНО ПЕРЕДАВАТЬ ТОЛЬКО ОДИН АТТРИБУТ - [viewId] или [viewTag]**
     * @param[forceStandardWindowSizeOnTablet] - Признак, уведомляющий о необходимости отображения требуемого контента на планшете по стандарту - стандартного размера (320/640px)
     * Для активации тледует указывать [fixedWidthRes] = null

     */
    fun getInContainer(
        context: Context,
        contentCreator: BaseContentCreator,
        fragmentManager: FragmentManager,
        isInstant: Boolean = true,
        sheetSoftInputMode: Int?,
        fragmentTag: String?,
        customVisualParams: VisualParams? = null,
        viewTag: String? = null,
        @IdRes viewId: Int = View.NO_ID,
        @DimenRes fixedWidthRes: Int? = null,
        forceStandardWindowSizeOnTablet: Boolean = false,
        listenAnchorLayoutAlways: Boolean = false
    ): DialogFragment {
        return prepareNewDialog(
            fragmentManager = fragmentManager,
            fragmentTag = fragmentTag,
            newContainer = createContainer(
                DeviceConfigurationUtils.isTablet(
                    context
                ),
                isInstant = isInstant,
                viewTag = viewTag,
                viewId = viewId,
                contentCreator = contentCreator,
                customVisualParams = customVisualParams,
                fixedWidthRes = fixedWidthRes,
                sheetSoftInputMode = sheetSoftInputMode,
                forceStandardWindowSizeOnTablet = forceStandardWindowSizeOnTablet,
                listenAnchorLayoutAlways = listenAnchorLayoutAlways
            )
        )
    }

    private fun prepareNewDialog(
        fragmentManager: FragmentManager,
        fragmentTag: String?,
        newContainer: BaseContainerDialogFragment
    ): DialogFragment {
        var containerDialog = fragmentManager.findFragmentByTag(fragmentTag) as? BaseContainerDialogFragment
        containerDialog?.dismissAllowingStateLoss()
        containerDialog = newContainer
        return containerDialog

    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun createContainer(
        isTablet: Boolean = false,
        isInstant: Boolean = true,
        sheetSoftInputMode: Int?,
        viewTag: String? = null,
        @IdRes viewId: Int = View.NO_ID,
        @DimenRes fixedWidthRes: Int? = null,
        contentCreator: BaseContentCreator,
        customVisualParams: VisualParams? = null,
        listenAnchorLayoutAlways: Boolean,
        forceStandardWindowSizeOnTablet: Boolean
    ): BaseContainerDialogFragment {
        return (if (isTablet) {
            TabletContainerDialogFragment().apply {
                setInstant(isInstant)
                when {
                    viewTag == null && viewId == View.NO_ID && customVisualParams != null -> {
                        setVisualParams(customVisualParams)
                        return@apply
                    }
                    viewTag != null -> {
                        setVisualParams(
                            createVisualParams(
                                viewTag,
                                viewTag,
                                fixedWidthRes,
                                forceStandardWindowSizeOnTablet,
                                listenAnchorLayoutAlways
                            )
                        )
                        return@apply
                    }
                    viewId != View.NO_ID -> {
                        setVisualParams(createVisualParams(viewId))
                        return@apply
                    }
                }
            }
        } else {
            ContainerBottomSheet().apply {
                instant(isInstant)
                sheetSoftInputMode?.let {
                    softInputMode(sheetSoftInputMode)
                }
            }
        } as BaseContainerDialogFragment).apply {
            setContentCreator(contentCreator)
        }
    }

    /**
     * Создать визуальные параметры для диалогового окна
     * @param[viewTag] тэг для привязки диалогового окна и контекста, его вызывающего с помощью [Anchor]
     * @param[viewParentTag] будет использован, если не удастся найти view по [viewTag]
     * @param[forceStandardWindowSizeOnTablet] устанавливает стандартный размер и рапсположение окна, если не указан иной размер
     */
    private fun createVisualParams(
        viewTag: String?,
        viewParentTag: String? = null,
        widthRes: Int? = null,
        forceStandardWindowSizeOnTablet: Boolean,
        listenAnchorLayoutAlways: Boolean
    ): VisualParams {
        return VisualParams(listenAnchorLayoutAlways = listenAnchorLayoutAlways).apply {
            //ошибка по позиционированию относительно parent fragment https://online.sbis.ru/opendoc.html?guid=76fcb5d2-6ecf-488b-8f0b-aa617bf395e9
            widthRes?.let { fixedWidth = widthRes } ?: run {
                if (forceStandardWindowSizeOnTablet) {
                    fixedWidth = R.dimen.modalwindows_standard_modal_window_width
                    needHorizontalMargin = true
                } else {
                    boundingObject = BoundingObject.fromParentFragment(ensureDefaultMinWidth = false)
                }
            }
            if (viewTag != null && viewParentTag != null) {
                anchor = Anchor.createAnchor(
                    AnchorType.AUTO_WITH_OVERLAY_IF_NOT_ENOUGH_SPACE,
                    viewTag,
                    viewParentTag,
                    if (forceStandardWindowSizeOnTablet) AnchorGravity.END else AnchorGravity.CENTER
                )
            }
        }
    }

    /**
     * Создать визуальные параметры для диалогового окна
     * @param[id] идентификатор [View], которая будет являться якорем для диалогового окна
     */
    private fun createVisualParams(@IdRes id: Int): VisualParams {
        return VisualParams().apply {
            anchor = Anchor.createTopAnchor(id, AnchorGravity.CENTER)
        }
    }
}