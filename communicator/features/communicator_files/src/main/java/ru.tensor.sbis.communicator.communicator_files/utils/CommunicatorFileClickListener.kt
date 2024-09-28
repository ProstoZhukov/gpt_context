package ru.tensor.sbis.communicator.communicator_files.utils

import android.view.View
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileActionData

/**
 * Слушатель долго и обычного нажатия на файл.
 *
 * @author da.zhukov.
 */
internal interface CommunicatorFileClickListener {

    /** @SelfDocumented */
    fun onLongClick(view: View, actionData: CommunicatorFileActionData): Boolean

    /** @SelfDocumented */
    fun onClick(actionData: CommunicatorFileActionData)
}