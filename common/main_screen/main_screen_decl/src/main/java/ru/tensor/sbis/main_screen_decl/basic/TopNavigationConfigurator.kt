package ru.tensor.sbis.main_screen_decl.basic

import android.content.Context
import android.view.View
import ru.tensor.sbis.design.logo.api.SbisLogoType
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationApi
import ru.tensor.sbis.design.topNavigation.api.footer.SbisTopNavigationFooterItem
import ru.tensor.sbis.design.topNavigation.internal_view.SbisTopNavigationFooterView
import ru.tensor.sbis.design.utils.image_loading.BitmapSource
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.main_screen_decl.basic.data.ContentHost
import ru.tensor.sbis.main_screen_decl.basic.data.ContentPlacement
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenEntryPoint
import ru.tensor.sbis.main_screen_decl.basic.data.ScreenId

typealias ProvideContentAction = (Context, BasicMainScreenViewApi) -> BasicContentController

/**
 * API конфигурации шапки в компоненте Раскладка.
 *
 * @author us.bessonov
 */
interface TopNavigationConfigurator {

    /**
     * Отобразить логотип приложения.
     */
    fun setLogo(logoType: SbisLogoType)

    /**
     * Задать, либо сбросить фон шапки.
     */
    fun setBackground(background: BitmapSource? = null, roundCorners: Boolean = true)

    /**
     * Добавить вызывающий элемент, который будет инициировать открытие нового экрана.
     * Элементы, для которых предусмотрено создание новой [View], размещаются в правой части шапки, в порядке вызова
     * метода.
     * Из элементов типа [ScreenEntryPoint.MenuItem] формируется меню, для вызова которого будет добавлена кнопка у
     * правого края шапки.
     *
     * @param entryPoint вызывающий элемент.
     * @param provideContent провайдер экрана, открываемого по клику на вызывающий элемент.
     * @param contentPlacement способ размещения нового экрана.
     */
    fun addScreenEntryPoint(
        entryPoint: ScreenEntryPoint,
        provideContent: ProvideContentAction?,
        contentPlacement: ContentPlacement?
    )

    /**
     * Добавить вызывающий элемент для открытия нового экрана произвольным образом.
     */
    fun addCustomActionEntryPoint(
        entryPoint: ScreenEntryPoint,
        action: (ContentHost) -> Unit,
    )

    /**
     * Скрыть вызывающий элемент.
     */
    fun hideEntryPoint(id: ScreenId)

    /**
     * Показать ранее скрытый вызывающий элемент.
     */
    fun showEntryPoint(id: ScreenId)

    /**
     * Настроить подвал (нижнюю часть) шапки.
     *
     * @param newFooterItems см. [SbisTopNavigationApi.footerItems]. Значение `null` не произведёт никакого эффекта.
     * @param actionWithFooter даёт возможность настроить конкретное содержимое подвала шапки.
     */
    fun configureFooter(
        newFooterItems: List<SbisTopNavigationFooterItem>? = null,
        actionWithFooter: SbisTopNavigationFooterView.() -> Unit = { }
    )

    /**
     * Добавить строку поиска в нижней части шапки и настроить её.
     */
    fun configureSearchFooter(configure: SearchInput.() -> Unit)

    /**
     * Выполнить прикладную конфигурацию шапки.
     */
    fun applyCustomTopNavigationConfiguration(configure: SbisTopNavigationApi.() -> Unit)
}