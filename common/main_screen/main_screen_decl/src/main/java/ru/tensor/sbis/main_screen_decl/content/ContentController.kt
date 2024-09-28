package ru.tensor.sbis.main_screen_decl.content

import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageData
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Интерфейс для управления контентом в контейнере
 *
 * @author kv.martyshenko
 */
interface ContentController {

    /**
     * Метод для активации элемента после восстановления экрана.
     *
     * @param navigationItem активный элемент
     * @param mainScreen компонент главного экрана
     * @param contentContainer информация о контенте
     */
    fun restore(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для выполнения обновления контента (после пуш-уведомления/диплинка).
     *
     * @param navigationItem активный элемент.
     * @param entryPoint точка входа, с указанием дополнительных параметров.
     * @param mainScreen компонент главного экрана.
     * @param contentContainer информация о контенте.
     */
    fun update(
        navigationItem: NavigationItem,
        entryPoint: EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для активации элемента.
     *
     * @param selectionInfo информация о выбранном элементе
     * @param mainScreen компонент главного экрана
     * @param contentContainer информация о контенте
     * @param transaction текущая транзакция
     */
    fun select(
        selectionInfo: SelectionInfo,
        mainScreen: MainScreen,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction
    )

    /**
     * Метод для активации вложенного элемента (вкладки).
     */
    fun selectSubScreen(
        navxId: NavxIdDecl,
        entryPoint: EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    /**
     * Метод для перевыбора того же элемента.
     *
     * @param navigationItem активный элемент.
     * @param entryPoint точка входа, с указанием дополнительных параметров.
     * @param mainScreen компонент главного экрана.
     * @param contentContainer информация о контенте.
     */
    @Suppress("EmptyMethod")
    fun reselect(
        navigationItem: NavigationItem,
        entryPoint: EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для деактивации элемента.
     *
     * @param selectionInfo информация о выбранном элементе
     * @param mainScreen компонент главного экрана
     * @param contentContainer информация о контенте
     * @param transaction текущая транзакция
     */
    fun deselect(
        selectionInfo: SelectionInfo,
        mainScreen: MainScreen,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction
    )

    /**
     * Метод для обработки действий назад.
     *
     * @param mainScreen
     * @param contentContainer
     */
    fun backPressed(
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ): Boolean

    /**
     * Метод для обработки перехода в режим [Lifecycle.State.STARTED].
     *
     * @param navigationItem текущий выбранный элемент
     * @param mainScreen
     * @param contentContainer
     */
    fun start(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для обработки перехода в режим [Lifecycle.State.RESUMED].
     *
     * @param navigationItem текущий выбранный элемент
     * @param mainScreen
     * @param contentContainer
     */
    fun resume(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для обработки перехода в режим `Lifecycle.PAUSED`.
     *
     * @param navigationItem текущий выбранный элемент
     * @param mainScreen
     * @param contentContainer
     */
    fun pause(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Метод для обработки перехода в режим `Lifecycle.STOPPED`.
     *
     * @param navigationItem текущий выбранный элемент
     * @param mainScreen
     * @param contentContainer
     */
    fun stop(
        navigationItem: NavigationItem,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    )

    /**
     * Информация о выбранном элементе.
     *
     * @param newSelectedItem выбранный элемент.
     * @param oldSelectedItem предыдущий выбранный элемент.
     * @param entryPoint точка входа.
     * @param pageData свойства страницы МП, если доступны.
     */
    class SelectionInfo(
        val newSelectedItem: NavigationItem,
        val oldSelectedItem: NavigationItem?,
        val entryPoint: EntryPoint,
        val pageData: NavigationPageData? = null
    )

    /**
     * Событие нажатия на активный таб навигации
     * @param navigationItem текущий выбранный элемент
     * @param contentContainer информация о контенте
     */
    fun activeTabClicked(
        navigationItem: NavigationItem,
        contentContainer: ContentContainer,
    )

    /**
     * Вызывается по завершении транзакции выбора текущего пункта навигации.
     * В этом методе безопасно выполнять перевыбор пункта.
     */
    fun onSelectionFinished() = Unit

    /**
     * Оповещает об изменении видимости пункта меню.
     */
    fun onItemVisibilityChanged(item: NavigationItem, isVisible: Boolean)

    /**
     * Оповещает о доступности [NavigationPageData] для связанного раздела.
     */
    fun onPageDataAvailable(
        pageData: NavigationPageData,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) = Unit

    /**
     * Точка входа
     */
    interface EntryPoint

    /**
     * Попадание через клик элемента меню
     *
     * @param byUser true если выбор элемента пользователем, false если программный выбор
     */
    @Suppress("MemberVisibilityCanBePrivate")
    class MenuClick(
        val byUser: Boolean = false
    ) : EntryPoint

    /**
     * Смена выбора, в связи с изменением структуры навигации приложения.
     */
    object NavigationChangeEvent : EntryPoint
}