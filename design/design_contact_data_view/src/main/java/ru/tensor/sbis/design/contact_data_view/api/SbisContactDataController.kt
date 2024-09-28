package ru.tensor.sbis.design.contact_data_view.api

import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import ru.tensor.sbis.design.contact_data_view.ClickElementListener
import ru.tensor.sbis.design.contact_data_view.R
import ru.tensor.sbis.design.contact_data_view.SbisContactDataView
import ru.tensor.sbis.design.contact_data_view.SbisContactPhoneNumberView
import ru.tensor.sbis.design.contact_data_view.factory.ViewFactory
import ru.tensor.sbis.design.contact_data_view.model.SbisContactDataModel
import ru.tensor.sbis.design.contact_data_view.model.SbisContactPhoneNumberModel
import ru.tensor.sbis.design.contact_data_view.style.StyleHolder

class SbisContactDataController : SbisContactDataApi {

    private var currentModel: SbisContactDataModel? = null

    private lateinit var view: SbisContactDataView

    internal lateinit var contactDataViewFactory: ViewFactory

    private val styleHolder: StyleHolder = StyleHolder()

    /**
     * Текущее значение, сколько можно отображать элементов до пока кнопки "еще"
     */
    private var currentVisibleElements: Int = DEFAULT_NUMBER_OF_VISIBLE_ELEMENTS

    override var numberOpeningPerStep: Int = DEFAULT_NUMBER_OF_ITEMS_TO_OPEN_PER_STEP

    override var onClickElementListener: ClickElementListener? = null
        set(value) {
            field = value
            view.children
                .filterIsInstance<SbisContactPhoneNumberView>()
                .forEach {
                    it.clickListener = value
                }
        }

    internal fun attach(view: SbisContactDataView, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        view.context.theme.applyStyle(defStyleRes, false)
        this.view = view

        val styledAttributes = R.styleable.SbisContactDataView
        view.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {

            styleHolder.styleElement = getResourceId(
                R.styleable.SbisContactDataView_SbisContactDataView_phoneViewStyle,
                R.style.SbisContactPhoneNumberDefaultTheme
            )
            view.moreText.textPaint.apply {
                color = getColor(
                    R.styleable.SbisContactDataView_SbisContactDataView_moreTextColor,
                    Color.MAGENTA
                )
            }
            view.icon.textPaint.apply {
                color = getColor(
                    R.styleable.SbisContactDataView_SbisContactDataView_iconColor,
                    Color.MAGENTA
                )
                textSize = getDimension(R.styleable.SbisContactDataView_SbisContactDataView_iconSize, 0f)
            }
        }
    }

    override fun setData(model: SbisContactDataModel) {
        if (currentModel == null) {
            // Устанавливаем значение если, если модель устанавливаем в первый раз
            currentVisibleElements = model.maxNumberVisibleElements
        }
        currentModel = model
        updateMoreButton(model.data)
        view.removeAllViews()
        model.data.forEach {
            val child = createElement()
            child.setData(it)
            view.addView(child)
        }
        updateVisibleChildren()
    }

    private fun updateMoreButton(list: List<SbisContactPhoneNumberModel>) {
        val listSize = list.size
        val moreVisible = listSize > currentVisibleElements
        val countInvisibleElements = listSize - currentVisibleElements
        view.moreText.configure {
            if (moreVisible) {
                text = String.format(
                    view.context.resources.getString(R.string.design_contact_data_more_text),
                    countInvisibleElements
                )
            }
            isVisible = moreVisible
        }
    }

    private fun createElement(): SbisContactPhoneNumberView {
        return contactDataViewFactory.createView(view.context, styleHolder.styleElement).apply {
            clickListener = onClickElementListener
        }
    }

    private fun updateVisibleChildren() {
        repeat(view.childCount) {
            val isVisible = it < currentVisibleElements
            view.getChildAt(it).visibility = isVisible.toVisible()
        }
    }

    private fun Boolean.toVisible(): Int = if (this) View.VISIBLE else View.GONE

    internal fun clickMore() {
        currentVisibleElements += numberOpeningPerStep
        updateVisibleChildren()
        currentModel?.let { updateMoreButton(it.data) }
    }

    companion object {

        /**
         * Дефолтное значение видимых элементов для отображения списка
         */
        internal const val DEFAULT_NUMBER_OF_VISIBLE_ELEMENTS = 2

        /**
         * Количество элементов который будут открываться при нажатии кнопки "еще"
         */
        internal const val DEFAULT_NUMBER_OF_ITEMS_TO_OPEN_PER_STEP = 2
    }
}