package ru.tensor.sbis.design.topNavigation.api

import android.content.Context
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.theme.res.SbisDrawable
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Представление изображения в шапке.
 *
 * @author da.zolotarev
 */
sealed interface SbisTopNavigationImage {

    /** Изображение, заданное через [SbisDrawable].*/
    class Drawable(private val drawable: SbisDrawable) : SbisTopNavigationImage {
        /** @SelfDocumented */
        internal fun getDrawable(context: Context) = drawable.getOrNull(context)
    }

    /** Изображение, заданное через [PlatformSbisString.Icon].*/
    class Icon(val icon: PlatformSbisString.Icon) : SbisTopNavigationImage {
        /** @SelfDocumented */
        internal fun getIcon(context: Context) = icon.getString(context)
    }

    /** Изображение, заданное через [PhotoData], используется [PersonView].*/
    @Deprecated("Используйте Photos")
    class Photo(val photoData: PhotoData) : SbisTopNavigationImage

    /** Изображения, заданные через [PhotoData], используется [PersonCollageView].*/
    class Photos(val photos: List<PhotoData>) : SbisTopNavigationImage
}