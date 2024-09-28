package ru.tensor.sbis.design_selection.ui.content.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import javax.inject.Inject

/**
 * Вспомогательная реализация для отслеживания видимости контента компонента выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionContentStackHelper @Inject constructor(
    private val fragment: Fragment,
    private val folderItem: SelectionFolderItem?
) {

    /**
     * Слушатель изменений состояния стека контента.
     */
    fun interface OnStackChangeListener {

        /**
         * Изменился стек контента.
         *
         * @param isContentVisible признак видимости текущего контента для пользователя.
         */
        fun onStackChanged(isContentVisible: Boolean)
    }

    private var listener: OnStackChangeListener? = null
    private var skipFirstCallback = false
    private val backStackChangeListener = FragmentManager.OnBackStackChangedListener {
        if (!fragment.isAdded || skipFirstCallback) {
            skipFirstCallback = false
        } else {
            this.listener?.onStackChanged(isContentVisible = isContentVisible)
        }
    }

    /**
     * Признак видимости текущего контента для пользователя.
     */
    val isContentVisible: Boolean
        get() = fragment.isAdded && fragment.parentFragmentManager.fragments.lastOrNull() === fragment

    /**
     * Инициализировать хелпер для начала работы.
     *
     * @param savedInstanceState сохраненное состояние фрагмента.
     * @param listener слушатель событий изменения стека.
     */
    fun init(savedInstanceState: Bundle?, listener: OnStackChangeListener) {
        this.listener = listener
        // Для папок должен быть пропуск первого колбэка по добавлению текущего фрагмента.
        skipFirstCallback = savedInstanceState == null && folderItem != null
        fragment.parentFragmentManager.addOnBackStackChangedListener(backStackChangeListener)
    }

    /**
     * Очистить, завершив работу хелпера.
     */
    fun clear() {
        fragment.parentFragmentManager.removeOnBackStackChangedListener(backStackChangeListener)
        listener = null
    }
}