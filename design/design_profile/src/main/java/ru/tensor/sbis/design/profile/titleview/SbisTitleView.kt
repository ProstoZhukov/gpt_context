package ru.tensor.sbis.design.profile.titleview

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import ru.tensor.sbis.design.profile.R
import ru.tensor.sbis.design.profile.databinding.DesignProfileSbisTitleViewBinding
import ru.tensor.sbis.design.profile.person.controller.PersonViewApi
import ru.tensor.sbis.design.profile.personcollage.PersonCollageView
import ru.tensor.sbis.design.profile.titleview.utils.*
import ru.tensor.sbis.design.profile_decl.titleview.Default
import ru.tensor.sbis.design.profile_decl.titleview.ListContent
import ru.tensor.sbis.design.profile_decl.titleview.SubtitleSize
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewContent
import ru.tensor.sbis.design.profile_decl.titleview.TitleViewContent.Companion.EMPTY
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.fresco_view.SuperEllipseDraweeView

/**
 * `SbisTitleView` предназначен для отображения заголовка, подзаголовка и фотографий в тулбаре.
 *
 * Для изменения цвета текстовых полей необходимо использовать атрибуты:
 * - [R.styleable.DesignSbisTitleView_DesignSbisTitleView_titleColor] - устанавливает цвет заголовка
 * - [R.styleable.DesignSbisTitleView_DesignSbisTitleView_subtitleColor] - устанавливает цвет подзаголовка
 *
 * Ссылки на стандарты:
 * - [Стандарт для шапки](http://axure.tensor.ru/MobileStandart8/#p=шапка_v3&g=1)
 * - [Стандарт для изображений](http://axure.tensor.ru/MobileStandart8/изображения_3.html)
 *
 * @see PersonCollageView
 *
 * @author ns.staricyn, ma.kolpakov
 */
