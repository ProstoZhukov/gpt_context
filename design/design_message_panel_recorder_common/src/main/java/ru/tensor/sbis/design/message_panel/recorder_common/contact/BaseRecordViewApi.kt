package ru.tensor.sbis.design.message_panel.recorder_common.contact

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.StateFlow
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData

/**
 * Базове API компонента записи.
 *
 * @author vv.chekurda
 */
interface BaseRecordViewApi<STATE, RESULT> :
    MessagePanelRecordViewDelegate,
    AdjustResizeHelper.KeyboardEventListener {

    /**
     * Слушатель результата записи.
     */
    var resultListener: RecordResultListener<RESULT>?

    /**
     * Состояние компонента записи.
     */
    val state: StateFlow<STATE>

    /**
     * Инициализировать компонент записи.
     */
    fun init(
        fragment: Fragment,
        fileFactory: MediaFileFactory? = null,
        customPlayer: MediaPlayer? = null
    )

    /**
     * Начать запись.
     *
     * @param lockRecord true, если необходимо закрепить запись,
     * иначе пользователь будет управлять записью зажатием через события касаний.
     * @param animateHiding анимировать скрытие панели записи после окончания.
     */
    fun startRecording(
        lockRecord: Boolean = true,
        animateHiding: Boolean = true
    )

    /**
     * Остановить запись.
     */
    fun stopRecording()

    /**
     * Отменить запись.
     */
    fun cancelRecording()

    /**
     * Очистить компонент.
     * Отменяет текущую запись.
     */
    fun release()
}