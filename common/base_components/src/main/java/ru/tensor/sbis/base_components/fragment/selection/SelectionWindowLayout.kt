package ru.tensor.sbis.base_components.fragment.selection

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.base_components.R


/**
 * Режим работы макета не известен и будет вычислен на этапе onMeasure.
 */
private const val MODE_UNKNOWN = -1
/**
 * Режим работы макета - во весь экран. Если флаг [SelectionWindowLayout.isHoodScrollingEnabled] - true,
 * то шапка будет скроллиться.
 */
private const val MODE_FULLSCREEN = 0
/**
 * Режим работы макета во весь экран без возможности скролла шапки.
 */
private const val MODE_SCROLL_CONTENT_ONLY = 1
/**
 * Режим работы макета в качестве нижней панели (высота меньше высоты экрана).
 */
private const val MODE_BOTTOM_SHEET = 2

/**
 * Макет окна выбора ([SelectionWindowFragment]).
 */
@Suppress("KDocUnresolvedReference")
class SelectionWindowLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.coordinatorlayout.widget.CoordinatorLayout(context, attrs, defStyleAttr) {

    /**
     * Верхняя панель, содержащая шапки и кнопку "Закрыть".
     */
    private val mAppBarLayout: AppBarLayout by lazy {
        findViewById(R.id.base_components_app_bar)
    }
    /**
     * Верняя часть окна с кнопкой "Закрыть".
     */
    private val mHood: View? by lazy {
        findViewById(R.id.base_components_hood_container)
    }
    /**
     * Контейнер контентной области.
     */
    private val mContentContainer: FrameLayout by lazy {
        findViewById(R.id.base_components_content_container)
    }

    /**
     * Режим, в котором сейчас работает layout.
     */
    private var mMode = MODE_UNKNOWN

    /**
     * Включить/отключить возможность скроллирования шапки.
     */
    var isHoodScrollingEnabled = false
        set(value) {
            if (value != field) {
                field = value
                mMode = MODE_UNKNOWN
                requestLayout()
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mMode != MODE_FULLSCREEN) {
            // Переводим coordinator layout в состояние по-умолчанию (скролл включен)
            enableHoodScrolling(true)
            invalidateTotalScrollRange(widthMeasureSpec, heightMeasureSpec)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)
        val measuredHeight = measuredHeight
        val appBarHeight = mAppBarLayout.measuredHeight
        val scrollRange = mAppBarLayout.totalScrollRange
        val minAppBarHeight = appBarHeight - scrollRange
        val maxContentHeight = availableHeight - minAppBarHeight

        if (measuredHeight == maxContentHeight) {
            // Контент занял максимально допустимую высоту.
            // Можем растянуть coordinator на весь экран (избавиться от отступа сверху)
            setMeasuredDimension(measuredWidth, availableHeight)
            mMode = MODE_FULLSCREEN
            return
        }

        val leftoverHeight = maxContentHeight - measuredHeight

        if (leftoverHeight in 0..(scrollRange - 1)) {
            // Контент занял не максимальную высоту, не хватило меньше чем scrollRange
            // Можем растянуть coordinator на весь экран, отключив прокручивание appbar
            enableHoodScrolling(false)
            if (scrollRange > 0) { // Прокручиваемая часть должна быть равна 0
                invalidateTotalScrollRange(widthMeasureSpec, heightMeasureSpec)
            }
            // Для того, чтобы часть контента не осталась ниже экрана,
            // Выполняем measure с указанием точной высоты (доступная высота минус высота appbar)
            val containerHeightSpec = MeasureSpec.makeMeasureSpec(availableHeight - appBarHeight, MeasureSpec.EXACTLY)
            mContentContainer.measure(widthMeasureSpec, containerHeightSpec)
            setMeasuredDimension(measuredWidth, availableHeight)
            mMode = MODE_SCROLL_CONTENT_ONLY
            return
        }

        if (mContentContainer.measuredHeight + minAppBarHeight <= availableHeight) {
            // Макет займет не весь экран - исправляем размер, чтобы было достаточно места для отрисовки контента полностью
            enableHoodScrolling(false) // Отключаем скролл
            if (scrollRange > 0) { // Прокручиваемая часть должна быть равна 0
                invalidateTotalScrollRange(widthMeasureSpec, heightMeasureSpec)
            }
            setMeasuredDimension(measuredWidth, mContentContainer.measuredHeight + appBarHeight)
            mMode = MODE_BOTTOM_SHEET
            return
        }
    }

    /**
     * Включаем/выключаем проскроллирование шапки.
     */
    private fun enableHoodScrolling(enable: Boolean) {
        mHood?.let { hood ->
            // Обновляем флаги прокрутки
            val flags = if (enable && isHoodScrollingEnabled) AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL else 0
            (hood.layoutParams as AppBarLayout.LayoutParams).scrollFlags = flags
        }
    }

    /**
     * Сбрасываем значение проскролливаемой области.
     */
    private fun invalidateTotalScrollRange(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mAppBarLayout.forceLayout()
        mAppBarLayout.measure(widthMeasureSpec, heightMeasureSpec)
    }

}