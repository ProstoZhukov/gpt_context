package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.view_ext.gesture.SimpleOnGestureListenerCompat
import timber.log.Timber

/**
 * Обработчик касаний на экране информации о диалоге/канале, помогающий реализовать скрытие/показ при свайпе
 * вью второго участника в переписке 1 на 1. Предоставляет метод скрытия вью участника для специальных действий
 * (нажатие строки поиска, выбор контентного раздела).
 *
 * @author dv.baranov
 */
internal class ConversationInformationTouchHelper(
    context: Context,
    private val appBar: AppBarLayout
) {
    private val gestureListener: SimpleOnGestureListenerCompat =
        object : SimpleOnGestureListenerCompat {
            override fun onFlingCompat(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false

                return try {
                    Timber.v("ConversationInformationView.Motions: $e1 $e2")
                    val swipeDistance = e1.y - e2.y
                    when {
                        swipeDistance > SWIPE_MINIMAL_DISTANCE -> hideSingleParticipantView()
                        swipeDistance < -SWIPE_MINIMAL_DISTANCE -> showSingleParticipantView()
                    }
                    super.onFlingCompat(e1, e2, velocityX, velocityY)
                } catch (ex: Exception) {
                    Timber.e("ConversationInformationTouchHelper, Motions: $e1 $e2 \n $ex")
                    false
                }
            }
        }

    /** Спрятать вью второго участника в переписке 1 на 1, при этом вкладки с контентом поднимутся к тулбару. */
    fun hideSingleParticipantView() {
        appBar.setExpanded(false, true)
    }

    /** Показать вью второго участника в переписке 1 на 1, при этом вкладки с контентом поднимутся к тулбару. */
    fun showSingleParticipantView() {
        appBar.setExpanded(true, true)
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    /** @SelfDocumented */
    fun onTouchEvent(event: MotionEvent) = gestureDetector.onTouchEvent(event)
}

private const val SWIPE_MINIMAL_DISTANCE = 200f