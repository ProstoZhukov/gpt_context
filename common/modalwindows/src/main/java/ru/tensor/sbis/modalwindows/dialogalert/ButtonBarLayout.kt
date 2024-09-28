package ru.tensor.sbis.modalwindows.dialogalert

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.LinearLayout

/**
 * Контейнер-расширение [LinearLayout], позволяющий автоматически располагать дочерние элементы
 * в вертикальной ориентации в случае, если хотя бы один из них вынужден обрезать свое содержимое
 * для того, чтобы влезть в один горизонтальный ряд с остальными. В качестве элементов допускается
 * использование [Button]
 */
class ButtonBarLayout : LinearLayout {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        viewTreeObserver.addOnGlobalLayoutListener (object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                if (hasButtonsNeedingSpace()) {
                    orientation = VERTICAL
                }
            }
        })
    }

    private fun hasButtonsNeedingSpace(): Boolean {
        for (i in 0 until childCount) {
            (getChildAt(i) as Button).layout?.apply {
                if (lineCount > 0) {
                    if (getEllipsisCount(lineCount - 1) > 0) {
                        return true
                    }
                }
            }
        }
        return false
    }
}