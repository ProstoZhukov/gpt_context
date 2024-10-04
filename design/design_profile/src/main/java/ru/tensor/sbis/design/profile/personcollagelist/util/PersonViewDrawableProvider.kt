package ru.tensor.sbis.design.profile.personcollagelist.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.fresco_view.R as RFrescoView

/**
 * Поставщик изображений заглушек фото сотрудника.
 *
 * @author us.bessonov
 */
internal interface PersonViewPlaceholderProvider {

    /** @SelfDocumented */
    fun getDefaultPlaceholder(context: Context): Drawable

    /** @SelfDocumented */
    fun getCompanyPlaceholder(context: Context): Drawable

    /** @SelfDocumented */
    fun getDepartmentPlaceholder(context: Context): Drawable

    /** @SelfDocumented */
    fun getGroupPlaceholder(context: Context): Drawable
}

/**
 * Возвращает [Drawable], используемые компонентами фото сотрудника, по возможности переиспользуя [Bitmap]'ы.
 *
 * @author us.bessonov
 */
internal object PersonViewDrawableProvider : PersonViewPlaceholderProvider {

    private var defaultPlaceholder: Bitmap? = null
    private var companyPlaceholder: Bitmap? = null
    private var departmentPlaceholder: Bitmap? = null
    private var superEllipseShape: Bitmap? = null
    private var circleShape: Bitmap? = null
    private var squareShape: Bitmap? = null

    /** @SelfDocumented */
    fun getSuperEllipseShape(context: Context) = superEllipseShape?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, RFrescoView.drawable.fresco_view_super_ellipse_vector_mask)!!
            .also { superEllipseShape = it.toBitmap() }

    /** @SelfDocumented */
    fun getCircleShape(context: Context) = circleShape?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, R.drawable.design_profile_circle_white)!!
            .also { circleShape = it.toBitmap() }

    /** @SelfDocumented */
    fun getSquareShape(context: Context) = squareShape?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, R.drawable.design_profile_square_white)!!
            .also { squareShape = it.toBitmap() }

    /** @SelfDocumented */
    fun getSquareShape(@Px cornerRadius: Float) = GradientDrawable().apply {
        color = ColorStateList.valueOf(Color.WHITE)
        setCornerRadius(cornerRadius)
    }

    override fun getDefaultPlaceholder(context: Context) = defaultPlaceholder?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, R.drawable.design_profile_person_placeholder)!!
            .also { defaultPlaceholder = it.toBitmap() }

    override fun getCompanyPlaceholder(context: Context) = companyPlaceholder?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, R.drawable.design_profile_company_placeholder_opaque)!!
            .also { companyPlaceholder = it.toBitmap() }

    override fun getDepartmentPlaceholder(context: Context) = departmentPlaceholder?.toDrawable(context.resources)
        ?: ContextCompat.getDrawable(context, R.drawable.design_profile_three_persons_placeholder)!!
            .also { departmentPlaceholder = it.toBitmap() }

    override fun getGroupPlaceholder(context: Context) = getDepartmentPlaceholder(context)

}