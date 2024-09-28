package ru.tensor.sbis.android_ext_decl.viewprovider

import androidx.fragment.app.Fragment

/**
 * Реализация предоставляет возможность отобразить Fragment поверх всего контента Activity.
 * Типичные задачи:
 * - открытие полноэкранной карточки из списка на телефоне, не используя дополнительную Activity.
 * - отображение своего контента поверх всего контента приложения при невозможности использовать DialogFragment.
 *
 * @author du.bykov
 */
interface OverlayFragmentHolder {

    /**
     * Показать фрагмент [fragment] поверх всего контента. Если там уже есть фрагмент, показанный этим методом,
     * он будет замещен.
     * Если выставлен флаг [swipeable], то фрагмент будет показан с анимацией и возможностью закрыть свайпом.
     * Важно: удаление фрагмента должно происходить только через использование метода [removeFragment]
     */
    fun setFragment(fragment: Fragment, swipeable: Boolean = true)

    /**
     * Задать [fragment], отображаемый поверх контента, с указанием тега.
     * @see setFragment
     */
    fun setFragmentWithTag(fragment: Fragment, swipeable: Boolean = true, tag: String? = null) {
        throw NotImplementedError()
    }

    /**
     * Есть ли уже отображаемый фрагмент поверх всего контента, отображенный методом [setFragment].
     */
    fun hasFragment(): Boolean

    /**
     * Получить текущий фрагмент.
     */
    fun getExistingFragment(tag: String? = null): Fragment? = null

    /**
     * Удалить фрагмент(если он есть), который был отображен поверх всего контента методом [setFragment].
     */
    fun removeFragment()

    /**
     * Выполнить [removeFragment] немедленно.
     */
    fun removeFragmentImmediate() = removeFragment()

    /**
     * Обработать действие "Назад". Если содержит фрагмент который успешно отработает действие через
     * ru.tensor.sbis.base_components.fragment.FragmentBackPress, то вернет true или удалит этот фрагмент и вернет
     * так же true. Если фрагмента не содержит, вернет false.
     */
    fun handlePressed(): Boolean
}