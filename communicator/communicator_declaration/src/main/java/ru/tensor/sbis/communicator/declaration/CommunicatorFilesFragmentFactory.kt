package ru.tensor.sbis.communicator.declaration

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * Фабрика для создания фрагмента файлов по переписке.
 *
 * @author da.zhukov
 */
interface CommunicatorFilesFragmentFactory : Feature {

    /**
     * Создать фрагмент экрана файлов по переписке.
     */
    fun createCommunicatorFilesListFragment(themeId: UUID): Fragment
}