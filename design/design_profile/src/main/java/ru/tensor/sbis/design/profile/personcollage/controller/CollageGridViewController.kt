package ru.tensor.sbis.design.profile.personcollage.controller

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.personcollage.CollageGridView
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape

/**
 * Контракт контроллера [CollageGridView].
 *
 * @author us.bessonov
 */
internal interface CollageGridViewController {

    /** @SelfDocumented */
    fun init(root: CollageGridView)

    /**
     * Задаёт список отображаемых фото.
     */
    fun setDataList(dataList: List<PhotoData>)

    /**
     * Установить размер фото.
     */
    fun setSize(size: PhotoSize)

    /** @SelfDocumented */
    fun setCustomPlaceholder(drawableId: Int)

    /** @SelfDocumented */
    fun setShape(shape: Shape)

    /** @SelfDocumented */
    fun setCornerRadius(@Px radius: Float)

    /** @SelfDocumented */
    fun setBitmap(bitmap: Bitmap?): Boolean

    /** @SelfDocumented */
    fun onMeasured(@Px measuredWidth: Int, @Px measuredHeight: Int)

    /** @SelfDocumented */
    fun performLayout()

    /** @SelfDocumented */
    fun performDraw(canvas: Canvas)

    /** @SelfDocumented */
    fun performInvalidate()

    /** @see View.onVisibilityAggregated */
    fun onVisibilityAggregated(isVisible: Boolean)
}