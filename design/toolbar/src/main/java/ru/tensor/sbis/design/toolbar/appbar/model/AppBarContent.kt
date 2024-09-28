package ru.tensor.sbis.design.toolbar.appbar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.profile.titleview.SbisTitleView
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Содержимое графической шапки.
 *
 * @property title заголовок, размеры которого зависят от степени разворота шапки
 * @property subTitle подзаголовок, отображаемый в развёрнутом состоянии над заголовком
 * @property collapsedSubtitle заголовок, отображаемый в свёрнутом состоянии (только при использовании [SbisTitleView])
 * @property comment комментарий, отображаемый в развёрнутом состоянии справа от заголовка
 * @property photoData данные изображения, отображаемого в свёрнутом состоянии (только при использовании
 * [SbisTitleView])
 *
 * @author us.bessonov
 */
@Parcelize
data class AppBarContent(
    val title: String = "",
    val subTitle: String = "",
    val collapsedSubtitle: String = subTitle,
    val comment: String = "",
    val photoData: PhotoData? = null
) : Parcelable