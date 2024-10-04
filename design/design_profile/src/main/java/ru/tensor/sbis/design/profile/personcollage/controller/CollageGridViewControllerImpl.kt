package ru.tensor.sbis.design.profile.personcollage.controller

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.imageview.PersonImageView
import ru.tensor.sbis.design.profile.person.hasPreviewSizeTemplate
import ru.tensor.sbis.design.profile.personcollage.CollageGridView
import ru.tensor.sbis.design.profile.personcollage.PersonCollageWithSeparatorBuilder
import ru.tensor.sbis.design.profile.util.getPreviewerPhotoUri
import ru.tensor.sbis.design.profile_decl.person.ImageData
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.ImageSize
import ru.tensor.sbis.design.utils.image_loading.ImageUrl
import ru.tensor.sbis.design.utils.image_loading.RawBitmap
import ru.tensor.sbis.design.utils.image_loading.ViewImageLoader

private var ID = 0

/**
 * Реализует логику компонента [CollageGridView].
 *
 * @author us.bessonov
 */
internal class CollageGridViewControllerImpl(
    @ColorInt
    private val backgroundColor: Int,
    private val collageBuilder: PersonCollageWithSeparatorBuilder = PersonCollageWithSeparatorBuilder(),
    provideImageLoader: (Int) -> ViewImageLoader = { id -> ViewImageLoader(collageBuilder, id) },
    private val providePersonImageView: (Context) -> PersonImageView = { context ->
        PersonImageView(context).apply {
            setShapeBackgroundColor(backgroundColor)
        }
    },
    private val getPlaceholderBitmap: (Resources, Int) -> Bitmap? = { resources, resId ->
        BitmapFactory.decodeResource(resources, resId)
    }
) : CollageGridViewController {

    private val id = ID++

    private val imageLoader: ViewImageLoader = provideImageLoader(id)

    private lateinit var view: CollageGridView

    private var photoSize = PhotoSize.UNSPECIFIED

    private var dataList = emptyList<PhotoData>()

    @DrawableRes
    private var customPlaceholder: Int? = null

    private val personImageView by lazy {
        providePersonImageView(view.context)
    }

    @Px
    private var separatorSize = 0
        set(value) {
            collageBuilder.separatorWidth = value
            field = value
        }

    override fun init(
        root: CollageGridView
    ) {
        view = root
        separatorSize = root.resources.getDimensionPixelSize(R.dimen.design_profile_person_collage_view_separator_size)
        imageLoader.init(
            view,
            personImageView,
            {},
            { null },
            ::getImageWidthAndHeight
        )
    }

    override fun setDataList(dataList: List<PhotoData>) {
        this.dataList = dataList
        val dataWithPhotoCount = getDataWithPhotoCount()
        if (dataWithPhotoCount == 0) {
            showPlaceholder(dataList)
        } else {
            personImageView.configurePlaceholderBitmap(getPlaceholderBitmap(dataList.take(1)))
            showPhotos(dataList, dataWithPhotoCount)
        }
    }

    override fun setSize(size: PhotoSize) {
        val isBiggerPreviewRequired = isBiggerPreviewRequired(photoSize, size)
        photoSize = size
        val photoSize = getSizeInPx(size) ?: ViewGroup.LayoutParams.MATCH_PARENT
        view.layoutParams.apply {
            if (width != photoSize) {
                width = photoSize
                height = photoSize
                view.requestLayout()
            }
        }
        personImageView.setPhotoSize(photoSize)
        if (isBiggerPreviewRequired) setDataList(dataList)
    }

    override fun setCustomPlaceholder(drawableId: Int) {
        customPlaceholder = drawableId

        if (getDataWithPhotoCount() == 0) {
            showPlaceholder(dataList)
        }
    }

    override fun setShape(shape: Shape) {
        personImageView.setShape(shape)
    }

    override fun setCornerRadius(radius: Float) {
        personImageView.setCornerRadius(radius)
    }

    override fun setBitmap(bitmap: Bitmap?) = personImageView.setBitmap(bitmap)

    override fun onMeasured(measuredWidth: Int, measuredHeight: Int) {
        imageLoader.onViewMeasured()
    }

    override fun performLayout() {
        personImageView.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    override fun performDraw(canvas: Canvas) {
        personImageView.draw(canvas)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        imageLoader.onVisibilityAggregated(isVisible)
    }

    override fun performInvalidate() {
        personImageView.invalidate()
    }

    private fun getImageWidthAndHeight() = view.measuredWidth to view.measuredHeight

    private fun getDataWithPhotoCount() = dataList.count { !it.photoUrl.isNullOrEmpty() }

    private fun isBiggerPreviewRequired(oldSize: PhotoSize, newSize: PhotoSize) =
        newSize.ordinal > oldSize.ordinal && dataList.any { it.photoUrl?.let(::hasPreviewSizeTemplate) == true }

    private fun showPhotos(dataList: List<PhotoData>, dataWithPhotoCount: Int) {
        getDisplayedData(dataList, dataWithPhotoCount).map {
            if (it.photoUrl != null) {
                ImageUrl(
                    getPreviewerPhotoUri(
                        it.photoUrl.orEmpty(),
                        getSizeInPx(photoSize) ?: view.layoutParams.width
                    )
                )
            } else {
                val placeholderImage = (it as? ImageData)?.placeholder ?: R.drawable.design_profile_person_placeholder
                RawBitmap(getPlaceholderBitmap(view.resources, placeholderImage)!!)
            }
        }.also(imageLoader::setImages)
        view.requestLayout()
    }

    private fun showPlaceholder(dataList: List<PhotoData>) {
        setBitmap(getPlaceholderBitmap(dataList))
        view.invalidate()
    }

    private fun getPlaceholderBitmap(dataList: List<PhotoData>): Bitmap? {
        val placeholder =
            dataList.filterIsInstance<ImageData>().firstOrNull()?.placeholder
                ?: customPlaceholder
                ?: getPlaceholder(dataList.size)
        return getPlaceholderBitmap(view.resources, placeholder)
    }

    private fun getSizeInPx(size: PhotoSize) =
        size.photoImageSize.takeUnless { it == null }?.let(::getDimensionPixelSizeNullable)

    @Px
    private fun getDimensionPixelSizeNullable(size: ImageSize) = try {
        size.getDimenPx(view.context)
    } catch (e: Resources.NotFoundException) {
        null
    }

    /**
     * Возвращает заглушку для случая, когда в списке у всех элементов отсутствует фото.
     */
    @DrawableRes
    private fun getPlaceholder(missingCount: Int) = when {
        missingCount < 2 -> R.drawable.design_profile_person_placeholder
        missingCount == 2 -> R.drawable.design_profile_two_persons_placeholder
        else -> R.drawable.design_profile_three_persons_placeholder
    }

    private fun getDisplayedData(dataList: List<PhotoData>, dataWithPhotoCount: Int): List<PhotoData> {
        // Сначала выбираются элементы с фото, а в конце может присутствовать не более одного элемента без фото
        return dataList.sortedBy { if (it.photoUrl.isNullOrBlank()) 1 else 0 }
            .take(dataWithPhotoCount + (dataList.size - dataWithPhotoCount).coerceAtMost(1))
    }
}