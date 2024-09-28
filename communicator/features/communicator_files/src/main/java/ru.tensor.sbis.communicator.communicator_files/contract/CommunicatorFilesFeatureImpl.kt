package ru.tensor.sbis.communicator.communicator_files.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesFragment
import java.util.UUID

/**
 * API модуля communicator_files, описывающий предоставляемый модулем функционал.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesFeatureImpl : CommunicatorFilesFeature {

    override fun createCommunicatorFilesListFragment(themeId: UUID): Fragment {
        return CommunicatorFilesFragment.createCommunicatorFilesListFragment(themeId)
    }
}