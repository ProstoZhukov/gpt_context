package ru.tensor.sbis.link_share.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.link_share.ui.model.SbisLinkShareMode.ON_MOVABLE_PANEL

/**
 * Модель с настройками компонента
 * @param links
 * Набор ссылок, которыми нужно поделиться.
 * Если ссылок несколько будет показан переключатель
 * @param title
 * Заголовок, отображаемый в шапке компонента (по умолчанию - "Ссылка/Поделиться")
 * @param mode
 * Тип отображения экрана компонента
 * @param customMenuItem
 * Модель пользовательского пункта меню (по умолчанию нет)
 */
@Parcelize
data class SbisLinkShareParams(
    val links: List<SbisLinkShareLink>,
    val title: String = "",
    val mode: SbisLinkShareMode = ON_MOVABLE_PANEL,
    val customMenuItem: List<SbisLinkShareCustomMenuItem> = listOf()
) : Parcelable