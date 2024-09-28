package ru.tensor.sbis.base_components.fragment

/**@SelfDocumented*/
interface FragmentBackPress {

    /**
     * Обработчик нажатия системной кнопки "назад"
     * возвращает:
     * true если фрагмент сам обработал нажатие на кнопку и обработка из вне НЕ требуется
     * false если требуется обработка нажатия из вне
     *
     * @author au.pervov
     */
    fun onBackPressed(): Boolean

}