package ru.tensor.sbis.design.profile.person

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat.TRANSLUCENT
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.person.ActivityStatusBitmapProvider.getActivityStatusBitmaps
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus

/**
 * Рисует статус активности [ActivityStatus].
 *
 * [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=индикатор_статуса_активности_пользователя&g=1)
 *
 * @author vv.chekurda
 */
internal class ActivityStatusDrawable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    val styleHolder: ActivityStatusStyleHolder
) : Drawable() {

    /**
     * Размер статуса.
     */
    @Px
    var size: Int = styleHolder.statusSizeMedium

    private val bitmaps = getActivityStatusBitmaps(context, attrs, size, styleHolder)
    internal var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    internal var statusImage: Bitmap? = null

    init {
        // Для превью в android studio
        setBounds(0, 0, size, size)

        setActivityStatus(ActivityStatus.UNKNOWN, false)
    }

    /** @SelfDocumented */
    fun setActivityStatus(status: ActivityStatus, displayOfflineHome: Boolean) {
        statusImage = when (status) {
            ActivityStatus.ONLINE_WORK -> bitmaps.onlineWorkBitmap
            ActivityStatus.OFFLINE_WORK -> bitmaps.offlineWorkBitmap
            ActivityStatus.ONLINE_HOME -> bitmaps.onlineHomeBitmap
            ActivityStatus.OFFLINE_HOME -> bitmaps.offlineHomeBitmap.takeIf { displayOfflineHome }
            else -> null
        }
    }

    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        statusImage?.let {
            canvas.drawBitmap(it, null, bounds, paint)
        }
    }

    override fun getIntrinsicWidth(): Int = size

    override fun getIntrinsicHeight(): Int = getIntrinsicWidth()

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = TRANSLUCENT
}