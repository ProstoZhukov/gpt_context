package ru.tensor.sbis.fresco_view.shapeddrawer

import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.widget.ImageView
import ru.tensor.sbis.design.utils.getViewIdName

/**
 * Реализация [ShapedImageView] для [ImageView]
 *
 * @author us.bessonov
 */
internal class DefaultShapedImageView(private val imageView: ImageView) : ShapedImageView {

    override fun getName() = getViewIdName(imageView)

    override fun getDrawable(): Drawable? = imageView.drawable

    override fun getWidth() = imageView.width

    override fun getHeight() = imageView.height

    override fun getPaddingLeft() = imageView.paddingLeft

    override fun getPaddingRight() = imageView.paddingRight

    override fun getPaddingTop() = imageView.paddingTop

    override fun getPaddingBottom() = imageView.paddingBottom

    override fun getImageMatrix(): Matrix? = imageView.imageMatrix

}