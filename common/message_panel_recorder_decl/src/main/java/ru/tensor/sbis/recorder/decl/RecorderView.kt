package ru.tensor.sbis.recorder.decl

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Панель для записи аудио
 * Расширять данные класс на прикладном уровне вряд ли понадобится. Реализация есть в модуле звукозаписи.
 * В случае реализации необходимо сделать взаимодействие ui панели звукозаписи с параметрами, метода [init]
 *
 * @author ma.kolpakov
 */
abstract class RecorderView(context: Context) : ConstraintLayout(context) {

    /**
     * Инициализация панели аудиозаписи
     *
     * @param recorderService сервис для управления аудиозаписью
     * @param permissionMediator медиатор для запроса разрешений
     * @param recipientMediator медиатор для запроса получателей
     * @param recordingListener слушатель событий аудиозаписи (начало\конец аудиозаписи)
     * @param hintListener слушатель отображения подсказк об аудиозаписи
     */
    abstract fun init(
        recorderService: RecorderService,
        permissionMediator: RecordPermissionMediator,
        recipientMediator: RecordRecipientMediator,
        recordingListener: RecorderViewListener? = null,
        hintListener: RecordViewHintListener? = null
    )
}
