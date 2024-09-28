package ru.tensor.sbis.design.folders.support.listeners

import android.content.Context
import androidx.preference.PreferenceManager
import ru.tensor.sbis.design.folders.FoldersView

/**
 * Слушатель обновления данных, который скролирует свернутый список папок вначало,
 * при первой подгрузке данных с кэша контроллера.
 *
 * @author da.zolotarev
 */
class FoldersDataScrollToBeginDataUpdateListener(
    private val context: Context,
    private val foldersView: FoldersView
) : FoldersDataUpdateListener {
    private var isDataSet = false
    private var isFirstLaunch = context.isFirstLaunch()
    override fun updated(isEmpty: Boolean) {
        if (!isFirstLaunch) return
        if (isDataSet) {
            foldersView.compactFoldersViewListPosition = 0
            context.setFirstLaunch(false)
        }
        isDataSet = true
    }

    companion object {
        private const val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"

        private fun getPrefs(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)
        private fun Context.isFirstLaunch() = getPrefs(this).getBoolean(IS_FIRST_LAUNCH, true)
        private fun Context.setFirstLaunch(value: Boolean) =
            getPrefs(this).edit().putBoolean(IS_FIRST_LAUNCH, value).apply()
    }
}
