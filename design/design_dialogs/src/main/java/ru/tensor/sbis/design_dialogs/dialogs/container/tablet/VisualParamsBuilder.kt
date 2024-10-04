package ru.tensor.sbis.design_dialogs.dialogs.container.tablet

import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.design_dialogs.R

/**
 * Builder для создания различных конфигураций отображения контента в [TabletContainerDialogFragment].
 * Упрощает создание набора визуальных параметров [VisualParams]
 *
 * Пример конфигурации для окна фильтра уведомлений:
 * new VisualParamsBuilder()
 * .gravity(Gravity.END)
 * .horizontalMargin()
 * .build()
 *
 * Пример конфигурации для окна создания контактов:
 * new VisualParamsBuilder()
 * .setBoundingRectFromParentFragment()
 * .bottomAnchor(R.id.fab)
 * .build()
 *
 * Пример конфигурации для окна фильтра контактов:
 * new VisualParamsBuilder()
 * .setBoundingRectFromParentFragment()
 * .gravity(Gravity.TOP)
 * .belowActionBar()
 * .build()
 */
class VisualParamsBuilder {

    /**
     * Набор параметров
     */
    private var visualParams = VisualParams()

    /**
     * Позиционирование относительно экрана
     * @param gravity флаг позиционирования
     * @return ссылка на Builder
     */
    fun gravity(gravity: Int): VisualParamsBuilder {
        visualParams.gravity = gravity
        return this
    }

    /**
     * Отступ по бокам
     * @return ссылка на Builder
     */
    fun horizontalMargin(): VisualParamsBuilder {
        visualParams.needHorizontalMargin = true
        return this
    }

    /**
     * Ограничение области контента границами родительского фрагмента
     * @param ensureDefaultMinWidth должна ли обеспечиваться стандартная минимальная ширина окна, независимо от ширины
     * родительского фрагмента
     * @return ссылка на Builder
     */
    @JvmOverloads
    fun setBoundingRectFromParentFragment(ensureDefaultMinWidth: Boolean = true): VisualParamsBuilder {
        visualParams.boundingObject = BoundingObject.fromParentFragment(ensureDefaultMinWidth)
        return this
    }

    /**
     * Ограничение области контента границами целевого фрагмента
     * @param ensureDefaultMinWidth должна ли обеспечиваться стандартная минимальная ширина окна, независимо от ширины
     * целевого фрагмента
     * @return ссылка на Builder
     */
    @JvmOverloads
    fun setBoundingRectFromTargetFragment(ensureDefaultMinWidth: Boolean = true): VisualParamsBuilder {
        visualParams.boundingObject = BoundingObject.fromTargetFragment(ensureDefaultMinWidth)
        return this
    }

    /**
     * Ограничение области контента границами View
     * @param ensureDefaultMinWidth должна ли обеспечиваться стандартная минимальная ширина окна, независимо от ширины
     * указанного View
     * @return ссылка на Builder
     */
    @JvmOverloads
    fun setBoundingRectFromView(viewId: Int, ensureDefaultMinWidth: Boolean = true): VisualParamsBuilder {
        visualParams.boundingObject = BoundingObject.fromView(viewId, ensureDefaultMinWidth)
        return this
    }

    /**
     * Расположение контента ниже области ActionBar-а
     * @return ссылка на Builder
     */
    fun belowActionBar(): VisualParamsBuilder {
        visualParams.overlayActionBar = false
        return this
    }

    fun topAnchor(
        @IdRes topAnchor: Int,
        gravity: AnchorGravity = AnchorGravity.UNSPECIFIED
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createTopAnchor(topAnchor, gravity)
        return this
    }

    fun topWithOverlayAnchor(
        @IdRes topAnchor: Int,
        gravity: AnchorGravity = AnchorGravity.UNSPECIFIED
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createTopWithOverlayAnchor(topAnchor, gravity)
        return this
    }

    fun bottomAnchor(
        @IdRes bottomAnchor: Int,
        gravity: AnchorGravity = AnchorGravity.UNSPECIFIED
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createBottomAnchor(bottomAnchor, gravity)
        return this
    }

    fun autoAnchor(
        anchorTag: String,
        gravity: AnchorGravity = AnchorGravity.CENTER,
        anchorParentTag: String? = null
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createAnchor(AnchorType.AUTO, anchorTag, anchorParentTag, gravity)
        return this
    }

    fun autoWithOverlayAnchor(
        anchorTag: String,
        gravity: AnchorGravity = AnchorGravity.CENTER,
        anchorParentTag: String? = null
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createAnchor(AnchorType.AUTO_WITH_OVERLAY, anchorTag, anchorParentTag, gravity)
        return this
    }

    /**
     * Позволяет использовать в качестве якоря View с заданным тегом. Применимо для случаев, когда на экране может быть
     * более одного View с id якоря
     */
    fun anchorWithTag(
        anchorTag: String,
        anchorType: AnchorType,
        gravity: AnchorGravity = AnchorGravity.UNSPECIFIED
    ): VisualParamsBuilder {
        visualParams.anchor = Anchor.createAnchor(anchorType, anchorTag, null, gravity)
        return this
    }

    /**
     * Позволяет задать фиксированную ширину окна. По умолчанию используется минимальная ширина
     * [R.dimen.design_dialogs_tablet_container_dialog_default_min_width],
     * допускается использование больших значений при необходимости
     */
    @JvmOverloads
    fun fixedWidth(
        @DimenRes width: Int = R.dimen.design_dialogs_tablet_container_dialog_default_min_width
    ): VisualParamsBuilder {
        visualParams.fixedWidth = width
        return this
    }

    fun wrapWidth(isWrap: Boolean): VisualParamsBuilder {
        visualParams.wrapWidth = isWrap
        return this
    }

    fun wrapHeight(isWrap: Boolean): VisualParamsBuilder = apply {
        visualParams.wrapHeight = isWrap
    }

    fun softInputMode(softInputMode: Int): VisualParamsBuilder {
        visualParams.softInputMode = softInputMode
        return this
    }

    fun listenAnchorLayoutAlways(listenAnchorLayoutAlways: Boolean): VisualParamsBuilder {
        visualParams.listenAnchorLayoutAlways = listenAnchorLayoutAlways
        return this
    }

    fun dialogStyle(@StyleRes styleRes: Int): VisualParamsBuilder {
        visualParams.dialogStyle = styleRes
        return this
    }

    /**
     * Получение набора параметров
     * @return набор визуальных параметров
     */
    fun build() = visualParams
}