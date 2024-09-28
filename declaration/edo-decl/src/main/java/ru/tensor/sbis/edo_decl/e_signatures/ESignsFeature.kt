package ru.tensor.sbis.edo_decl.e_signatures

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Публичная функциональность модуля
 *
 * @author sa.nikitin
 */
interface ESignsFeature : Feature {

    /**
     * Создать новый фрагмент списка электронных подписей
     *
     * @param withNavigation    Включить ли навигацию "назад" на фрагменте
     *                          Если true, то в тулбаре будет кнопка для перехода "назад"
     *                          Обычно false для планшетов
     */
    fun newESignsFragment(withNavigation: Boolean): Fragment
}