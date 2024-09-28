package ru.tensor.sbis.message_panel.recorder.util.listener

import android.app.Activity
import android.content.pm.ActivityInfo
import ru.tensor.sbis.design.swipeback.SwipeBackLayout
import ru.tensor.sbis.recorder.decl.RecorderViewListener

/**
 * Подписка на события звукозаписи, которая управляет активацией swipe back и блокирует поворот
 *
 * @author vv.chekurda
 * Создан 8/9/2019
 */
internal class DefaultRecorderViewListener(
    private val activity: Activity,
    private val swipeBackLayout: SwipeBackLayout?
) : RecorderViewListener {

    override fun onRecordStarted() {
        swipeBackLayout?.setDragEdge(SwipeBackLayout.DragEdge.NONE)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun onRecordCompleted() {
        swipeBackLayout?.setDragEdge(SwipeBackLayout.DragEdge.LEFT)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}