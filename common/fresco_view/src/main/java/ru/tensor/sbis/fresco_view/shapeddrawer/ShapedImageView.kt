package ru.tensor.sbis.fresco_view.shapeddrawer

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.annotation.Px

/**
 * Интерфейс View, для которого может применяться обрезка по произвольной форме
 *
 * @see [AbstractShapedImageDrawer]
 *
 * @author us.bessonov
 */
interface ShapedImageView {

    /** @SelfDocumented */
    fun getName(): String

    /** @SelfDocumented */
    fun getDrawable() : Drawable?

    /** @SelfDocumented */
    @Px
    fun getWidth(): Int

    /** @SelfDocumented */
    @Px
    fun getHeight(): Int

    /** @SelfDocumented */
    @Px
    fun getPaddingLeft(): Int

    /** @SelfDocumented */
    @Px
    fun getPaddingRight(): Int

    /** @SelfDocumented */
    @Px
    fun getPaddingTop(): Int

    /** @SelfDocumented */
    @Px
    fun getPaddingBottom(): Int

    /** @SelfDocumented */
    fun getImageMatrix(): Matrix?

}
