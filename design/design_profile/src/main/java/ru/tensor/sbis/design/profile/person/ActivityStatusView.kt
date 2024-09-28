package ru.tensor.sbis.design.profile.person

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus

/**
 * View индикатора статуса активности пользователя.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=индикатор_статуса_активности_пользователя&g=1)
 *
 * @author us.bessonov
 */
class ActivityStatusView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.activityStatusViewTheme,
    @StyleRes defStyleRes: Int = R.style.DesignProfileActivityStatusStyleWhite,
    styleHolder: ActivityStatusStyleHolder = ActivityStatusStyleHolder.create(context)
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val statusDrawable = ActivityStatusDrawable(context, attrs, styleHolder)

    init {
        setWillNotDraw(false)
        if (isInEditMode) setActivityStatus(ActivityStatus.ONLINE_WORK)
    }

    /**
     * Задаёт отображаемый статус активности.
     *
     * @param displayOfflineHomeStatus Должен ли быть виден индикатор статуса [ActivityStatus.OFFLINE_HOME].
     */
    fun setActivityStatus(status: ActivityStatus, displayOfflineHomeStatus: Boolean = false) {
        statusDrawable.setActivityStatus(status, displayOfflineHomeStatus)
        invalidate()
    }

    override fun setEnabled(enabled: Boolean) {
        statusDrawable.setVisible(enabled, false)
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            statusDrawable.intrinsicWidth,
            statusDrawable.intrinsicHeight
        )
    }

    override fun onDraw(canvas: Canvas) {
        statusDrawable.draw(canvas)
    }

    override fun hasOverlappingRendering(): Boolean = false
}