@Suppress("KDocUnresolvedReference", "unused")
class SbisTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.designSbisTitleViewTheme,
    defStyleRes: Int = R.style.DesignProfileSbisTitleViewThemeWhiteText
) : FrameLayout(
    ThemeContextBuilder(context, attrs, defStyleAttr, defStyleRes).build(),
    attrs,
    defStyleAttr,
    defStyleRes
) {

    private val binding = DesignProfileSbisTitleViewBinding.inflate(LayoutInflater.from(getContext()), this, true)
    private var personCollageView: PersonCollageView? = PersonCollageView(this.context, attrs, defStyleAttr)
    private var superEllipseView: SuperEllipseDraweeView? = SuperEllipseDraweeView(this.context, attrs, defStyleAttr)
    private val titleView = binding.designProfileTitleViewTitle
    private val subtitleView = binding.designProfileTitleViewSubtitle

    private var _content: TitleViewContent = EMPTY

    /**
     * Флаг, показывающий был ли добавлен нужный ImageView к заголовку.
     */
    private var isCreatedImageView = false

    /**
     * Инструмент, требуемый при использовании компонента в графической шапке.
     */
    val appBarTitleViewHelper by lazy {
        SbisAppBarTitleViewHelper(binding)
    }

    /**
     * Контент шапки.
     *
     * Возможные варианты использования:
     * - [ListContentOld] - вью для изображения есть всегда. Если нет картинки, будет отображаться заглушка.
     * - [DefaultOld] - вью для изображения будет скрыта, если ссылка [DefaultOld.imageUrl] будет пустой.
     *
     * @see TitleViewContent
     */
    var content: TitleViewContent
        get() = _content
        set(value) {
            _content = value
            updateContent()
        }

    /**
     * @see [PersonViewApi.setHasActivityStatus]
     */
    var hasActivityStatus: Boolean = false
        set(value) {
            field = value
            updateActivityStatus(hasActivityStatus)
        }

    /**
     * Принудительное отображение заголовка в одну строку с многоточием при недостатке пространства.
     */
    var singleLineTitle = false
        set(value) {
            field = value
            updateTitleView()
        }

    /**
     * Размер подзаголовка.
     *
     * @see SubtitleSize
     */
    var subtitleSize = SubtitleSize.SMALL
        set(value) {
            val newValue = if (
                resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                imageIsVisible()
            ) {
                SubtitleSize.SMALL
            } else {
                value
            }
            if (field == newValue) return
            field = value
            updateSubtitleTextSize()
        }

    init {
        getContext().withStyledAttributes(
            attrs,
            R.styleable.DesignSbisTitleView,
            defStyleAttr,
            defStyleRes
        ) {
            hasActivityStatus =
                getBoolean(R.styleable.DesignSbisTitleView_DesignSbisTitleView_withActivityStatus, false)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val subtitlePaddingEnd = appBarTitleViewHelper.getSubtitleEndPadding()
        if (subtitleView.paddingEnd != subtitlePaddingEnd) {
            subtitleView.updatePadding(right = subtitlePaddingEnd)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun addImageViewIfNeeded() {
        if (!isCreatedImageView && _content != EMPTY) {
            when (_content) {
                is Default -> {
                    personCollageView = null
                    superEllipseView?.let { addImageViewToRoot(it) }
                }

                is ListContent -> {
                    superEllipseView = null
                    personCollageView?.let { addImageViewToRoot(it) }
                }
            }

            isCreatedImageView = true
        }
    }

    private fun addImageViewToRoot(imageView: View) {
        imageView.visibility = GONE
        imageView.id = generateViewId()

        val rootLayout: ConstraintLayout = binding.root
        val set = ConstraintSet()

        val imageViewParams = ConstraintLayout.LayoutParams(
            resources.getDimension(R.dimen.design_profile_sbis_title_view_collage_size).toInt(),
            resources.getDimension(R.dimen.design_profile_sbis_title_view_collage_size).toInt()
        )
        imageViewParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        imageViewParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        imageViewParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        imageView.layoutParams = imageViewParams
        rootLayout.addView(imageView)

        val barrier = Barrier(context)
        barrier.id = generateViewId()
        barrier.type = Barrier.END

        val referenceIds: IntArray = intArrayOf(imageView.id)
        barrier.referencedIds = referenceIds
        rootLayout.addView(barrier)

        set.clone(rootLayout)
        set.connect(binding.designProfileTitleViewTitle.id, ConstraintSet.START, barrier.id, ConstraintSet.END)
        set.connect(binding.designProfileTitleViewSubtitle.id, ConstraintSet.START, barrier.id, ConstraintSet.END)
        set.applyTo(rootLayout)
    }

    private fun updateContent() {
        updateImages()
        updateTitles()
    }

    private fun updateImages() {
        updateImagesViewData()
        updateImagesVisible()
        updateTitleAndSubtitleMargin()
    }

    private fun updateImagesViewData() {
        addImageViewIfNeeded()
        when (val content = _content) {
            is Default -> setSuperEllipseViewContent(content)
            is ListContent -> personCollageView?.setDataList(content.list.map { it.photoData })
        }
    }

    private fun updateImagesVisible() {
        personCollageView?.isVisible = _content is ListContent
        superEllipseView?.isVisible = _content is Default && !isNeedHideImage(_content)
    }

    private fun updateTitles() {
        (_content as? ListContent)
            ?.takeIf { it.forceListTitles }
            ?.let(::setTitleList)
            ?: titleView.setSimpleTitle(_content.title)

        titleView.isVisible = titleView.text.isNotEmpty()

        subtitleView.text = _content.subtitle

        subtitleView.isVisible = _content.subtitle.isNotEmpty()

        updateTitleView()
    }

    private fun updateActivityStatus(hasActivityStatus: Boolean) {
        personCollageView?.setHasActivityStatus(hasActivityStatus)
    }

    private fun updateTitleView() {
        val maxLines = chooseTitleMaxLines(singleLineTitle, _content)

        val textSize = resources.getDimension(chooseTitleTextSize(_content))
        val smallerTextSize = resources.getDimension(chooseSmallerTitleTextSize(_content))
        titleView.apply {
            setDefaultTextSize(textSize)
            setSmallerNamesTextSize(smallerTextSize)
            this.maxLines = maxLines
        }
    }

    private fun updateSubtitleTextSize() {
        subtitleView.setTextSize(COMPLEX_UNIT_PX, resources.getDimension(subtitleSize.sizeRes))
    }

    private fun updateTitleAndSubtitleMargin() {
        val marginLeft = if (imageIsVisible()) {
            resources.getDimensionPixelSize(R.dimen.design_profile_sbis_title_view_image_padding_end)
        } else {
            0
        }
        titleView.updateLeftMarginIfNeeded(marginLeft)
        subtitleView.updateLeftMarginIfNeeded(marginLeft)
    }

    private fun imageIsVisible() = personCollageView?.isVisible == true || superEllipseView?.isVisible == true

    private fun setSuperEllipseViewContent(content: Default) = with(superEllipseView) {
        this?.setImageURI(content.imageUrl)
        content.imagePlaceholderRes
            ?.let { this?.hierarchy?.setPlaceholderImage(it) }
            ?: this?.hierarchy?.setPlaceholderImage(null)
    }

    private fun View.updateLeftMarginIfNeeded(@Px marginLeft: Int) {
        (layoutParams as MarginLayoutParams).apply {
            if (leftMargin != marginLeft) {
                leftMargin = marginLeft
                requestLayout()
            }
        }
    }

    private fun setTitleList(content: ListContent) {
        titleView.setParticipantsNames(
            content.list.map { it.title }.filter { it.isNotEmpty() },
            content.hiddenTitleCount
        )
    }
}