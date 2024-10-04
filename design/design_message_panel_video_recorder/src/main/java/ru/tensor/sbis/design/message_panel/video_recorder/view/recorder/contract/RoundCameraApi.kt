package ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.video_recorder.view.recorder.RoundCameraView

/**
 * API компонента записи круглых видео [RoundCameraView].
 *
 * @author vv.chekurda
 */
internal interface RoundCameraApi {

    /**
     * Признак необходимости автоматически сбрасывать состояние.
     */
    var autoClearState: Boolean

    /**
     * Проинициализировать контроллер записи круглых видео.
     */
    fun initController(
        fragment: Fragment,
        fileFactory: MediaFileFactory,
        listener: RoundCameraListener?
    )

    /**
     * Начать запись видео.
     */
    fun startRecording(useFrontCamera: Boolean = true)

    /**
     * Остановить запись видео.
     */
    fun stopRecording()

    /**
     * Отменить запись видео.
     */
    fun cancelRecording()

    /**
     * Переключить камеру.
     */
    fun switchCamera()

    /**
     * Выполнить действие [action], если имеются указанные разрешения.
     */
    fun withPermissions(action: () -> Unit)

    /**
     * Очистить состояние.
     */
    fun clearState()

    /**
     * Высвобождение ресурсов.
     */
    fun release()
}