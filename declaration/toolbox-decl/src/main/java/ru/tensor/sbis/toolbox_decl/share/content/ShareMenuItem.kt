package ru.tensor.sbis.toolbox_decl.share.content

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Элемент пункта меню, который будет отображаться в навигационной панели меню "поделиться".
 *
 * @property id идентификатор элемента. Если раздел завязан на NavigationService -
 * рекомендуется использовать идентификаторы из NavxId, например, NavxId.CONTACTS.id.
 * @property icon иконка элемента.
 * @property title ресурс заголовка элемента.
 * @property order порядок элемента в общем списке.
 * @property canBeSelected признак может ли элемент быть выбранным.
 * true, если у элемента есть фрагмент в качестве контента меню,
 * в ином случае false, чтобы элемент не превыбрался при открытии меню.
 * Использовать значения кратные 100, чтобы сохранять гибкость настройки сортировки пунктов.
 *
 * @author vv.chekurda
 */
@Parcelize
data class ShareMenuItem(
    val id: String,
    val icon: SbisMobileIcon.Icon,
    @StringRes val title: Int,
    val order: Int,
    val canBeSelected: Boolean = true
): Parcelable