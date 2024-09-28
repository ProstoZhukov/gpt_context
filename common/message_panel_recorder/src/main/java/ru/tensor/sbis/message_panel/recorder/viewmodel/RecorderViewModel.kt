package ru.tensor.sbis.message_panel.recorder.viewmodel

import io.reactivex.disposables.Disposable
import ru.tensor.sbis.recorder.decl.RecorderView

/**
 * View model для [RecorderView]
 *
 * @author vv.chekurda
 * @since 7/25/2019
 */
internal interface RecorderViewModel : Disposable {

    /**
     * Реакция на нажатие кнопки записи
     */
    fun onIconClick()

    /**
     * Реакция на удержание нажатие кнопки записи
     *
     * @return `true`, если запись началась
     */
    fun onIconLongClick(): Boolean

    /**
     * Реакция на отпускание кнопки записи
     *
     * @throws IllegalStateException если вызов предшествует [onIconLongClick]
     */
    fun onIconReleased()

    /**
     * Реакция на смещение пальца с кнопки записи (без отпускания)
     *
     * @throws IllegalStateException если вызов предшествует [onIconLongClick]
     */
    fun onOutOfIcon(fingerOutOfIcon: Boolean)
}