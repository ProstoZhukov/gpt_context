package ru.tensor.sbis.communicator_support_consultation_list.feature

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Контракт для создания фрагмента списка консультация
 */
interface SupportConsultationListFeatureContract {

    /**
     * Получить фрагмент
     */
    fun getFragment() : Fragment

    /**
     * Выбрана консультация
     */
    fun selectedConsultation() : Flow<UUID>

    /**
     * Произошло нажатие на шапку реестра консультаций в сабигете.
     */
    fun onSabyGetTitleClick() : Flow<UUID>
}