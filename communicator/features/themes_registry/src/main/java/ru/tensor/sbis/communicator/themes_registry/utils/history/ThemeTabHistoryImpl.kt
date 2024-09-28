package ru.tensor.sbis.communicator.themes_registry.utils.history

import android.content.Context
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.declaration.tab_history.ThemeTabHistory

/**
 * Реализация фичи для сохранения последней открытой вкладки диалогов/каналов.
 *
 * @author dv.baranov
 */
class ThemeTabHistoryImpl(context: Context) : ThemeTabHistory {

    private val prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun saveLastSelectedTab(navxId: String) {
        prefs.edit().putString(LAST_SELECTED_TAB_VALUE, navxId).apply()
    }

    override fun chatsIsLastSelectedTab(): Boolean {
        val navxId = prefs.getString(LAST_SELECTED_TAB_VALUE, "") ?: ""
        return NavxId.CHATS.matches(navxId)
    }
}

private const val SHARED_PREFERENCES_NAME = "THEME_TAB_HISTORY"
private const val LAST_SELECTED_TAB_VALUE = "LAST_SELECTED_TAB_VALUE"
