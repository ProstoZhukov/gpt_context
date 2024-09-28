package ru.tensor.sbis.master_detail

import androidx.fragment.app.Fragment

/**
 * Реализация интерфейса позволит использовать класс в [MasterDetailFragment] и должна отображать в правильной
 * компоновке Detail фрагмент, а так же удалять его со всеми необходимыми действиями при этом.
 *
 * @author du.bykov
 */
interface DetailFragmentManager {

    /**
     * Отобразить Detail [fragment] поверх Master фрагмента(либо уже отображенного Detail) для телефонной компоновки
     * или сбоку - для планшетной(либо замена уже отображенного).
     * Если выставлен флаг [swipeable], то фрагмент будет показан с анимацией и возможностью закрыть свайпом.
     * Если выставлен флаг [popPreviousFromBackStack], то на планшете будет удаляться 1 элемент из backstack-а
     * details фрагментов перед показом нового detail фрагмента.
     * Добавление фрагмента безопасно в любой момент, т.к. реализация интерфейса гарантирует выполняет добавление
     * фрагмента только в случае возможности этого, либо откладывает добавление до появления этой возможности.
     */
    fun showDetailFragment(
        fragment: Fragment,
        swipeable: Boolean = true,
        tag: String? = null,
        popPreviousFromBackStack: Boolean = true
    )

    /**
     * @see [showDetailFragment]
     * Этот метод позволяет задать использовать созданные разным способом фрагменты
     * для планшета [createFragmentForTablet] и телефона [createFragmentForPhone], чтобы
     * можно было задать специфичные параметры, например, отображение стрелки "назад" в тулбаре.
     */
    fun showDetailFragment(
        createFragmentForPhone: () -> Fragment,
        createFragmentForTablet: () -> Fragment
    )

    /**
     * Удалить ранее добавленного Detail фрагмента методом [showDetailFragment], если какой-либо был добавлен.
     * Удаление фрагмента безопасно в любой момент, т.к. реализация интерфейса гарантирует выполняет удаление
     * фрагмента только в случае возможности этого, либо откладывает удаление до появления этой возможности.
     */
    fun removeDetailFragment()
}