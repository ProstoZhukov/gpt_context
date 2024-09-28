package ru.tensor.sbis.communicator.declaration.crm.providers

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика фрагмента переназначения консультации другому оператору.
 *
 * @author da.zhukov
 */
interface CRMAnotherOperatorFragmentFactory : Feature {

    /**
     * Создать фрагмент переназначения консультации другому оператору.
     *
     * @param params Параметры для открытия экрана переназначения консультации другому оператору.
     */
    fun createCRMAnotherOperatorFragment(params: CRMAnotherOperatorParams): Fragment
}

/**
 * Параметры для открытия экрана переназначения консультации другому оператору.
 *
 * @property consultationId идентификатор консультации(переписки).
 * @property operatorId     идентификатор оператора(его необходимо исклоючить из списка).
 * @property channelId      идентификатор канала.
 * @property channelName    имя канала.
 *
 * @author da.zhukov
 */
@Parcelize
data class CRMAnotherOperatorParams(
    val consultationId: UUID,
    val operatorId: UUID?,
    val channelId: UUID,
    val channelName: String,
    val message: String? = null
) : Parcelable