package ru.tensor.sbis.communication_decl.complain

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фрагмент для отправки жалоб на контент.
 *
 * @author da.zhukov
 */
interface ComplainDialogFragmentFeature : Feature {

    /**
     * Показать диалоговое окно для отправки жалобы.
     */
    fun showComplainDialogFragment(fragmentManager: FragmentManager, useCase: ComplainUseCase)
}