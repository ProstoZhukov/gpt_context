package ru.tensor.sbis.app_file_browser.feature

import android.content.Context
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData

/**
 * Фича компонента браузера файлов и папок приложения на устройстве.
 *
 * @author us.bessonov
 */
interface AppFileBrowserFeature {

    /**
     * Предоставляет актуальный список путей до выбранных файлов.
     */
    val selectedFiles: LiveData<Set<String>>

    /**
     * Событие закрытия панели.
     */
    val panelCloseEvent: LiveData<Unit>

    /**
     * Возвращает строку с текущим общим размером выбранных файлов.
     */
    fun getSelectedTotalSize(): String

    /**
     * Позволяет сбросить выбранные элементы.
     * По умолчанию, выбор сохраняется в пределах жизненного цикла экрана, из которого открывается файловый браузер.
     */
    fun reset()

    /**
     * Отображает файловый браузер.
     */
    fun show(context: Context, fragmentManager: FragmentManager, @IdRes containerViewId: Int)
}