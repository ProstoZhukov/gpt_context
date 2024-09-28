package ru.tensor.sbis.person_decl.motivation.ui

import androidx.fragment.app.Fragment

/**
 * Интерфейс для фрагмента-холдера, умеющего отображать фрагменты на себе поверх всего остального контента
 *
 * Похож на аналогичный для активити, но предоставляет также информацию о необходимости добавления
 * padding-а под status bar
 */
interface FragmentOverlayHolder {
    /**
     * true, если holder предоставляет всё пространство (в том числе статус бар) и если нет
     * соответствующего макета, необходимо сделать отступ
     */
    var needAddDefaultTopPadding: Boolean

    /** Отобразить фрагмент */
    fun showFragment(fragment: Fragment)

    /** Обработать нажатие назад */
    fun handleBackPressed(): Boolean
}

/** @SelfDocumented */
fun Fragment.findFragmentOverlayHolder(): FragmentOverlayHolder? =
    (parentFragment as? FragmentOverlayHolder)
        ?: parentFragment?.findFragmentOverlayHolder()
        ?: (activity as? FragmentOverlayHolder)