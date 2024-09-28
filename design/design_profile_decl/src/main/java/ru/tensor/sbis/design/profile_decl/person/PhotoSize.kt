package ru.tensor.sbis.design.profile_decl.person

import android.content.Context
import androidx.annotation.DimenRes
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.profile_decl.R
import ru.tensor.sbis.design.profile_decl.util.calculateInitialsTextSize
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.ImageSize

/**
 * Доступные размеры фото сотрудника
 *
 * @author us.bessonov
 */
enum class PhotoSize(
    @Deprecated("Используйте photoImageSize") @DimenRes val photoSize: Int,
    val photoImageSize: ImageSize?,
    val collageCounterTextSize: FontSize?
) {
    X2S(
        ResourcesCompat.ID_NULL,
        ImageSize.X2S,
        FontSize.X3S
    ),
    XS(
        R.dimen.design_profile_decl_person_view_size_xs,
        ImageSize.XS,
        FontSize.X3S
    ),
    S(
        R.dimen.design_profile_decl_person_view_size_s,
        ImageSize.S,
        FontSize.XS
    ),
    M(
        R.dimen.design_profile_decl_person_view_size_m,
        ImageSize.M,
        FontSize.M
    ),
    L(
        R.dimen.design_profile_decl_person_view_size_l,
        ImageSize.L,
        FontSize.XL
    ),
    XL(
        R.dimen.design_profile_decl_person_view_size_xl,
        ImageSize.XL,
        FontSize.X4L
    ),
    X2L(
        R.dimen.design_profile_decl_person_view_size_2xl,
        ImageSize.X2L,
        FontSize.X5L
    ),
    UNSPECIFIED(ResourcesCompat.ID_NULL, null, null);

    fun getInitialsTextSize(context: Context): Float? = photoImageSize?.let {
        calculateInitialsTextSize(it.getDimen(context))
    }
}