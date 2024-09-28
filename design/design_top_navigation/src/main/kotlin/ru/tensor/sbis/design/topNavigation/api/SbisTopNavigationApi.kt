package ru.tensor.sbis.design.topNavigation.api

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.design.buttons.base.AbstractSbisButton
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.image_loading.BitmapSource

/**
 * API компонента шапка.
 *
 * @author da.zolotarev
 */
interface SbisTopNavigationApi {
    /**
     * Контент главной области шапки.
     *
     * Важно. При смене контента с существующего на новый, необходимо будет заново назначить листенеры и т.п. на
     * кнопку "назад" [SbisTopNavigationView.backBtn].
     */
    var content: SbisTopNavigationContent

    /**
     * Нужно ли показывать кнопку назад или крестик закрытия экрана.
     */
    var showBackButton: Boolean

    /**
     * Доступен ли режим редактирования для `largeTitle`и `smallTitle` контента.
     */
    var isEditingEnabled: Boolean

    /**
     * Максимальное количество строк у заголовка `smallTitle` контента.
     */
    var smallTitleMaxLines: Int

    @Deprecated("Используй rightActions")
    var rightButtons: List<AbstractSbisButton<*, *>>

    @Deprecated("Используй rightActions")
    var rightItems: List<View>

    /**
     * Элементы в правой части области шапки.
     */
    var rightActions: List<SbisTopNavigationActionItem>

    /**
     * Значение счётчика, отображаемое после кнопки назад, показывается только при ее наличии.
     */
    var counter: Int

    /**
     * @see [SbisTopNavigationPresentationContext].
     */
    var presentationContext: SbisTopNavigationPresentationContext

    /**
     * Блок прикладного контента в правой части.
     */
    var customView: View?

    /**
     * Блок прикладного контента в левой части.
     */
    var leftCustomView: View?

    /**
     * Размер левой иконки.
     */
    var leftIconSize: SbisDimen

    /**
     * Состояние индикаторов синхронизации.
     */
    var syncState: SbisTopNavigationSyncState

    /**
     * Положение заголовка (слева или по центру, справа не поддерживается).
     *
     * Нельзя использовать с подзаголовком или многострочным заголовком.
     */
    var titlePosition: HorizontalAlignment

    /**
     * Использовать дизайн старой шапки (синий фон, белая стрелка назад).
     *
     * Важно, данную опцию нужно выставить ПЕРЕД установкой контента [content].
     */
    var isOldToolbarDesign: Boolean

    /**
     * Подвал шапки, располагается под основным контентом шапки.
     */
    var footerItems: List<SbisTopNavigationFooterItem>

    /**
     * Состояние видимости нижнего разделителя.
     */
    var isDividerVisible: Boolean

    /**
     * Состояние прозрачности шапки.
     */
    var isTransparent: Boolean

    /**
     * Уведомляет об изменении содержимого ([content]).
     */
    val contentChanges: Flow<SbisTopNavigationContent>

    /**
     * Обработчик текста, который позволяет изменить текст перед отрисовкой
     */
    var titleTextHandler: SbisTopNavigationTitleHandler?

    /** @SelfDocumented */
    fun setBackgroundColor(color: SbisColor)

    /** @SelfDocumented */
    fun setTitleColor(color: SbisColor)

    /** @SelfDocumented */
    fun setSubTitleColor(color: SbisColor)

    /** @SelfDocumented */
    fun setBackBtnTextColor(color: SbisColor)

    /** @SelfDocumented */
    fun setGraphicBackground(bg: BitmapSource?, roundCorners: Boolean = true)

    /**
     * Подписаться на скролл, переданной view.
     */
    fun attachScrollableView(view: RecyclerView)

    /**
     * Получить настройки кнопки, установленной через [SbisTopNavigationActionItem.IconButton]
     */
    fun getIconButtonViewConfigurator(model: SbisTopNavigationActionItem): SbisTopNavigationIconButtonViewConfigurator?
}