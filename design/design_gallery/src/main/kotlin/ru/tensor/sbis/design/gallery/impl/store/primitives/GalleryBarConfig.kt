package ru.tensor.sbis.design.gallery.impl.store.primitives

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.gallery.decl.GalleryMode

/**
 * Конфиг верхней панели при режиме [GalleryMode.ByAlbums]
 *
 * @property title          Заголовок тулбара
 * @property hasBackArrow   Присутствует ли стрелка "Назад"
 */
@Parcelize
internal class GalleryBarConfig(
    val title: String,
    val hasBackArrow: Boolean,
) : Parcelable