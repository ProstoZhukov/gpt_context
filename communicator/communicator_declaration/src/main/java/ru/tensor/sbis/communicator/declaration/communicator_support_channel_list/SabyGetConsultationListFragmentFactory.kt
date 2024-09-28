package ru.tensor.sbis.communicator.declaration.communicator_support_channel_list

import android.os.Parcelable
import androidx.fragment.app.Fragment
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.io.Serializable
import java.util.UUID

/**
 * Фабрика хост фрагмента реестра консультаций сабигет.
 *
 * @author da.zhukov
 */
interface SabyGetConsultationListFragmentFactory : Feature {

    /** SelfDocumented */
    fun createSabyGetChatsListHostFragment(sabyGetOpenChatsParams: SabyGetOpenChatsParams): Fragment
}
@Parcelize
data class SabyGetOpenChatsParams(
    val showLeftPanelOnToolbar: Boolean = true,
    val isBrand: Boolean = false,
    val salePoint: UUID,
    val hasAccordion: Boolean = false
): Serializable, Parcelable