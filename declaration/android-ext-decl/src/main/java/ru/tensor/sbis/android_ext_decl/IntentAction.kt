package ru.tensor.sbis.android_ext_decl

/**
 * Строковые ключи различного назначения.
 *
 * @author du.bykov
 */
class IntentAction {
    object Extra {
        /**Признак необходимости открытия боковой навигации при старте приложения*/
        const val MAIN_ACTIVITY_OPEN_NAVIGATION_EXTRA = BuildConfig.MAIN_APP_ID + ".MAIN_ACTIVITY.OPEN_NAVIGATION_EXTRA"

        /**Раздел приложения который нужно открыть при старте.*/
        const val NAVIGATION_MENU_POSITION = "NAVIGATION_MENU_POSITION"

        /**Дополнительные аргументы для открываемой страницы приложения.*/
        const val NAVIGATION_MENU_ARGS = "navigation_menu_args"

        /**Категория пуш уведомления для выделения элемента навигации.*/
        const val PUSH_CONTENT_CATEGORY = "push_content_category"

        /**Признак необходимости наличия действий необходимых для планшетного формата.*/
        const val TABLET_ACTION = "tablet_action"

        /**Id контейнера.*/
        const val CONTAINER_ID = "container_id"

        /**Признак необходимости скрыть действия тулбара для экрана переписки.*/
        const val HIDE_TOOLBAR_BUTTON_AND_DELETE_ACTION = "hide_toolbar_button_and_delete_action"

        /**Признак необходимости выполнить добавления фрагмента в транзакции.*/
        const val NEED_TO_ADD_FRAGMENT_TO_BACKSTACK = "need_to_add_fragment_to_backstack"

        /**Признак необходимости показа клавиатуры для экрана переписки.*/
        const val NEED_TO_SHOW_KEYBOARD = "need_to_show_keyboard"

        /**
         * Должно ли срабатывать первоначальное открытие Аккордеона при запуске под автотестами (по умолчанию `false`).
         */
        const val SHOW_ACCORDION_ON_AUTOTESTS_LAUNCH = "SHOW_ACCORDION_ON_AUTOTESTS_LAUNCH"

        /**
         * Восстанавливать ли ранее открытый раздел навигации при перезапуске (по умолчанию `true` - согласно настройке
         * политики сохранения раздела в приложении).
         */
        const val RESTORE_ACTIVE_NAVIGATION_ITEM = "RESTORE_ACTIVE_NAVIGATION_ITEM"
    }
}