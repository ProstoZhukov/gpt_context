package ru.tensor.sbis.communicator.communicator_files.mapper

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFileData
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesItemView
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFileClickListener

/**
 * Класс `CommunicatorFilesHolder` представляет собой ViewHolder для элементов RecyclerView,
 * используемый для отображения списка вложений в коммуникационном модуле.
 *
 * @property context Контекст, используемый для доступа к ресурсам и другим аспектам приложения.
 * @property view Представление элемента.
 *
 * @author da.zhukov
 */
internal class CommunicatorFilesHolder(
    private val context: Context,
    private val view: CommunicatorFilesItemView,
) : RecyclerView.ViewHolder(view) {

    fun bind(data: CommunicatorFileData) {
        view.apply {
            setData(data)
        }
    }
}