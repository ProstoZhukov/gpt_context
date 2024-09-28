/**
 * Переиспользуемые инструменты для привязки данных ко View.
 *
 * @author us.bessonov
 */
@file:Suppress("unused")

package ru.tensor.sbis.design.profile.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import com.facebook.common.references.CloseableReference
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import ru.tensor.sbis.design.profile.person.RecentPhotoUrlCache
import ru.tensor.sbis.design.profile.personcollagelist.util.InitialsDrawableFactory
import ru.tensor.sbis.design.profile.personcollagelist.util.PersonViewPlaceholderProvider
import ru.tensor.sbis.design.profile_decl.person.CompanyData
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.GroupData
import ru.tensor.sbis.design.profile_decl.person.ImageData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.fresco_view.util.RetainingDataSourceSupplier
import ru.tensor.sbis.fresco_view.util.draweeview.enableRetry

/** @SelfDocumented */
internal typealias RetainingImageDataSourceSupplier = RetainingDataSourceSupplier<CloseableReference<CloseableImage>>

/** @SelfDocumented */
internal fun setPhotoData(
    view: SimpleDraweeView,
    data: PhotoData,
    @Px photoSize: Int,
    retainingDataSourceSupplier: RetainingImageDataSourceSupplier? = null,
    resetDraweeController: Boolean = false
) {
    val uri = data.photoUrl?.takeUnless { it.isEmpty() }
        ?.let { Uri.parse(getPreviewerPhotoUri(it, photoSize)) }
        ?: run {
            view.setImageURI(null as Uri?, null)
            return
        }
    val imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).build()
    if (view.controller == null || retainingDataSourceSupplier == null || resetDraweeController) {
        setDraweeController(view, imageRequest, retainingDataSourceSupplier)
    } else {
        replaceSupplier(retainingDataSourceSupplier, imageRequest)
    }
}

/**
 * Определяет изображение отображаемой заглушки.
 */
internal fun getPlaceholder(
    data: PhotoData,
    personViewPlaceholderProvider: PersonViewPlaceholderProvider,
    initialsTextSize: Float?,
    @ColorInt
    initialsColor: Int,
    context: Context,
    initialsDrawableProvider: InitialsDrawableFactory,
    initialsEnabled: Boolean
): Drawable = with(personViewPlaceholderProvider) {
    when (data) {
        is ImageData -> {
            data.placeholder?.let {
                ContextCompat.getDrawable(context, it)!!
            } ?: ColorDrawable(Color.TRANSPARENT)
        }

        is CompanyData -> getCompanyPlaceholder(context)
        is DepartmentData -> getDepartmentPlaceholder(context)
        is GroupData -> getGroupPlaceholder(context)
        is PersonData -> data.initialsStubData?.let { it ->
            initialsDrawableProvider.createDrawable(context, initialsColor, initialsTextSize, it, initialsEnabled)
        } ?: getDefaultPlaceholder(context)
    }
}

/**
 * @see RecentPhotoUrlCache.getPhotoUrlForSize
 */
internal fun getPreviewerPhotoUri(url: String, @Px photoSize: Int): String {
    return RecentPhotoUrlCache.getPhotoUrlForSize(url, photoSize)
}

private fun setDraweeController(
    view: SimpleDraweeView,
    imageRequest: ImageRequest,
    retainingDataSourceSupplier: RetainingImageDataSourceSupplier?
) {
    view.controller = Fresco.newDraweeControllerBuilder()
        .setOldController(view.controller)
        .apply {
            retainingDataSourceSupplier?.let {
                replaceSupplier(it, imageRequest)
                setDataSourceSupplier(it)
            } ?: setImageRequest(imageRequest)
        }
        .build()
        .apply { enableRetry() }
}

private fun replaceSupplier(
    retainingDataSourceSupplier: RetainingImageDataSourceSupplier,
    imageRequest: ImageRequest
) {
    retainingDataSourceSupplier.replaceSupplier(
        Fresco.getImagePipeline()
            .getDataSourceSupplier(imageRequest, null, ImageRequest.RequestLevel.FULL_FETCH)
    )
}