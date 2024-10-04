package ru.tensor.sbis.design.profile.personcollage

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.person.PERSON_VIEW_DEFAULT_CORNER_RADIUS
import ru.tensor.sbis.design.profile.person.PersonView
import ru.tensor.sbis.design.profile.person.data.DisplayMode
import ru.tensor.sbis.design.profile_decl.person.DepartmentData
import ru.tensor.sbis.design.profile_decl.person.PersonCollageClickListener
import ru.tensor.sbis.design.profile_decl.person.PhotoData
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.profile_decl.person.Shape
import ru.tensor.sbis.design.theme.global_variables.BorderRadius

private const val MAX_DEPARTMENT_PHOTO_COUNT = 3

/**
 * View компонента "Коллаж внутри блока".
 * Предназначен для отображения коллажа из нескольких фото (до 4-х) плиткой, либо одиночного фото с опциональным
 * статусом активности.
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=изображения_3&g=1)
 *
 * @author us.bessonov
 */
class PersonCollageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val photo = addPersonView(attrs)
    private var collage: CollageGridView? = null
    private var size = PhotoSize.UNSPECIFIED
    private var shape = Shape.CIRCLE
    private var cornerRadius = 0f
    private var personCollageClickListener: PersonCollageClickListener? = null
    private var departmentData: DepartmentData? = null

    private var isCollageAdded = false
    private var isCollageSizeApplied = false
    private var isCollageAddedInLayout = false

    init {
        getContext().withStyledAttributes(attrs, R.styleable.PersonCollageView) {
            val sizeOrdinal = getInt(
                R.styleable.PersonCollageView_PersonCollageView_size,
                PhotoSize.UNSPECIFIED.ordinal
            )
            val displayModeOrdinal =
                getInt(R.styleable.PersonCollageView_PersonCollageView_displayMode, DisplayMode.REGISTRY.ordinal)
            val shapeOrdinal =
                getInt(R.styleable.PersonCollageView_PersonCollageView_shape, Shape.SUPER_ELLIPSE.ordinal)
            val withActivityStatus =
                getBoolean(R.styleable.PersonCollageView_PersonCollageView_withActivityStatus, false)
            val customPlaceholderId =
                getResourceId(R.styleable.PersonCollageView_PersonCollageView_customPlaceholder, 0)
            val cornerRadius = getDimension(R.styleable.PersonCollageView_PersonCollageView_cornerRadius, 0f)
                .takeIf { it > 0 }

            setSize(PhotoSize.values()[sizeOrdinal])
            setShape(Shape.values()[shapeOrdinal])
            cornerRadius?.let(::setCornerRadius)
            setCustomPlaceholder(customPlaceholderId)
            photo.setDisplayMode(DisplayMode.values()[displayModeOrdinal])
            photo.setHasActivityStatus(withActivityStatus)
        }
    }

    /**
     * Задаёт список отображаемых фото.
     * Для коллажа подразделения установите список из единственного [DepartmentData]. Первый элемент в
     * [DepartmentData.persons] считается руководителем подразделения.
     * Если [dataList] содержит элементы помимо [DepartmentData], то список изображений будет формироваться из всех
     * одиночных элементов и сотрудников подразделений.
     * Порядок отображения фото соответствует порядку в [dataList], за исключением фото без изображения - они
     * отбрасываются в конец.
     */
    fun setDataList(dataList: List<PhotoData>) {
        getSingleDepartment(dataList)?.let { department ->
            departmentData = department
            getDepartmentPhotoList(department)
                ?.let { setPhotoList(it, setAsCollage = true) }
                ?: run {
                    department.isStub = true
                    setPhotoList(dataList)
                }
            return
        }
        departmentData = null
        val photos = mutableListOf<PhotoData>().apply {
            dataList.forEach {
                if (it is DepartmentData) addAll(it.persons) else add(it)
            }
        }
        setPhotoList(photos)
    }

    /**
     * @see [PersonView.setHasActivityStatus]
     */
    fun setHasActivityStatus(hasActivityStatus: Boolean, displayOfflineHomeStatus: Boolean = false) {
        photo.setHasActivityStatus(hasActivityStatus, displayOfflineHomeStatus)
    }

    /**
     * Программная установка размера фото.
     * Размер рекомендуется задавать через атрибут [R.attr.PersonCollageView_size]. При установке из кода, данный метод
     * должен быть вызван до установки данных.
     * По умолчанию используется [PhotoSize.UNSPECIFIED] - размеры из [View.getLayoutParams]. В этом случае необходимо
     * явно указать желаемое значение в `layout_width` и `layout_height` (оба параметра обязательны).
     */
    fun setSize(size: PhotoSize) {
        this.size = size
        photo.setSize(size)
        collage?.setSize(size)
    }

    /**
     * Программная установка кастомной заглушки в случае пустого списка отображаемых фото.
     * Кастомную заглушку рекомендуется задавать через атрибут [R.attr.PersonCollageView_customPlaceholder].
     */
    fun setCustomPlaceholder(@DrawableRes drawableId: Int) {
        if (drawableId != 0) getCollage().setCustomPlaceholder(drawableId)
    }

    /**
     * Радиус скругления углов. Применяется только если указана квадратная форма ([Shape.SQUARE]).
     *
     * @see [R.attr.PersonCollageView_shape]
     */
    fun setCornerRadius(radius: BorderRadius? = PERSON_VIEW_DEFAULT_CORNER_RADIUS) =
        setCornerRadius(radius?.getDimen(context) ?: 0f)

    /** @SelfDocumented */
    fun setClickListener(listener: PersonCollageClickListener) {
        personCollageClickListener = listener
        collage?.let { setCollageClickListener(it, listener) }
        photo.setClickListener(listener::onPersonClick)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        collage?.setOnClickListener(l)
        photo.setOnClickListener(l)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        super.setLayoutParams(params)
        params
            ?.takeIf { it.width > 0 }
            ?.let {
                photo.layoutParams = LayoutParams(it)
                collage?.layoutParams = LayoutParams(it)
            }
    }

    override fun setClickable(clickable: Boolean) {
        super.setClickable(clickable)
        photo.isClickable = clickable
    }

    /**
     * Подготовить ленивый коллаж к отображению.
     */
    fun prepareCollage() {
        collage = createAndAddCollage()
    }

    private fun setPhotoList(list: List<PhotoData>, setAsCollage: Boolean = list.size != 1) {
        if (setAsCollage) {
            photo.isVisible = false
            getCollage().isVisible = true
            getCollage().setDataList(list)
        } else {
            photo.isVisible = true
            collage?.isVisible = false
            photo.setData(list.single())
        }
    }

    private fun getSingleDepartment(dataList: List<PhotoData>): DepartmentData? =
        dataList.singleOrNull() as? DepartmentData

    private fun getDepartmentPhotoList(departmentData: DepartmentData): List<PhotoData>? {
        return departmentData
            .takeUnless { data -> data.persons.all { it.photoUrl == null } }
            ?.persons
            ?.sortedBy { if (it.photoUrl.isNullOrBlank()) 1 else 0 }
            ?.take(MAX_DEPARTMENT_PHOTO_COUNT)
    }

    private fun getCollage() = collage ?: createAndAddCollage().also { collage = it }

    private fun createAndAddCollage() = CollageGridView(context).also { collage ->
        collage.id = R.id.design_profile_collage_grid_view
        isCollageAddedInLayout = isInLayout
        addView(collage)
        isCollageAdded = true
        if (size == PhotoSize.UNSPECIFIED) {
            layoutParams?.let {
                collage.layoutParams = LayoutParams(it)
            }
        } else {
            collage.setSize(size)
        }
        isCollageSizeApplied = true
        collage.setShape(shape)
        collage.setCornerRadius(cornerRadius)
        personCollageClickListener?.let { setCollageClickListener(collage, it) }
    }

    private fun setCollageClickListener(collage: CollageGridView, listener: PersonCollageClickListener) {
        collage.setOnClickListener {
            departmentData
                ?.let { listener.onPersonClick(it.uuid) }
                ?: listener.onCollageClick()
        }
    }

    private fun addPersonView(attrs: AttributeSet?) = PersonView(context, attrs)
        .also {
            it.id = R.id.design_profile_collage_person_view
            addView(it, MATCH_PARENT, MATCH_PARENT)
        }

    private fun setShape(shape: Shape) {
        this.shape = shape
        photo.setShape(shape)
    }

    private fun setCornerRadius(@Px radius: Float) {
        cornerRadius = radius
        photo.setCornerRadius(radius)
        collage?.setCornerRadius(radius)
    }
}