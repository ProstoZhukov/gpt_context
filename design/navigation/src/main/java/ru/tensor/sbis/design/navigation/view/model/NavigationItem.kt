package ru.tensor.sbis.design.navigation.view.model

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.toolbox_decl.navigation.DefaultNavxIdResolver
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.io.Serializable

/**
 * Неопределённый идентификатор раздела приложения.
 * Отсутствие идентификатора говорит о том, что данные из микросервиса не влияют на видимость пункта, т.к. для него не
 * предусмотрен идентификатор на облаке.
 */
const val UNDEFINED_NAVX_IDENTIFIER = "UNDEFINED"

/**
 * Описание элемента меню.
 *
 * @author ma.kolpakov
 * Создан 11/27/2018
 */
interface NavigationItem : Serializable {

    /**
     * Поток испускающий изменение описания названия элемента меню [NavigationItemLabel].
     */
    @Deprecated(message = "Используйте корутины", replaceWith = ReplaceWith("labelFlow"))
    val labelObservable: Observable<NavigationItemLabel>

    /**
     * Поток испускающий изменение описания иконки элемента меню [NavigationItemIcon].
     */
    @Deprecated(message = "Используйте корутины", replaceWith = ReplaceWith("iconFlow"))
    val iconObservable: Observable<NavigationItemIcon>

    /**
     *Поток испускающий изменение описания названия элемента меню [NavigationItemLabel].
     */
    val labelFlow: Flow<NavigationItemLabel>

    /**
     * Поток испускающий изменение описания иконки элемента меню [NavigationItemIcon].
     */
    val iconFlow: Flow<NavigationItemIcon>

    /**
     * Используется как стабильный id для RecyclerView. При реализации [NavigationItem] в качестве Enum будет работать
     * сразу, иначе нужно в этом свойстве выдавать уникальный одинаковый id для всех [NavigationItem], у которых
     * одинаковый контент.
     */
    val ordinal: Int

    /**
     * Уникальный идентификатор элемента. В том числе по нему будет восстанавливаться выбранная вкладка.
     */
    val persistentUniqueIdentifier: String

    /**
     * Идентификатор элемента, позволяющий определять доступность пункта навигации, согласно настройкам на облаке для
     * клиента СБИС.
     */
    @Suppress("SameReturnValue")
    @Deprecated(
        "Будет удалено по https://online.sbis.ru/opendoc.html?guid=307c8603-08ac-44b8-a0f0-0673ef3c6293&client=3",
        replaceWith = ReplaceWith("navxId")
    )
    val navxIdentifier: String
        get() = UNDEFINED_NAVX_IDENTIFIER

    /**
     * Идентификатор раздела приложения в структуре навигации.
     */
    val navxId: NavxIdDecl?
        get() = DefaultNavxIdResolver.navxIdOf(navxIdentifier)

    /**
     * Название счётчиков для сервиса счётчиков.
     * Для полного понимания что здесь возвращать лучше обратиться к разработчикам контроллера.
     */
    val counterName: String
        get() = persistentUniqueIdentifier
}