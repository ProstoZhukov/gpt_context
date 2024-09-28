package ru.tensor.sbis.crud4.hierarchy_component

import androidx.fragment.app.Fragment
import ru.tensor.sbis.crud4.ListComponentViewViewModel

/**
 * Прикладной фрагмент с иерархическим списком.
 *
 * @author ma.kolpakov
 */
abstract class ListComponentFragment<PATH_MODEL> : Fragment() {
    /**
     * Предоставить вью-модель иерархической коллекции
     */
    abstract fun getViewModel(): ListComponentViewViewModel<*,*, *, *, PATH_MODEL, *>

    /**
     * Сменить корень для текущего списка по модели.
     *
     * Можно переопределить для дополнительных настроек списка при переходе в папку.
     */
    open fun changeRoot(parentFolder: PATH_MODEL?) = getViewModel().changeRoot(parentFolder)

    /**
     * Установить приоритетность отображения экрана.
     */
    open fun setPriority(isForegroundFragment: Boolean) = Unit

}
