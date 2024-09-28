package ru.tensor.sbis.toolbox_decl.share

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuContent
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuItem
import ru.tensor.sbis.toolbox_decl.share.delegates.SharePermissionsChecker

/**
 * Обработчик функциональности "поделиться".
 * @see ShareMenuContent контент, отображаемый в меню. Дает возможность управлять состоянием меню.
 *
 * Пункт [menuItem] будет доступен в меню исходя из следующих условий:
 * 1) Реализация [ShareHandler] поставляется плагинной системе приложения.
 * 2) Опционально: данные для шаринга текущей сессии поддерживаются обработчиком [isShareSupported].
 * По умолчанию [ShareHandler] поддерживает любой тип контента.
 * 3) Опционально: если указанны [navxIds] и хотя бы один доступен
 * в сервисе навигации NavigationService для аккаунта пользователя.
 * По умолчанию [navxIds] формируется из [ShareMenuItem.id].
 * 4) Опционально: если указаны требующиеся разрешения в [permissionScope]
 * и эти права есть у пользователя. Правила проверки прав можно конкретизировать [checkPermission].
 * По умолчанию дополнительных проверок прав не требуется.
 *
 * Также обработчик декларирует доступность опции быстрого шаринга [isQuickShareSupported],
 * которое может происходить по клику на зарегистрированную системную миниатюрку "недавнего",
 * например поделиться с недавним контактом.
 * По умолчанию поддержка опции выключена.
 *
 * Для сохранения обратной совместимости со старым UI интерфейсом доступен опциональный метод [getShareContentIntent],
 * которая будет открываться для пункта [menuItem] на отдельном экране, а не в самом меню.
 *
 * @author vv.chekurda
 */
interface ShareHandler : Feature,
    SharePermissionsChecker {

    /**
     * Элемент навигации, который будет отображаться в списке меню.
     *
     * Для отображения элемента [navxIds] должны быть доступны для пользователя
     * и контент "поделиться" должен быть доступен при запросе [isShareSupported],
     * В ином случае элемент не будет отображаться.
     */
    val menuItem: ShareMenuItem

    /**
     * Набор идентификаторов вкладки,
     * по которым будет определяться доступность использования,
     * исходя из включенных разделов для аккаунта пользователя.
     *
     * Если пункт никак не связан с разделами приложения, например просмотр SabyDoc,
     * то navxIds должен быть null.
     */
    val navxIds: Set<String>?
        get() = setOf(menuItem.id)

    /**
     * Название для аналитики используемых разделов меню шаринга в snake_case.
     * Использовать краткое понятное название раздела без лишних префиксов.
     * Примеры: contacts, my_disk, tasks.
     */
    val analyticHandlerName: String?
        get() = null

    /**
     * Получить признак поддержки [shareData] данным пунктом навигации [menuItem].
     * Если нет - элемент не будет отображаться в меню.
     */
    fun isShareSupported(shareData: ShareData): Boolean = true

    /**
     * Получить признак поддержки прикладного ключа [quickShareKey], который устанавливается в intent для отображения
     * системной миниатюрки.
     * @see QUICK_SHARE_KEY
     */
    fun isQuickShareSupported(quickShareKey: String): Boolean = false

    /**
     * Получить контент-фрагмент для элемента навигации [menuItem].
     *
     * Метод вызывается при клике пользователем на элемент навигации [menuItem] в меню "поделиться",
     * а также для сценариев quick share, чтобы отобразить фрагмент в компоненте меню "поделиться".
     *
     * Для получения доступа в возможностям управления компонентом меню - необходимо имплементировать на фрагмент
     * [ShareMenuContent].
     *
     * @param shareData данные для поделиться.
     * @param quickShareKey ключ для быстрого шаринга.
     */
    fun getShareContent(shareData: ShareData, quickShareKey: String? = null): Fragment? = null

    /**
     * Получить интент экрана для элемента навигации [menuItem].
     *
     * Метод вызывается при клике пользователем на элемент навигации [menuItem] в меню "поделиться",
     * если [getShareContent] отсутствует.
     * Обязательно укажите в [ShareMenuItem.canBeSelected] = false,
     * чтобы данный элемент не был превыбран при открытии меню.
     *
     * @param shareData данные для поделиться.
     * @param quickShareKey ключ для быстрого шаринга.
     */
    fun getShareContentIntent(context: Context, shareData: ShareData, quickShareKey: String? = null): Intent? = null
}

/**
 * Ключ - id, который указывается при создании Shortcut для создания ярлыка Direct-share.
 */
const val QUICK_SHARE_KEY = "android.intent.extra.shortcut.ID"

/**
 * Если вы хотите открыть экран шаринга через динамичные шорткаты, то указывайте в них данную категорию.
 */
const val QUICK_SHARE_SHORTCUT_CATEGORY = "ru.tensor.sbis.droid.experemental.category.TEXT_SHARE_TARGET"