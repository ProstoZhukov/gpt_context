package ru.tensor.sbis.design.period_picker.view.listener

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.period_picker.view.utils.heightFraction
import ru.tensor.sbis.design_dialogs.movablepanel.MovablePanel

/**
 * Слушатель изменении видимости компонента Выбор Периода в дереве представлений,
 * который позволяет поддержать выполнение действия тогда и только тогда,
 * когда шторка/контейнер явно отображены на экране.
 *
 * @author mb.kruglova
 */
internal class CalendarGlobalLayoutListener(
    private val parentView: View?,
    private val calendar: RecyclerView,
    private val isBottomPosition: Boolean,
    private val handleAction: (Boolean) -> Unit,
    @TestOnly private val coordinate: Int? = null
) : ViewTreeObserver.OnGlobalLayoutListener {

    override fun onGlobalLayout() {
        if (parentView is MovablePanel) {
            val panelHeight = parentView.getPanelHeight()
            if (panelHeight > 0) {
                val panelY = parentView.getPanelY()
                val calendarHeight = panelHeight - panelY
                val calendarFraction = calendarHeight * 100 / panelHeight
                handleAction(
                    if (isBottomPosition) {
                        /* Для позиции снизу, необходимо убедиться, что поднятие шторки завершено,
                        иначе календарь отобразится не на нужной дате. */
                        calendarFraction == (heightFraction * 100).toInt() || calendarFraction == 100
                    } else {
                        calendarFraction > 0
                    }
                )
            }
        } else {
            with(calendar) {
                val location = IntArray(2)
                this.getLocationOnScreen(location)
                val yCoordinate = coordinate ?: location[1]

                if (
                    yCoordinate > 0 && this.height > 0 &&
                    (this.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() > -1
                ) {
                    handleAction(true)
                }
            }
        }
    }
}